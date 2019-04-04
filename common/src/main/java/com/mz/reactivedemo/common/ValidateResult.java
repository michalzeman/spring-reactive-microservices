package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.Event;
import org.eclipse.collections.api.list.ImmutableList;
import org.immutables.value.Value;

@Value.Immutable
public interface ValidateResult {

  ImmutableList<Event> events();

  static ImmutableValidateResult.Builder builder() {
    return ImmutableValidateResult.builder();
  }
}
