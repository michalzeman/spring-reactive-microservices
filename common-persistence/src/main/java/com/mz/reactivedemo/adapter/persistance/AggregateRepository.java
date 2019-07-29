package com.mz.reactivedemo.adapter.persistance;

import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;
import reactor.core.publisher.Mono;

public interface AggregateRepository {

  <S> Mono<CommandResult<S>> execute(String aggregateId, Command cmd, AggregateFactory<S> aggregateFactory);

}
