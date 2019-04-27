package com.mz.user.domain.event;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import org.immutables.value.Value;

@Value.Immutable
public interface ShortenerAdded extends DomainEvent {

  String shortenerId();

  Long userVersion();

  static ImmutableShortenerAdded.Builder builder() {
    return ImmutableShortenerAdded.builder();
  }
}
