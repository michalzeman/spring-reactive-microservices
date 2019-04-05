package com.mz.user.adapter.shortener.impl;

import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.topics.ShortenerTopics;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.adapter.shortener.ShortenerAdapter;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(ShortenerSink.class)
public class ShortenerAdapterImpl implements ShortenerAdapter {

  private final UserApplicationMessageBus messageBus;

  public ShortenerAdapterImpl(UserApplicationMessageBus messageBus) {
    this.messageBus = messageBus;
  }

  @StreamListener
  public void process(@Input(ShortenerTopics.SHORTENER_CHANGED) KStream<String, ShortenerChangedEvent> shortenerChanged) {
    shortenerChanged.foreach((k, v) -> this.messageBus.publishEvent(v));
  }
}
