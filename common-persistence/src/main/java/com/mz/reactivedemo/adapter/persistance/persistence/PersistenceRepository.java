package com.mz.reactivedemo.adapter.persistance.persistence;

import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;
import reactor.core.publisher.Mono;

public interface PersistenceRepository {

  <S> Mono<CommandResult<S>> create(String aggregateId, Command cmd, AggregateFactory<S> aggregateFactory);

  <S> Mono<CommandResult<S>> update(String aggregateId, Command cmd);

}
