package com.mz.reactivedemo.shortener.api.event;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShortenerViewed.class)
@JsonDeserialize(as = ImmutableShortenerViewed.class)
public interface ShortenerViewed extends ShortenerEvent {

  String key();

  Long number();

  static ImmutableShortenerViewed.Builder builder() {
    return ImmutableShortenerViewed.builder();
  }
}
