package com.mz.user.messages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableUserPayload.class)
@JsonDeserialize(as = ImmutableUserPayload.class)
public interface UserPayload {

  String id();

  Instant createdAt();

  Long version();

  Optional<String> lastName();

  Optional<String> firstName();

  Optional<ContactInfoPayload> contactInfo();

  static ImmutableUserPayload.Builder builder() {
    return ImmutableUserPayload.builder();
  }
}
