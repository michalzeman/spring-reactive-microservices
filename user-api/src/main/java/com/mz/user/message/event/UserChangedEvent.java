package com.mz.user.message.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.message.UserPayload;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUserChangedEvent.class)
@JsonDeserialize(as = ImmutableUserChangedEvent.class)
public interface UserChangedEvent extends Event {

  UserPayload payload();

  UserEventType type();

  static ImmutableUserChangedEvent.Builder builder() {
    return ImmutableUserChangedEvent.builder();
  }
}
