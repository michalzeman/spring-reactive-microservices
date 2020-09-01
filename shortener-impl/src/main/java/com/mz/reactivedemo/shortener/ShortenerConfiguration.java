package com.mz.reactivedemo.shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.common.http.HttpErrorHandler;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerAggregate;
import com.mz.reactivedemo.shortener.impl.ShortenerFunctions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mz.user.topics.UserTopics.USER_CHANGED;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class ShortenerConfiguration {

  @Bean
  public AggregateService<ShortenerDto> aggregateService(
      ShortenerFunctions.UpdateView updateView,
      ShortenerFunctions.PublishChangedEvent publishChanged,
      ShortenerFunctions.PublishDocumentMessage publishDocumentMessage,
      AggregateRepository aggregateRepository) {
    return AggregateService.of(aggregateRepository,
        AggregateFactory.build(ShortenerAggregate::of, ShortenerAggregate::of),
        updateView, publishChanged,
        publishDocumentMessage);
  }

  @Bean
  public RouterFunction<ServerResponse> shortenerRoute(ShortenerHandler handler) {
    return RouterFunctions.route()
        .add(RouterFunctions.nest(RequestPredicates.path("/shorteners"), handler.route()))
        .onError(Throwable.class, HttpErrorHandler.FN::onError)
        .build();
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
  public ReceiverOptions<String, String> kafkaReceiverOptionsUserChangedTopic(
      @Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.consumer.group-id}") String consumerGroupId
  ) {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return ReceiverOptions.<String, String>create(consumerProps)
        .subscription(Collections.singleton(USER_CHANGED));
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.build();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
