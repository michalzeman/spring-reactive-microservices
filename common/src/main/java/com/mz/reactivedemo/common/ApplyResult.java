package com.mz.reactivedemo.common;

import com.mz.reactivedemo.common.api.events.Event;
import java.util.Optional;

import org.immutables.value.Value;

/**
 * Created by zemi on 29/09/2018.
 */
@Value.Immutable
public interface ApplyResult<R> {

  Optional<String> rootEntityId();

  Optional<Event> event();

  R result();

}
