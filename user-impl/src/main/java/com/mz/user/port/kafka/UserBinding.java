package com.mz.user.port.kafka;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static com.mz.user.topics.UserTopics.USER_CHANGED;
import static com.mz.user.topics.UserTopics.USER_DOCUMENT;

public interface UserBinding {

  @Output(USER_DOCUMENT)
  MessageChannel userDocumentOut();

  @Output(USER_CHANGED)
  MessageChannel userChangedOut();
}
