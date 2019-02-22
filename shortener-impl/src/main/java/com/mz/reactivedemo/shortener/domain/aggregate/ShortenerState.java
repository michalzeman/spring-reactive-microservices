package com.mz.reactivedemo.shortener.domain.aggregate;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

@Value.Immutable
public interface ShortenerState {
  Optional<String> id();

  String key();

  String url();

  String shortUrl();

  Instant createdAt();

  Optional<Long> version();

  static ImmutableShortenerState.Builder builder() {
    return ImmutableShortenerState.builder();
  }
}
