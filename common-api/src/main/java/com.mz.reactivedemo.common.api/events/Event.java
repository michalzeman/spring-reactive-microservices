package com.mz.reactivedemo.common.api.events;


import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public interface Event extends Serializable {

  @Value.Default
  default String eventId() {
    return UUID.randomUUID().toString();
  }

  @Value.Default
  default Instant eventCreatedAt() {
    return Instant.now();
  }
}
