package com.mz.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.common.http.HttpErrorHandler;
import com.mz.user.domain.aggregate.UserAggregate;
import com.mz.user.dto.UserDto;
import com.mz.user.impl.UserFunctions.PublishUserChangedEvent;
import com.mz.user.impl.UserFunctions.PublishUserDocumentMessage;
import com.mz.user.impl.UserFunctions.UpdateUserView;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.SHORTENER_CHANGED;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class UserApplicationConfiguration {

  @Bean
  public AggregateService<UserDto> aggregateService(
      UpdateUserView updateUserView,
      PublishUserChangedEvent publishUserChanged,
      PublishUserDocumentMessage publishDocumentMessage,
      AggregateRepository aggregateRepository) {
    return AggregateService.of(aggregateRepository,
        AggregateFactory.build(UserAggregate::of, UserAggregate::of),
        updateUserView, publishUserChanged,
        publishDocumentMessage);
  }

  @Bean
  public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
    return RouterFunctions.route()
        .add(RouterFunctions.nest(RequestPredicates.path("/users"),handler.route()))
        .add(RouterFunctions.route(GET("/health/ticks")
            .and(accept(MediaType.APPLICATION_JSON_UTF8)), req -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just("Tick"), String.class))
        )
        .onError(Throwable.class, HttpErrorHandler.FN::onError)
        .build();
  }

  @Bean("kafkaReceiverOptionsShortenerChangedTopic")
  public ReceiverOptions<String, String> kafkaReceiverOptionsShortenerChangedTopic(
      @Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.consumer.group-id}") String consumerGroupId
  ) {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return ReceiverOptions.<String, String>create(consumerProps)
        .subscription(Collections.singleton(SHORTENER_CHANGED));
  }

  @Bean
  public KafkaSender<String, String> kafkaSender(@Value("${kafka.bootstrap.servers}") String bootstrapServers) {
    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    return KafkaSender.create(SenderOptions.<String, String>create(producerProps).maxInFlight(1024));
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.build();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
