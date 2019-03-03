package com.mz.reactivedemo.common.aggregates;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.ApplyResultState;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.Try;

import java.util.Optional;

/**
 * Created by zemi on 04/01/2019.
 */
public abstract class AbstractRootAggregate<R> {

  protected abstract Optional<Event> behavior(Command cmd);

  protected abstract R toResult();

  protected abstract Optional<String> getRootEntityId();

  public Try<ApplyResult<R>> apply(Command cmd) {
    return Try.of(() -> behavior(cmd))
        .map(event -> ApplyResult.<R>builder()
            .event(event)
            .result(toResult())
            .rootEntityId(getRootEntityId())
            .state(event.isPresent() ? ApplyResultState.CHANGED : ApplyResultState.NONE)
            .build());
  }
}
