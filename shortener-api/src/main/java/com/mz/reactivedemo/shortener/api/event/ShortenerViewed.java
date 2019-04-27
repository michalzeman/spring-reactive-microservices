package com.mz.reactivedemo.shortener.api.event;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.common.api.events.Event;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShortenerViewed.class)
@JsonDeserialize(as = ImmutableShortenerViewed.class)
public interface ShortenerViewed extends Event {

  String key();

  Long number();

  static ImmutableShortenerViewed.Builder builder() {
    return ImmutableShortenerViewed.builder();
  }
}
