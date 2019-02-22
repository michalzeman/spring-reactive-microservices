package com.mz.user.domain.aggregate;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

@Value.Immutable
public interface UserState {

  Optional<String> id();

  Optional<String> lastName();

  Optional<String> firstName();

  Instant createdAt();

  Optional<Long> version();

  Optional<ContactInfoState> contactInformation();

  static ImmutableUserState.Builder builder() {
    return ImmutableUserState.builder();
  }

  @Value.Immutable
  interface ContactInfoState {

    Instant createdAt();

    Optional<String> email();

    Optional<String> phoneNumber();

    static ImmutableContactInfoState.Builder builder() {
      return ImmutableContactInfoState.builder();
    }
  }

}
