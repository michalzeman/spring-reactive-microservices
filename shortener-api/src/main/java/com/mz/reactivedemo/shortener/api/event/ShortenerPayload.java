package com.mz.reactivedemo.shortener.api.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableShortenerPayload.class)
@JsonDeserialize(as = ImmutableShortenerPayload.class)
public interface ShortenerPayload {
  String id();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> userId();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> key();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> url();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> shortUrl();

  Long version();

  static ImmutableShortenerPayload.Builder builder() {
    return ImmutableShortenerPayload.builder();
  }
}
