package com.mz.statistic.adapters.user.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.reactivedemo.common.KafkaMapper;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.statistic.adapters.user.UserSubscriber;
import com.mz.user.message.event.UserChangedEvent;
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

import static java.util.Objects.requireNonNull;

@Component
public class UserSubscriberImpl implements UserSubscriber {

  private final Logger logger = new Logger(LogFactory.getLog(UserSubscriberImpl.class));

  private final ReplayProcessor<UserChangedEvent> changedEvents = ReplayProcessor.create(1);

  private final FluxSink<UserChangedEvent> changedEventsSink = changedEvents.sink();

  private final ReceiverOptions<String, String> kafkaReceiverOptionsUserChangedTopic;

  private final ObjectMapper objectMapper;

  public UserSubscriberImpl(
      @Qualifier("kafkaReceiverOptionsUserChangedTopic") ReceiverOptions<String, String> kafkaReceiverOptionsUserChangedTopic,
      ObjectMapper objectMapper) {
    this.kafkaReceiverOptionsUserChangedTopic = requireNonNull(kafkaReceiverOptionsUserChangedTopic, "kafkaReceiverOptionsUserChangedTopic is required");
    this.objectMapper = requireNonNull(objectMapper, "objectMapper is required");
  }

  @PostConstruct
  private void subscribeToTopic() {
    KafkaReceiver.create(kafkaReceiverOptionsUserChangedTopic).receive()
        .map(ConsumerRecord::value)
        .map(KafkaMapper.FN.mapFromJson(objectMapper, UserChangedEvent.class))
        .retry()
        .subscribe(changedEventsSink::next, this::processError);
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }

  @Override
  public Flux<UserChangedEvent> userChanged() {
    return changedEvents.publishOn(Schedulers.parallel());
  }

}
