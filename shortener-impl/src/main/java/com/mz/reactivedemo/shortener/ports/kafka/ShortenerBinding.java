package com.mz.reactivedemo.shortener.ports.kafka;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.*;

/**
 * Created by zemi on 12/10/2018.
 */
public interface ShortenerBinding {

  @Output(SHORTENER_VIEWED)
  MessageChannel shortenerViewed();

  @Output(SHORTENER_DOCUMENT)
  MessageChannel shortenerDocumentOut();

  @Output(SHORTENER_CHANGED)
  MessageChannel shortenerChangedOut();

}
