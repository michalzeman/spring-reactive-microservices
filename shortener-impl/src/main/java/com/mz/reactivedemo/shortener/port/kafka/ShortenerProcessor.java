package com.mz.reactivedemo.shortener.port.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;

import javax.annotation.PostConstruct;

import static com.mz.reactivedemo.common.KafkaMapper.FN;
import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.*;
import static java.util.Objects.requireNonNull;

/**
 * Created by zemi on 07/10/2018.
 */
@Service
public class ShortenerProcessor {

  private final Logger logger = new Logger(LogFactory.getLog(ShortenerProcessor.class));

  private final ApplicationMessageBus shortenerMessageBus;

  private final KafkaSender<String, String> kafkaSender;

  private final ObjectMapper objectMapper;

  public ShortenerProcessor(
      ApplicationMessageBus shortenerMessageBus,
      KafkaSender<String, String> kafkaSender,
      ObjectMapper objectMapper
  ) {
    this.shortenerMessageBus = requireNonNull(shortenerMessageBus, "shortenerMessageBus is required");
    this.kafkaSender = requireNonNull(kafkaSender, "kafkaSender is required");
    this.objectMapper = requireNonNull(objectMapper, "objectMapper is required");
  }

  @PostConstruct
  private void onInit() {
    logger.debug(() -> "ShortenerProcessor.onInit() ->");
    var events = shortenerMessageBus.events()
        .subscribeOn(Schedulers.parallel());

    var shortenerViewedStream = events
        .filter(event -> event instanceof ShortenerViewed)
        .cast(ShortenerViewed.class)
        .map(FN.mapToRecord(SHORTENER_VIEWED, objectMapper, ShortenerViewed::aggregateId));

    var shortenerChangedStream = events
        .filter(event -> event instanceof ShortenerChangedEvent)
        .cast(ShortenerChangedEvent.class)
        .map(FN.mapToRecord(SHORTENER_CHANGED, objectMapper, ShortenerChangedEvent::aggregateId));

    var shortenerDocumentStream = shortenerMessageBus.documents()
        .subscribeOn(Schedulers.parallel())
        .map(FN.mapToRecord(SHORTENER_DOCUMENT, objectMapper, ShortenerDto::id));

    kafkaSender.send(shortenerChangedStream)
        .doOnError(this::processError)
        .retry()
        .subscribe();

    kafkaSender.send(shortenerViewedStream)
        .doOnError(this::processError)
        .retry()
        .subscribe();

    kafkaSender.send(shortenerDocumentStream)
        .doOnError(this::processError)
        .retry()
        .subscribe();
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }

}
