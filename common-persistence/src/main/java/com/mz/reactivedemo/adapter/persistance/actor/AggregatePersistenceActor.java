package com.mz.reactivedemo.adapter.persistance.actor;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.PersistentRepr;
import akka.persistence.RecoveryCompleted;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.ValidateResult;
import com.mz.reactivedemo.common.aggregate.Aggregate;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.Optional;

public class AggregatePersistenceActor<S> extends AbstractPersistentActor {

  public static <S> Props props(String id, AggregateFactory<S> aggregateFactory) {
    return Props.create(AggregatePersistenceActor.class, () -> new AggregatePersistenceActor(id, aggregateFactory));
  }

  private final LoggingAdapter log = Logging.getLogger(this);

  private final String id;

  private final AggregateFactory<S> aggregateFactory;

  private Optional<Aggregate<S>> aggregate;

  private AggregatePersistenceActor(String id, AggregateFactory<S> aggregateFactory) {
    this.aggregateFactory = aggregateFactory;
    this.id = id;
    this.aggregate = Optional.of(aggregateFactory.of(id));
  }

  @Override
  public Receive createReceiveRecover() {
    return receiveBuilder()
        .match(PersistentRepr.class, m -> log.info(m.toString()))
        .match(DomainEvent.class, event -> {
          log.info("Event to apply in recovery -> ", event);
          this.aggregate = Optional.of
              (this.aggregate.orElseGet(() -> aggregateFactory.of(this.id)).apply(event));
        })
        .match(RecoveryCompleted.class, evt -> log.info("Recovery completed. Current sequence: {}", lastSequenceNr()))
        .build();
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(Command.class, this::processUpdate)
        .build();
  }

  @Override
  public String persistenceId() {
    return this.id;
  }

  private void processUpdate(Command cmd) {
    aggregate.ifPresentOrElse(
        a -> a.validate(cmd)
            .map(ValidateResult::events)
            .onFailure(this::onFailure)
            .toOptional()
            .filter(domainEvents -> !domainEvents.isEmpty())
            .ifPresentOrElse(
                domainEvents -> onSuccess(a, domainEvents),
                () -> {
                  log.debug("No changes on {} aggregate", a.getClass());
                  sender().tell(CommandResult.none(), self());
                }),
        () -> sender().tell(CommandResult.error(new RuntimeException("Wrong actor state")), self())
    );
  }

  private void onSuccess(Aggregate<S> aggregate, ImmutableList<DomainEvent> events) {
    persistAll(events, (evt) -> {
      log.debug("persistAllAsync for event: {}", evt);
      aggregate.apply(evt);
    });

    deferAsync(events, (evt) -> {
      log.debug("Defer for event: {}", evt);
      this.aggregate.ifPresent(a -> {
        CommandResult<S> commandResult = CommandResult.of(a.state(), events);
        sender().tell(commandResult, self());
      });
    });
  }

  private void onFailure(Throwable error) {
    CommandResult<S> commandResult = CommandResult.error(error);
    sender().tell(commandResult, self());
  }
}
