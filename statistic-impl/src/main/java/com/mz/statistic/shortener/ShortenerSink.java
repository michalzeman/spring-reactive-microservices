package com.mz.statistic.shortener;

import com.mz.reactivedemo.shortener.api.topics.ShortenerTopics;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

/**
 * Created by zemi on 14/10/2018.
 */
public interface ShortenerSink {

  @Input(ShortenerTopics.SHORTENER_VIEWED)
  KStream<?, ?> shortenerViewed();

  @Input(ShortenerTopics.SHORTENER_DOCUMENT)
  KStream<?, ?> shortenerDocument();

  @Input(ShortenerTopics.SHORTENER_CHANGED)
  KStream<?, ?> shortenerChanged();
}
