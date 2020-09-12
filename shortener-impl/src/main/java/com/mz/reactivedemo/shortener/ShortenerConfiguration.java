package com.mz.reactivedemo.shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.adapter.persistance.document.DocumentReadOnlyRepository;
import com.mz.reactivedemo.adapter.persistance.document.impl.DocumentReadOnlyRepositoryImpl;
import com.mz.reactivedemo.common.http.HttpErrorHandler;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerAggregate;
import com.mz.reactivedemo.shortener.impl.ShortenerFunctions;
import com.mz.user.dto.UserDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.mz.user.topics.UserTopics.USER_CHANGED;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class ShortenerConfiguration {

  public static final String USER_DOCUMENTS_STORAGE = "user-documents-storage";

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

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kStreamsConfigs(
      @Value("${kafka.bootstrap.servers}") String bootstrapServers,
      @Value("${kafka.consumer.group-id}") String consumerGroupId
  ) {
    Map<String, Object> props = new HashMap<>();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, consumerGroupId);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
    return new KafkaStreamsConfiguration(props);
  }

  @Bean
  public Consumer<KStream<String, UserDto>> aggregate() {
    return input -> input
        .map((key, value) -> new KeyValue<>(value.id(), value))
        .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(UserDto.class)))
        .reduce((acc, newValue) -> newValue ,Materialized.<String, UserDto, KeyValueStore<Bytes, byte[]>>as(USER_DOCUMENTS_STORAGE)
            .withKeySerde(Serdes.String())
            .withValueSerde(new JsonSerde<>(UserDto.class)))
        .toStream();
  }

  @Bean
  @Profile("!test")
  public DocumentReadOnlyRepository<String, UserDto>  userDocumentReadOnlyRepository(InteractiveQueryService queryService) {
    return new DocumentReadOnlyRepositoryImpl<>(
        queryService,
        Schedulers.newParallel("UserReadOnlyRepositoryScheduler", 4),
        USER_DOCUMENTS_STORAGE
    );
  }

  @Bean
  @Profile("test")
  public DocumentReadOnlyRepository<String, UserDto>  userDocumentReadOnlyTestRepository(InteractiveQueryService queryService) {
    return key -> Mono.just(UserDto.builder()
        .id(key)
        .firstName("Test")
        .lastName("Test")
        .createdAt(Instant.now())
        .version(1L)
        .build());
  }
}
