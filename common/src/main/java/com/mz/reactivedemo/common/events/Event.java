package com.mz.reactivedemo.common.events;


import org.immutables.value.Value;

import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public interface Event {

  @Value.Default
  default String id() {
    return UUID.randomUUID().toString();
  }
}
