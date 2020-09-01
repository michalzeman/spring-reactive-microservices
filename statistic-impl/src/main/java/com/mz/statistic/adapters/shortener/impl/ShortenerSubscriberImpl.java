package com.mz.statistic.adapters.shortener.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.statistic.adapters.shortener.ShortenerSubscriber;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import javax.annotation.PostConstruct;

import static com.mz.reactivedemo.common.KafkaMapper.FN;
import static java.util.Objects.requireNonNull;

/**
 * Created by zemi on 14/10/2018.
 */
@Component
public class ShortenerSubscriberImpl implements ShortenerSubscriber {

  private final Logger logger = new Logger(LogFactory.getLog(ShortenerSubscriberImpl.class));

  private final ReplayProcessor<ShortenerViewed> events = ReplayProcessor.create(1);

  private final FluxSink<ShortenerViewed> eventSink = events.sink();

  private final ReplayProcessor<ShortenerChangedEvent> changedEvents = ReplayProcessor.create(1);

  private final FluxSink<ShortenerChangedEvent> changedEventsSink = changedEvents.sink();

  private final ReceiverOptions<String, String> shortenerChangedReceiverOptions;

  private final ReceiverOptions<String, String> kafkaReceiverOptionsShortenerDocumentTopic;

  private final ReceiverOptions<String, String> kafkaReceiverOptionsShortenerViewedTopic;

  private final ObjectMapper objectMapper;

  public ShortenerSubscriberImpl(
      @Qualifier("kafkaReceiverOptionsShortenerChangedTopic") ReceiverOptions<String, String> shortenerChangedReceiverOptions,
      @Qualifier("kafkaReceiverOptionsShortenerDocumentTopic") ReceiverOptions<String, String> kafkaReceiverOptionsShortenerDocumentTopic,
      @Qualifier("kafkaReceiverOptionsShortenerViewedTopic") ReceiverOptions<String, String> kafkaReceiverOptionsShortenerViewedTopic,
      ObjectMapper objectMapper
  ) {
    this.shortenerChangedReceiverOptions = requireNonNull(shortenerChangedReceiverOptions, "shortenerChangedReceiverOptions is required");
    this.kafkaReceiverOptionsShortenerDocumentTopic = requireNonNull(kafkaReceiverOptionsShortenerDocumentTopic, "kafkaReceiverOptionsShortenerDocumentTopic is required");
    this.kafkaReceiverOptionsShortenerViewedTopic = requireNonNull(kafkaReceiverOptionsShortenerViewedTopic, "kafkaReceiverOptionsShortenerViewedTopic is required");
    this.objectMapper = requireNonNull(objectMapper, "objectMapper is required");
  }

  @Override
  public Flux<ShortenerViewed> eventsShortenerViewed() {
    return events.publishOn(Schedulers.parallel());
  }

  @Override
  public Flux<ShortenerChangedEvent> shortenerChanged() {
    return changedEvents.publishOn(Schedulers.parallel());
  }

  @PostConstruct
  private void subscribeToTopics() {
    KafkaReceiver.create(shortenerChangedReceiverOptions).receive()
        .map(ConsumerRecord::value)
        .map(FN.mapFromJson(objectMapper, ShortenerChangedEvent.class))
        .retry()
        .subscribe(changedEventsSink::next, this::processError);

    KafkaReceiver.create(kafkaReceiverOptionsShortenerViewedTopic).receive()
        .map(ConsumerRecord::value)
        .map(FN.mapFromJson(objectMapper, ShortenerViewed.class))
        .retry()
        .subscribe(eventSink::next, this::processError);

    KafkaReceiver.create(kafkaReceiverOptionsShortenerDocumentTopic).receive()
        .retry()
        .subscribe(record -> System.out.println(record.value()), this::processError);
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }

}
