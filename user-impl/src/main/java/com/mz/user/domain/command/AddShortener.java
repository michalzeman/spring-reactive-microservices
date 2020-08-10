package com.mz.user.domain.command;

import com.mz.reactivedemo.common.api.events.Command;
import org.immutables.value.Value;

@Value.Immutable
public interface AddShortener extends Command {

  String userId();

  String shortenerId();

  static ImmutableAddShortener.Builder builder() {
    return ImmutableAddShortener.builder();
  }
}
