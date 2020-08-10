package com.mz.reactivedemo.adapter.persistance.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.common.api.events.Command;

public class RepositoryActor extends AbstractActor {

  public static Props props() {
    return Props.create(RepositoryActor.class);
  }

  public static class CommandMsg {

    final Command cmd;

    final String aggregateId;

    final AggregateFactory aggregateFactory;

    public CommandMsg(Command cmd, String aggregateId, AggregateFactory aggregateFactory) {
      this.cmd = cmd;
      this.aggregateId = aggregateId;
      this.aggregateFactory = aggregateFactory;
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(CommandMsg.class, c -> getContext().findChild(c.aggregateId)
            .orElseGet(() -> getContext().actorOf(AggregatePersistenceActor.props(c.aggregateId, c.aggregateFactory),
                c.aggregateId))
            .tell(c.cmd, sender()))
        .build();
  }
}
