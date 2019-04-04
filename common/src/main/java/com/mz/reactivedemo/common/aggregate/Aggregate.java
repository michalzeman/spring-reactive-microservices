package com.mz.reactivedemo.common.aggregate;

import com.mz.reactivedemo.common.ValidateResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.Try;

public interface Aggregate<S> {

  Aggregate<S> apply(Event event);

  Try<ValidateResult> validate(Command cmd);

  S state();
}
