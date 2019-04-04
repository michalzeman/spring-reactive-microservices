package com.mz.user.message;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Optional;

@Value.Immutable
public interface ContactInfoPayload extends Serializable {

  Optional<String> userId();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableContactInfoPayload.Builder builder() {
    return ImmutableContactInfoPayload.builder();
  }
}
