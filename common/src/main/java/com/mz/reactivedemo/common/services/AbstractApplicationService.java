package com.mz.reactivedemo.common.services;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by zemi on 2019-01-19.
 * E - DB entity type
 * D - DTO type
 * M - changed event massage type published to out
 * R - repository type
 */
public abstract class AbstractApplicationService<D, S, M extends Event> {

  protected final Supplier<Predicate<Optional<ApplyResult<S>>>> isApplyResultPresent = () -> Optional::isPresent;

  abstract protected Optional<M> mapToChangedEvent(Event event, D dto);

  abstract protected void publishChangedEvent(Event event);

  abstract protected void publishDocumentMessage(D doc);

  abstract protected Mono<D> applyToStorage(ApplyResult<S> result);

  protected Mono<D> processChanges(Mono<Optional<ApplyResult<S>>> result) {
    return result
        .filter(isApplyResultPresent.get())
        .map(Optional::get)
        .flatMap(r -> applyToStorage(r)
            .doOnSuccess(d -> r.event().ifPresent(e -> mapToChangedEvent(e, d).ifPresent(this::publishChangedEvent))))
        .doOnSuccess(this::publishDocumentMessage);
  }

}
