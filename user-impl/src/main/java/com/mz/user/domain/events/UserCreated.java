package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.UserDto;
import java.util.Optional;

import com.mz.user.messages.CreateUser;
import org.immutables.value.Value;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface UserCreated extends Event {

  Optional<String> lastName();

  Optional<String> firstName();

  Optional<Long> version();

//  Optional<CreateUser.ContactInfo> contactInfoCreated();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableUserCreated.Builder builder() {
    return ImmutableUserCreated.builder();
  }

}
