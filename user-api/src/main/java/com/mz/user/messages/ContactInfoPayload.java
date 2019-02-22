package com.mz.user.messages;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface ContactInfoPayload {

  Optional<String> userId();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableContactInfoPayload.Builder builder() {
    return ImmutableContactInfoPayload.builder();
  }
}
