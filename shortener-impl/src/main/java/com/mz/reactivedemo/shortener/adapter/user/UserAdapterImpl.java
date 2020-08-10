package com.mz.reactivedemo.shortener.adapter.user;

import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.topics.UserTopics;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(UserSink.class)
public class UserAdapterImpl {

  @StreamListener
  public void process(
      @Input(UserTopics.USER_CHANGED) KStream<String, UserChangedEvent> userChanged) {
    //TODO: add impl.
    userChanged.foreach((k, v) -> System.out.println(v));
  }

}
