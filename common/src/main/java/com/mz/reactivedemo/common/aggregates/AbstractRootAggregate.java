package com.mz.reactivedemo.common.aggregates;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.ImmutableApplyResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import org.eclipse.collections.api.set.ImmutableSet;

import java.util.Optional;

/**
 * Created by zemi on 04/01/2019.
 */
public abstract class AbstractRootAggregate<R> {

  protected abstract ImmutableSet<Event> behavior(Command cmd);

  protected abstract R toResult();

  public Optional<ApplyResult<R>> apply(Command cmd) {
    return Optional.ofNullable(cmd)
        .map(c -> {
          ImmutableSet<Event> events = behavior(c);
          return ImmutableApplyResult.<R>builder().events(events).result(toResult()).build();
        });
  }
}
