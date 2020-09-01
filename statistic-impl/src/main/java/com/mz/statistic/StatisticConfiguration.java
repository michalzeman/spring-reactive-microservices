package com.mz.statistic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mz.reactivedemo.common.http.HttpErrorHandler;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.*;
import static com.mz.user.topics.UserTopics.USER_CHANGED;

@Configuration
public class StatisticConfiguration {

  @Bean
  public RouterFunction<ServerResponse> statisticRoute(StatisticHandler handler) {
    return RouterFunctions.route()
        .add(RouterFunctions.nest(RequestPredicates.path("/statistics"),handler.route()))
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

  @Bean("kafkaReceiverOptionsShortenerViewedTopic")
  public ReceiverOptions<String, String> kafkaReceiverOptionsShortenerViewedTopic(
      @Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.consumer.group-id}") String consumerGroupId
  ) {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return ReceiverOptions.<String, String>create(consumerProps)
        .subscription(Collections.singleton(SHORTENER_VIEWED));
  }

  @Bean("kafkaReceiverOptionsShortenerDocumentTopic")
  public ReceiverOptions<String, String> kafkaReceiverOptionsShortenerDocumentTopic(
      @Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.consumer.group-id}") String consumerGroupId
  ) {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return ReceiverOptions.<String, String>create(consumerProps)
        .subscription(Collections.singleton(SHORTENER_DOCUMENT));
  }

  @Bean("kafkaReceiverOptionsUserChangedTopic")
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
