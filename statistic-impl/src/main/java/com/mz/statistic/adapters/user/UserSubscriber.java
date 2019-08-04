package com.mz.statistic.adapters.user;

import com.mz.user.message.event.UserChangedEvent;
import reactor.core.publisher.Flux;

public interface UserSubscriber {
  Flux<UserChangedEvent> userChanged();
}
