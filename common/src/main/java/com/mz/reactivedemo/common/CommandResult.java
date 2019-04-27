package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface CommandResult<S> {

  enum StatusCode {
    NOT_MODIFIED,
    MODIFIED,
    BAD_COMMAND,
    ERROR
  }

  StatusCode status();

  Optional<Throwable> error();

  Optional<S> state();

  ImmutableList<DomainEvent> domainEvents();

  static <S> CommandResult<S> of(S state, ImmutableList<DomainEvent> domainEvents) {
    return ImmutableCommandResult.<S>builder()
        .state(state)
        .status(domainEvents.size() > 0 ? StatusCode.MODIFIED : StatusCode.NOT_MODIFIED)
        .domainEvents(domainEvents)
        .build();
  }

  static <S> CommandResult<S> error(Throwable error) {
    return ImmutableCommandResult.<S>builder()
        .status(StatusCode.ERROR)
        .domainEvents(Lists.immutable.empty())
        .error(error)
        .build();
  }

  static <S> CommandResult<S> none() {
    return ImmutableCommandResult.<S>builder()
        .status(StatusCode.NOT_MODIFIED)
        .domainEvents(Lists.immutable.empty())
        .build();
  }
}
