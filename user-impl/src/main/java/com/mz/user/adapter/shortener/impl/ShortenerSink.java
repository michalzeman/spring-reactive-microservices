package com.mz.user.adapter.shortener.impl;

import com.mz.reactivedemo.shortener.api.topics.ShortenerTopics;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface ShortenerSink {
  @Input(ShortenerTopics.SHORTENER_CHANGED)
  KStream<?, ?> shortenerChanged();
}
