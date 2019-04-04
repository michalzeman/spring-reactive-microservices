package com.mz.reactivedemo.common.service;

import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.service.impl.ApplicationServiceImpl;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ApplicationService<D> {

  Mono<D> processResult(CommandResult<D> result);

  static <D> ApplicationService of(Function<D, Mono<D>> updateView, Consumer<Event> publishChangedEvent,
                               Consumer<D> publishDocumentMessage) {
    return new ApplicationServiceImpl(updateView, publishChangedEvent, publishDocumentMessage);
  }
}
