package com.mz.reactivedemo.shortener.port.kafka;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.utils.Logger;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

/**
 * Created by zemi on 07/10/2018.
 */
@EnableBinding(ShortenerBinding.class)
@Service
public class ShortenerProcessor {

  private Logger logger = new Logger(LogFactory.getLog(ShortenerProcessor.class));

  private final ApplicationMessageBus shortenerMessageBus;

  private final ShortenerBinding shortenerBinding;

  public ShortenerProcessor(ApplicationMessageBus shortenerMessageBus, ShortenerBinding shortenerBinding) {
    this.shortenerMessageBus = shortenerMessageBus;
    this.shortenerBinding = shortenerBinding;
  }

  @PostConstruct
  private void onInit() {
    logger.debug(() -> "ShortenerProcessor.onInit() ->");
    shortenerMessageBus.events()
        .subscribeOn(Schedulers.parallel())
        .doOnError(this::processError)
        .retry()
        .subscribe(this::processEvent, this::processError);
    shortenerMessageBus.documents()
        .subscribeOn(Schedulers.parallel())
        .doOnError(this::processError)
        .retry()
        .subscribe(this::processDocument, this::processError);
  }

  private void processEvent(Event event) {
    if (event instanceof ShortenerViewed) {
      ShortenerViewed payload = (ShortenerViewed) event;
      MessageChannel messageChannel = shortenerBinding.shortenerViewed();
      messageChannel.send(MessageBuilder
          .withPayload(payload)
          .setHeader(KafkaHeaders.MESSAGE_KEY, payload.eventId().getBytes())
          .build());
    } else if (event instanceof ShortenerChangedEvent) {
      ShortenerChangedEvent payload = (ShortenerChangedEvent)event;
      shortenerBinding.shortenerChangedOut()
          .send(MessageBuilder
              .withPayload(payload)
              .setHeader(KafkaHeaders.MESSAGE_KEY, payload.aggregateId().getBytes())
              .build());
    }
  }

  private void processDocument(ShortenerDto doc) {
    shortenerBinding.shortenerDocumentOut()
        .send(MessageBuilder
            .withPayload(doc)
            .setHeader(KafkaHeaders.MESSAGE_KEY, doc.id().getBytes())
            .build());
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }

}
