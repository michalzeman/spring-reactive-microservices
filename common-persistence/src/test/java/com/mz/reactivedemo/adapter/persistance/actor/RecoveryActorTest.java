package com.mz.reactivedemo.adapter.persistance.actor;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecoveryActorTest {

  static ActorSystem system;

  @BeforeAll
  public static void setup() {
    system = ActorSystem.create();
  }

  @Test
  void recoverActorNotEquals() {
    final var testMsg = "test";
    final var testProbe = new TestKit(system);
    final var repositoryActor = new TestKit(system);
    var repoActorPath = ActorPath.fromString("akka://default/user/testNotEquals");
    var repoActorNew = new TestKit(system);
    final var recoveryActor = system.actorOf(RecoveryActor.props());
    recoveryActor.tell(new RecoveryActor.RecoverActor(repoActorPath, () -> repoActorNew.getRef()), testProbe.getRef());

    ActorRef result = testProbe.expectMsgClass(ActorRef.class);
    result.tell(testMsg, testProbe.getRef());

    repoActorNew.expectMsg(testMsg);
  }

  @Test
  void recoverActorEquals() {
    final var testProbe = new TestKit(system);

    final var repositoryActor = system.actorOf(RepositoryActor.props());

    var repoActorPath = ActorPath.fromString("akka://default/user/testEquals");
    final var recoveryActor = system.actorOf(RecoveryActor.props());

    recoveryActor.tell(new RecoveryActor.RecoverActor(repoActorPath, () -> repositoryActor), testProbe.getRef());
    var repoActorNew = testProbe.expectMsgClass(ActorRef.class);
    assertEquals(repoActorNew, repositoryActor);
  }

  @AfterAll
  public static void teardown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }
}
