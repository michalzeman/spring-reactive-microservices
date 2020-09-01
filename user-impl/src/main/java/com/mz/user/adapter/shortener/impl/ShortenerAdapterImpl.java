package com.mz.user.adapter.shortener.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.UserApplicationService;
import com.mz.user.adapter.shortener.ShortenerAdapter;
import com.mz.user.domain.command.AddShortener;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import javax.annotation.PostConstruct;

import static com.mz.reactivedemo.common.KafkaMapper.FN;
import static java.util.Objects.requireNonNull;

@Component
public class ShortenerAdapterImpl implements ShortenerAdapter {

  private final Logger logger = new Logger(LogFactory.getLog(ShortenerAdapterImpl.class));

  private final UserApplicationMessageBus messageBus;

  private final UserApplicationService userApplicationService;

  private final ReceiverOptions<String, String> kafkaReceiverOptionsShortenerChangedTopic;

  private final ObjectMapper objectMapper;

  public ShortenerAdapterImpl(
      UserApplicationMessageBus messageBus,
      UserApplicationService userApplicationService,
      @Qualifier("kafkaReceiverOptionsShortenerChangedTopic") ReceiverOptions<String, String> kafkaReceiverOptionsShortenerChangedTopic,
      ObjectMapper objectMapper
  ) {
    this.messageBus = requireNonNull(messageBus, "messageBus is required");
    this.userApplicationService = requireNonNull(userApplicationService, "userApplicationService is required");
    this.kafkaReceiverOptionsShortenerChangedTopic = requireNonNull(kafkaReceiverOptionsShortenerChangedTopic, "kafkaReceiverOptionsShortenerChangedTopic is required");
    this.objectMapper = requireNonNull(objectMapper, "objectMapper is required");
  }

  @PostConstruct
  private void subscribeToTopics() {
    KafkaReceiver.create(kafkaReceiverOptionsShortenerChangedTopic).receive()
        .map(ConsumerRecord::value)
        .map(FN.mapFromJson(objectMapper, ShortenerChangedEvent.class))
        .filter(event -> ShortenerEventType.CREATED.equals(event.type()))
        .retry()
        .subscribe(this::processShortenerChanged, this::processError);
  }

  private void processShortenerChanged(ShortenerChangedEvent changedEvent) {
      logger.debug(() -> "shortenerChanged ->");
      AddShortener addShortener = AddShortener.builder()
          .shortenerId(changedEvent.payload().id())
          .userId(changedEvent.payload().userId().get())
          .build();
      userApplicationService.execute(addShortener).subscribe();
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }
}
