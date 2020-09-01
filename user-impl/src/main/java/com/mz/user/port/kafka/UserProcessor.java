package com.mz.user.port.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.dto.UserDto;
import com.mz.user.message.event.UserChangedEvent;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;

import static com.mz.reactivedemo.common.KafkaMapper.FN;
import static com.mz.user.topics.UserTopics.USER_CHANGED;
import static com.mz.user.topics.UserTopics.USER_DOCUMENT;
import static java.util.Objects.requireNonNull;

@Service
public class UserProcessor {

  private final Logger logger = new Logger(LogFactory.getLog(UserProcessor.class));

  private final UserApplicationMessageBus messageBus;

  private final KafkaSender<String, String> kafkaSender;

  private final ObjectMapper objectMapper;

  public UserProcessor(
      UserApplicationMessageBus messageBus,
      KafkaSender<String, String> kafkaSender,
      ObjectMapper objectMapper
  ) {
    this.messageBus = requireNonNull(messageBus, "messageBus is required");
    this.kafkaSender = requireNonNull(kafkaSender, "kafkaSender is required");
    this.objectMapper = requireNonNull(objectMapper, "objectMapper is required");
  }

  @PostConstruct
  void onInit() {
    logger.debug(() -> "UserProcessor.onInit() ->");
    var userChangedStream = messageBus.events()
        .subscribeOn(Schedulers.parallel())
        .filter(event -> event instanceof UserChangedEvent)
        .cast(UserChangedEvent.class)
        .map(FN.mapToRecord(USER_CHANGED, objectMapper, UserChangedEvent::aggregateId));

    var userDocumentStream = messageBus.documents()
        .subscribeOn(Schedulers.parallel())
        .map(this::mapUserDocumentSenderRecord);

    kafkaSender.send(userChangedStream)
        .doOnError(this::processError)
        .retry()
        .subscribe();

    kafkaSender.send(userDocumentStream)
        .doOnError(this::processError)
        .retry()
        .subscribe();
  }

  private void processError(Throwable throwable) {
    logger.log().error(throwable);
  }

  private SenderRecord<String, String, UserDto> mapUserDocumentSenderRecord(UserDto document) {
    try {
      final var producerRecord = new ProducerRecord<>(
          USER_DOCUMENT, document.id(),
          objectMapper.writeValueAsString(document)
      );
      return SenderRecord.create(producerRecord, document);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
