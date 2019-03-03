package com.mz.reactivedemo.common.services;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.Try;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Created by zemi on 2019-01-19.
 * E - DB entity type
 * D - DTO type
 * M - changed event massage type published to out
 * R - repository type
 */
public abstract class AbstractApplicationService<D, S, M extends Event> {

  abstract protected Optional<M> mapToChangedEvent(Event event, D dto);

  abstract protected void publishChangedEvent(Event event);

  abstract protected void publishDocumentMessage(D doc);

  abstract protected Mono<D> applyToStorage(ApplyResult<S> result);

  protected Mono<D> processChanges(Mono<Try<ApplyResult<S>>> result) {
    return result
        .map(t -> t.get())
        .filter(ApplyResult::isChanged)
        .flatMap(r -> applyToStorage(r)
            .doOnSuccess(d -> r.event().ifPresent(e -> mapToChangedEvent(e, d).ifPresent(this::publishChangedEvent))))
        .doOnSuccess(this::publishDocumentMessage);
  }

}
