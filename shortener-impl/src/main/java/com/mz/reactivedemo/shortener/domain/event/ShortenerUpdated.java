package com.mz.reactivedemo.shortener.domain.event;

import org.immutables.value.Value;

/**
 * Created by zemi on 30/09/2018.
 */
@Value.Immutable
public interface ShortenerUpdated extends ShortenerChanged {

  String shortenerId();

  String url();

  Long version();

  static ImmutableShortenerUpdated.Builder builder() {
    return ImmutableShortenerUpdated.builder();
  }
}
