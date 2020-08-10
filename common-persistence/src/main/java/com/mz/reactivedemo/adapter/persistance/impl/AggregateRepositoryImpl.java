package com.mz.reactivedemo.adapter.persistance.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.actor.RecoveryActor;
import com.mz.reactivedemo.adapter.persistance.actor.RepositoryActor;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static akka.pattern.Patterns.ask;

public class AggregateRepositoryImpl implements AggregateRepository {

  private static String REPOSITORY_NAME = "persistence_repository";

  private static final Log log = LogFactory.getLog(AggregateRepositoryImpl.class);

  private final ActorSystem system;

  private ActorRef repositoryActor;

  public AggregateRepositoryImpl(ActorSystem system) {
    this.system = system;
    this.repositoryActor = getRepositoryActor(system);
  }

  private ActorRef getRepositoryActor(ActorSystem system) {
    return system.actorOf(RepositoryActor.props(), String.format("%s_%s", system.name(),
        REPOSITORY_NAME));
  }

  private Mono<ActorRef> recoverRepositoryActor() {
    log.info("recoverRepositoryActor() -> is going to recover RepositoryActor");
    return Mono.fromCompletionStage(ask(system.actorOf(RecoveryActor.props()),
        new RecoveryActor.RecoverActor(this.repositoryActor.path(),
            () -> getRepositoryActor(system)),
        Duration.ofMillis(5000)))
        .cast(ActorRef.class);
  }

  private <S> Mono<CommandResult<S>> sendCommand(ActorRef repActor, String aggregateId, Command cmd,
                                                 AggregateFactory<S> aggregateFactory) {
    return Mono.fromCompletionStage(ask(repActor, new RepositoryActor.CommandMsg(cmd, aggregateId,
        aggregateFactory), Duration.ofMillis(5000)))
        .publishOn(Schedulers.elastic())
        .map(r -> (CommandResult<S>) r);
  }

  @Override
  public <S> Mono<CommandResult<S>> execute(String aggregateId, Command cmd,
                                            AggregateFactory<S> aggregateFactory) {
    return sendCommand(this.repositoryActor, aggregateId, cmd, aggregateFactory)
        .onErrorResume(error -> recoverRepositoryActor()
            .flatMap(actorRef -> {
              this.repositoryActor = actorRef;
              return sendCommand(actorRef, aggregateId, cmd, aggregateFactory);
            })
        );

  }

}
