package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.Event;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/09/2018.
 */
@Value.Immutable
public interface ApplyResult<R> {

  @Value.Default
  default ImmutableSet<Event> events() {
    return Sets.immutable.empty();
  }

  R result();

}
