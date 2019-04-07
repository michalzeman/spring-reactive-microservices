package com.mz.user.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableUserPayload.class)
@JsonDeserialize(as = ImmutableUserPayload.class)
public interface UserPayload extends Serializable {

  String id();

  Instant createdAt();

  Long version();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> shortenerId();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> lastName();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> firstName();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<ContactInfoPayload> contactInfo();

  static ImmutableUserPayload.Builder builder() {
    return ImmutableUserPayload.builder();
  }
}
