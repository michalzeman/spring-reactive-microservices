package com.mz.reactivedemo.adapter.persistance.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public class AggregateServiceImpl<D> implements AggregateService<D> {

  protected final Function<D, Mono<D>> updateView;

  protected final Consumer<DomainEvent> publishChangedEvent;

  protected final Consumer<D> publishDocumentMessage;

  protected final AggregateRepository repository;

  protected final AggregateFactory<D> aggregateFactory;

  public AggregateServiceImpl(AggregateRepository repository,
                              AggregateFactory<D> aggregateFactory,
                              Function<D, Mono<D>> updateView,
                              Consumer<DomainEvent> publishChangedEvent,
                              Consumer<D> publishDocumentMessage) {
    this.updateView = updateView;
    this.publishChangedEvent = publishChangedEvent;
    this.publishDocumentMessage = publishDocumentMessage;
    this.repository = repository;
    this.aggregateFactory = aggregateFactory;
  }

  @Override
  public Mono<D> execute(String aggregateId, Command cmd) {
    return repository.execute(aggregateId, cmd, aggregateFactory)
            .flatMap(this::processResult);
  }

  private Mono<D> processResult(CommandResult<D> result) {
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
