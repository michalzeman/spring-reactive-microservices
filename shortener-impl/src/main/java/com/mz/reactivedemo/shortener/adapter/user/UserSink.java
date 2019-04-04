package com.mz.reactivedemo.shortener.adapter.user;

import com.mz.user.topics.UserTopics;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface UserSink {

  @Input(UserTopics.USER_CHANGED)
  KStream<?, ?> userChanged();
}
