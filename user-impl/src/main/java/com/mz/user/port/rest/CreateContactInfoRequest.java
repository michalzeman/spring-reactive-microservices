package com.mz.user.port.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableCreateContactInfoRequest.class)
@JsonDeserialize(as = ImmutableCreateContactInfoRequest.class)
public interface CreateContactInfoRequest {


  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableCreateContactInfoRequest.Builder builder() {
    return ImmutableCreateContactInfoRequest.builder();
  }
}
