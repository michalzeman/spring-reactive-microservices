package com.mz.reactivedemo.shortener.api.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.immutables.value.Value;

/**
 * Created by zemi on 20/10/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShortenerChangedEvent.class)
@JsonDeserialize(as = ImmutableShortenerChangedEvent.class)
public interface ShortenerChangedEvent extends ShortenerEvent {

  ShortenerEventType type();

  ShortenerDto payload();

  static ImmutableShortenerChangedEvent.Builder builder() {
    return ImmutableShortenerChangedEvent.builder();
  }
}
