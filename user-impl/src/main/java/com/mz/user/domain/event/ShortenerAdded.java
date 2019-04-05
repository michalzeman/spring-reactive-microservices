package com.mz.user.domain.event;

import com.mz.reactivedemo.common.api.events.Event;
import org.immutables.value.Value;

@Value.Immutable
public interface ShortenerAdded extends Event {
  String userId();

  String shortenerId();

  Long userVersion();

  static ImmutableShortenerAdded.Builder builder() {
    return ImmutableShortenerAdded.builder();
  }
}
