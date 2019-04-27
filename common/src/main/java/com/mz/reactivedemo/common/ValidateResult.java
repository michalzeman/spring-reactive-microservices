package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import org.eclipse.collections.api.list.ImmutableList;
import org.immutables.value.Value;

@Value.Immutable
public interface ValidateResult {

  ImmutableList<DomainEvent> events();

  static ImmutableValidateResult.Builder builder() {
    return ImmutableValidateResult.builder();
  }
}
