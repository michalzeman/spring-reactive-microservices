package com.mz.reactivedemo.adapter.persistance.persistence.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mz.reactivedemo.adapter.persistance.persistence.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.adapter.persistance.persistence.RepositoryActor;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

public class PersistenceRepositoryImpl implements PersistenceRepository {

  private static final Log log = LogFactory.getLog(PersistenceRepositoryImpl.class);

  private final ActorSystem system;

  private final ActorRef repositoryActor;

  public PersistenceRepositoryImpl(ActorSystem system) {
    this.system = system;
    this.repositoryActor = system.actorOf(RepositoryActor.props());
  }

  @Override
  public <S> Mono<CommandResult<S>> create(String aggregateId, Command cmd,
                                                  AggregateFactory<S> aggregateFactory) {
    CompletableFuture<Object> future = ask(repositoryActor, new RepositoryActor.CreateCommandMsg(cmd, aggregateId, aggregateFactory),
        Duration.ofMillis(5000)).toCompletableFuture();
    return Mono.fromCompletionStage(future)
        .publishOn(Schedulers.elastic())
        .map(r -> (CommandResult<S>) r);
  }

  @Override
  public <S> Mono<CommandResult<S>> update(String aggregateId, Command cmd) {
    CompletableFuture<Object> future = ask(repositoryActor, new RepositoryActor.UpdateCommandMsg(cmd, aggregateId),
        Duration.ofMillis(5000)).toCompletableFuture();
    return Mono.fromCompletionStage(future)
        .publishOn(Schedulers.elastic())
        .map(r -> (CommandResult<S>) r);
  }
}
