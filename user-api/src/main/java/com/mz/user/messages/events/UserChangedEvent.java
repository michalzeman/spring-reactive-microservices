package com.mz.user.messages.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.messages.UserPayload;
import org.immutables.value.Value;

@Value.Immutable
public interface UserChangedEvent extends Event {

  UserPayload payload();

  UserEventType type();

  static ImmutableUserChangedEvent.Builder builder() {
    return ImmutableUserChangedEvent.builder();
  }
}
