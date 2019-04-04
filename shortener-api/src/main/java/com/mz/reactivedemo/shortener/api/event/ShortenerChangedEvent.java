package com.mz.reactivedemo.shortener.api.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created by zemi on 20/10/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableShortenerChangedEvent.class)
@JsonDeserialize(as = ImmutableShortenerChangedEvent.class)
public interface ShortenerChangedEvent extends ShortenerEvent {

  ShortenerEventType type();

  ShortenerPayload payload();

  static ImmutableShortenerChangedEvent.Builder builder() {
    return ImmutableShortenerChangedEvent.builder();
  }
}
