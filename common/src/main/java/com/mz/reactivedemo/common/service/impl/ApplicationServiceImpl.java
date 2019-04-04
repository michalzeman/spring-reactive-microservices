package com.mz.reactivedemo.common.service.impl;

import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.service.ApplicationService;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public class ApplicationServiceImpl<D> implements ApplicationService<D> {

  protected final Function<D, Mono<D>> updateView;

  protected final Consumer<Event> publishChangedEvent;

  protected final Consumer<D> publishDocumentMessage;

  public ApplicationServiceImpl(Function<D, Mono<D>> updateView, Consumer<Event> publishChangedEvent,
                                Consumer<D> publishDocumentMessage) {
    this.updateView = updateView;
    this.publishChangedEvent = publishChangedEvent;
    this.publishDocumentMessage = publishDocumentMessage;
  }

  public Mono<D> processResult(CommandResult<D> result) {
    switch (result.status()) {
      case MODIFIED:
        if (result.state().isPresent()) {
          return updateView.apply(result.state().get())
              .doOnSuccess(s -> result.domainEvents().forEach(publishChangedEvent))
              .doOnSuccess(publishDocumentMessage);
        } else {
          return Mono.empty();
        }
      case ERROR:
        return Mono.error(result.error().orElseGet(() -> new RuntimeException("Generic error")));
      case NOT_MODIFIED:
      default:
        return Mono.empty();
    }
  }

}
