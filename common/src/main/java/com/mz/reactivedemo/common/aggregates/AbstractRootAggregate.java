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

  protected abstract Optional<Event> behavior(Command cmd);

  protected abstract R toResult();

  public Optional<ApplyResult<R>> apply(Command cmd) {
    return Optional.ofNullable(cmd)
        .map(c -> ImmutableApplyResult.<R>builder().event(behavior(c)).result(toResult()).build());
  }
}
