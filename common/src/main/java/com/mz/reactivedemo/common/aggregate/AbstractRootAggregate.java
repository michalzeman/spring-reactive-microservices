package com.mz.reactivedemo.common.aggregate;

import com.mz.reactivedemo.common.ValidateResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.Try;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * Created by zemi on 04/01/2019.
 */
public abstract class AbstractRootAggregate<S> implements Aggregate<S> {

  protected AggregateStatus status;

  protected abstract ImmutableList<Event> behavior(Command cmd);

  protected abstract String getRootEntityId();

  public abstract Aggregate<S> apply(Event event);

  @Override
  public Try<ValidateResult> validate(Command cmd) {
    return Try.of(() -> behavior(cmd))
        .map(events -> ValidateResult.builder()
            .events(events)
            .build());
  }
}
