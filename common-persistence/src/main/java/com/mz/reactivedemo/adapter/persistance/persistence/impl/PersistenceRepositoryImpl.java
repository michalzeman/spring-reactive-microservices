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

  private static String REPOSITORY_NAME = "persistence_repository";

  private static final Log log = LogFactory.getLog(PersistenceRepositoryImpl.class);

  private final ActorSystem system;

  private final ActorRef repositoryActor;

  public PersistenceRepositoryImpl(ActorSystem system) {
    this.system = system;
    this.repositoryActor = system.actorOf(RepositoryActor.props(), String.format("%s_%s",system.name(), REPOSITORY_NAME));
  }

  @Override
  public <S> Mono<CommandResult<S>> execute(String aggregateId, Command cmd,
                                            AggregateFactory<S> aggregateFactory) {
    CompletableFuture<Object> future = ask(repositoryActor, new RepositoryActor.CreateCommandMsg(cmd, aggregateId, aggregateFactory),
        Duration.ofMillis(5000)).toCompletableFuture();
    return Mono.fromCompletionStage(future)
        .publishOn(Schedulers.elastic())
        .map(r -> (CommandResult<S>) r);
  }
}
