package com.mz.reactivedemo.adapter.persistance.persistence;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.common.api.events.Command;

import java.util.Optional;

public class RepositoryActor extends AbstractActor {

  public static Props props() {
    return Props.create(RepositoryActor.class);
  }

  public static class CreateCommandMsg {

    final Command cmd;

    final String aggregateId;

    final AggregateFactory aggregateFactory;

    public CreateCommandMsg(Command cmd, String aggregateId, AggregateFactory aggregateFactory) {
      this.cmd = cmd;
      this.aggregateId = aggregateId;
      this.aggregateFactory = aggregateFactory;
    }
  }

  public static class UpdateCommandMsg {

    final Command cmd;

    final String aggregateId;

    public UpdateCommandMsg(Command cmd, String aggregateId) {
      this.cmd = cmd;
      this.aggregateId = aggregateId;
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(CreateCommandMsg.class, c -> {
          getContext().findChild(c.aggregateId)
              .orElseGet(() -> getContext().actorOf(EntityPersistenceActor.props(c.aggregateId, c.aggregateFactory),
                  c.aggregateId))
              .tell(c.cmd, sender());
        })
        .match(UpdateCommandMsg.class, c -> {
          Optional<ActorRef> persistenceActorOpt = getContext().findChild(c.aggregateId);
          if (persistenceActorOpt.isPresent()) {
            persistenceActorOpt.get().tell(c.cmd, sender());
          } else {
            sender().tell(CommandResult.error(new RuntimeException("Entity with id: " + c.aggregateId + "doesn't exist")),
                self());
          }
        })
        .build();
  }
}
