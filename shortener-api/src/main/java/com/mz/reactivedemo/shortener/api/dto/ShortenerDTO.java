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
public abstract class ShortenerDTO {

  public abstract Optional<String> id();

  public abstract String key();

  public abstract String url();

  public abstract String shortUrl();

  public abstract Instant createdAt();

  public abstract Optional<Long> version();

}
