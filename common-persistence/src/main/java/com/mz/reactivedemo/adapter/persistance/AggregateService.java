package com.mz.reactivedemo.adapter.persistance;

import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.adapter.persistance.impl.AggregateServiceImpl;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public interface AggregateService<D> {

  Mono<D> execute(String aggregateId, Command cmd);

  static <D> AggregateService<D> of(AggregateRepository repository,
                                    AggregateFactory<D> aggregateFactory,
                                    Function<D, Mono<D>> updateView,
                                    Consumer<DomainEvent> publishChangedEvent,
                                    Consumer<D> publishDocumentMessage) {
    return new AggregateServiceImpl<>(repository, aggregateFactory, updateView, publishChangedEvent,
            publishDocumentMessage);
  }
}
