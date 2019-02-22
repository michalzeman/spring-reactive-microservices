package com.mz.reactivedemo.shortener.api.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableShortenerPayload.class)
@JsonDeserialize(as = ImmutableShortenerPayload.class)
public interface ShortenerPayload {
  String id();

  Optional<String> key();

  Optional<String> url();

  Optional<String> shortUrl();

  Long version();

  static ImmutableShortenerPayload.Builder builder() {
    return ImmutableShortenerPayload.builder();
  }
}
