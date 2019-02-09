package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.UserDto;
import org.immutables.value.Value;

@Value.Immutable
public interface UserChangedEvent extends Event {

  UserDto payload();

  UserEventType type();

  static ImmutableUserChangedEvent.Builder builder() {
    return ImmutableUserChangedEvent.builder();
  }
}
