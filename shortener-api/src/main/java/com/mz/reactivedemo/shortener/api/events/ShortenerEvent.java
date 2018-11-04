package com.mz.reactivedemo.shortener.api.events;

import com.mz.reactivedemo.common.events.Event;
import org.immutables.value.Value;

import java.time.Instant;

/**
 * Created by zemi on 29/05/2018.
 */
public interface ShortenerEvent extends Event {

  @Value.Default
  default Instant createdAt() {
    return Instant.now();
  }

}
