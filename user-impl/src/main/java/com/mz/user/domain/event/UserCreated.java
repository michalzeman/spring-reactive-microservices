package com.mz.user.domain.event;

import com.mz.reactivedemo.common.api.events.Event;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface UserCreated extends Event {

  String id();

  String lastName();

  String firstName();

  Long version();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableUserCreated.Builder builder() {
    return ImmutableUserCreated.builder();
  }

}
