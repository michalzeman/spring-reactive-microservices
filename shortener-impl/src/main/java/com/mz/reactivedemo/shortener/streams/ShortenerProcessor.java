package com.mz.reactivedemo.shortener.streams;

import com.mz.reactivedemo.common.events.Event;
import com.mz.reactivedemo.common.utils.Logger;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.events.ShortenerViewed;
import com.mz.reactivedemo.shortener.impl.ShortenerServiceImpl;
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

  private Logger logger = new Logger(LogFactory.getLog(ShortenerServiceImpl.class));

  private final ShortenerService shortenerService;

  private final ShortenerBinding shortenerDinding;

  public ShortenerProcessor(ShortenerService shortenerService, ShortenerBinding shortenerDinding) {
    this.shortenerService = shortenerService;
    this.shortenerDinding = shortenerDinding;
  }

  @PostConstruct
  private void onInit() {
    logger.debug(() -> "ShortenerProcessor.onInit() ->");
    shortenerService.events()
        .subscribeOn(Schedulers.parallel())
        .subscribe(this::processEvent, this::processError);
    shortenerService.documents()
        .subscribeOn(Schedulers.parallel())
        .subscribe(this::processDocument, this::processError);
  }

  private void processEvent(Event event) {
    if (event instanceof ShortenerViewed) {
      ShortenerViewed payload = (ShortenerViewed) event;
      MessageChannel messageChannel = shortenerDinding.shortenerViewed();
      messageChannel.send(MessageBuilder
          .withPayload(payload)
          .setHeader(KafkaHeaders.MESSAGE_KEY, payload.id().getBytes())
          .build());
    } else if (event instanceof ShortenerChangedEvent) {
      ShortenerChangedEvent payload = (ShortenerChangedEvent)event;
      shortenerDinding.shortenerChangedOut()
          .send(MessageBuilder
              .withPayload(payload)
              .setHeader(KafkaHeaders.MESSAGE_KEY, event.id().getBytes())
              .build());
    }
  }

  private void processDocument(ShortenerDTO doc) {
    shortenerDinding.shortenerDocumentOut()
        .send(MessageBuilder
            .withPayload(doc)
            .setHeader(KafkaHeaders.MESSAGE_KEY, doc.id().get().getBytes())
            .build());
  }

  private void processError(Throwable error) {
    logger.log().error(error);
  }

}
