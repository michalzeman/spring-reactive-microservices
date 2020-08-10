package com.mz.reactivedemo.shortener.domain.event;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
public interface ShortenerCreated extends ShortenerChanged {

  ShortenerDto shortener();

  static ImmutableShortenerCreated.Builder builder() {
    return ImmutableShortenerCreated.builder();
  }
}
