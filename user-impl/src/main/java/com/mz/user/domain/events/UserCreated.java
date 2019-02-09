package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.UserDto;
import org.immutables.value.Value;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface UserCreated extends Event {

  UserDto user();

  static ImmutableUserCreated.Builder builder() {
    return ImmutableUserCreated.builder();
  }

}
