package com.mz.statistic.adapters.user.impl;

import com.mz.statistic.adapters.user.UserSink;
import com.mz.statistic.adapters.user.UserSubscriber;
import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.topics.UserTopics;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

@Component
@EnableBinding(UserSink.class)
public class UserSubscriberImpl implements UserSubscriber {

  private final ReplayProcessor<UserChangedEvent> changedEvents = ReplayProcessor.create(1);

  private final FluxSink<UserChangedEvent> changedEventsSink = changedEvents.sink();

  @StreamListener
  public void processUserStream(@Input(UserTopics.USER_CHANGED) KStream<String, UserChangedEvent> userChangedStream) {
    userChangedStream.foreach((id, event) -> changedEventsSink.next(event));
  }

  @Override
  public Flux<UserChangedEvent> userChanged() {
    return changedEvents.publishOn(Schedulers.parallel());
  }

}
