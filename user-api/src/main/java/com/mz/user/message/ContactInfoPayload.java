package com.mz.user.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableContactInfoPayload.class)
@JsonDeserialize(as = ImmutableContactInfoPayload.class)
public interface ContactInfoPayload extends Serializable {

  Optional<String> userId();

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableContactInfoPayload.Builder builder() {
    return ImmutableContactInfoPayload.builder();
  }
}
