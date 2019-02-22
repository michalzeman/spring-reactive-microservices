package com.mz.reactivedemo.shortener.domain.events;

import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerState;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
public interface ShortenerCreated extends ShortenerChanged {

  ShortenerState shortener();

  static ImmutableShortenerCreated.Builder builder() {
    return ImmutableShortenerCreated.builder();
  }
}
