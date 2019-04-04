package com.mz.reactivedemo.adapter.persistance.persistence;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.PersistentRepr;
import akka.persistence.RecoveryCompleted;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.ValidateResult;
import com.mz.reactivedemo.common.aggregate.Aggregate;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.Optional;

public class EntityPersistenceActor<S> extends AbstractPersistentActor {

  public static <S> Props props(String id, AggregateFactory<S> aggregateFactory) {
    return Props.create(EntityPersistenceActor.class, () -> new EntityPersistenceActor(id, aggregateFactory));
  }

  private final LoggingAdapter log = Logging.getLogger(this);

  private final String id;

  private final AggregateFactory<S> aggregateFactory;

  private Optional<Aggregate<S>> aggregate;

  public EntityPersistenceActor(String id, AggregateFactory<S> aggregateFactory) {
    this.aggregateFactory = aggregateFactory;
    this.id = id;
    this.aggregate = Optional.of(aggregateFactory.of(id));
  }

  @Override
  public Receive createReceiveRecover() {
    return receiveBuilder()
        .match(PersistentRepr.class, m -> log.info(m.toString()))
        .match(Event.class, event ->
            this.aggregate = Optional.of(this.aggregate.orElseGet(() -> aggregateFactory.<S>of(this.id)).apply(event)))
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
    if (aggregate.isPresent()) {
      boolean updated = aggregate.get().validate(cmd)
          .map(ValidateResult::events)
          .onSuccess(events -> onSuccess(aggregate.get(), events))
          .onFailure(this::onFailure)
          .toOptional()
          .isPresent();
      if (!updated) {
        sender().tell(CommandResult.none(), self());
      }
    } else {
      sender().tell(CommandResult.error(new RuntimeException("Wrong actor state")), self());
    }
  }

  protected void onSuccess(Aggregate<S> aggregate, ImmutableList<Event> events) {
    persistAll(events, (evt) -> {
      log.debug("persistAllAsync for event: {}", evt);
      aggregate.apply(evt);
    });

    deferAsync(events, (evt) -> {
      log.debug("Defer for event: {}", evt);
      CommandResult<S> commandResult = CommandResult.of(this.aggregate.get().state(), events);
      sender().tell(commandResult, self());
    });
  }

  protected void onFailure(Throwable error) {
    CommandResult<S> commandResult = CommandResult.error(error);
    sender().tell(commandResult, self());
  }
}
