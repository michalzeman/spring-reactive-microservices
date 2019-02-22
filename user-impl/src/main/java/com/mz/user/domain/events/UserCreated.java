package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;

import java.util.Optional;

import com.mz.user.messages.ContactInfoPayload;
import com.mz.user.messages.UserPayload;
import org.immutables.value.Value;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface UserCreated extends Event {

  Optional<String> lastName();

  Optional<String> firstName();

  Optional<Long> version();

  Optional<ContactInfoPayload> contactInfoCreated();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableUserCreated.Builder builder() {
    return ImmutableUserCreated.builder();
  }

}
