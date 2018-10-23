package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.events.Event;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zemi on 29/09/2018.
 */
@Value.Immutable
public interface ApplyResult<R> {

  @Value.Default
  default Set<Event> events() {
    return new HashSet<Event>();
  }

  R result();

}
