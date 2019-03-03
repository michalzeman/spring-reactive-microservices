package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.Event;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Created by zemi on 29/09/2018.
 */
@Value.Immutable
public interface ApplyResult<R> {

  @Value.Default
  default ApplyResultState state() {
    return ApplyResultState.NONE;
  }

  default boolean isChanged() {
    switch (state()) {
      case CHANGED:
        return true;
      case NONE:
        break;
      default:
        return false;
    }
    return false;
  }

  Optional<String> rootEntityId();

  Optional<Event> event();

  R result();

  static <R> ImmutableApplyResult.Builder<R> builder() {
    return ImmutableApplyResult.<R>builder();
  }
}
