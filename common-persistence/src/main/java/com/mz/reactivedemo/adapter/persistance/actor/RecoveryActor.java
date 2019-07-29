package com.mz.reactivedemo.adapter.persistance.actor;

import akka.actor.*;

import java.util.UUID;
import java.util.function.Supplier;

public class RecoveryActor extends AbstractActor {

  public static Props props() {
    return Props.create(RecoveryActor.class);
  }

  public static class RecoverActor {
    public final ActorPath actorPath;

    public final Supplier<ActorRef> createActor;

    public RecoverActor(ActorPath actorPath, Supplier<ActorRef> createActor) {
      this.createActor = createActor;
      this.actorPath = actorPath;
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(RecoverActor.class, r -> {
          getContext().getSystem()
              .actorSelection(r.actorPath)
              .tell(new Identify(UUID.randomUUID().toString()), getSelf());
          getContext().become(actorIdentity(getSender(), r));
        })
        .build();
  }

  private Receive actorIdentity(ActorRef orgRequester, RecoverActor recoverActor) {
    return receiveBuilder()
        .match(ActorIdentity.class, actorIdentity -> {
          orgRequester.tell(actorIdentity.getActorRef().orElseGet(() -> recoverActor.createActor.get()), getSelf());
          getContext().getSystem().stop(getSelf());
        })
        .build();
  }
}
