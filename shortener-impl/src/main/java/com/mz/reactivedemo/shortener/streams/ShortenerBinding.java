package com.mz.reactivedemo.shortener.streams;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.SHORTENER_CHANGED;
import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.SHORTENER_DOCUMENT;
import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.SHORTENER_VIEWED;

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
