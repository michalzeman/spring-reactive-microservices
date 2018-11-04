package com.mz.reactivedemo.shortener.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 07/10/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShortenerDTO.class)
@JsonDeserialize(as = ImmutableShortenerDTO.class)
public interface ShortenerDTO {

  Optional<String> id();

  String key();

  String url();

  String shortUrl();

  Instant createdAt();

  Optional<Long> version();

  static ImmutableShortenerDTO.Builder builder() {
    return ImmutableShortenerDTO.builder();
  }

}
