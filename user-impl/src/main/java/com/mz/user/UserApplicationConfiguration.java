package com.mz.user;

import akka.actor.ActorSystem;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.adapter.persistance.persistence.impl.PersistenceRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class UserApplicationConfiguration {

  @Bean
  @Scope("singleton")
  public ActorSystem actorSystem(@Value("${spring.cloud.stream.kafka.streams.binder.application-id}") String actorySystemName) {
    return ActorSystem.create(actorySystemName);
  }

  @Bean
  @Scope("singleton")
  public PersistenceRepository persistenceRepository(ActorSystem actorSystem) {
    return new PersistenceRepositoryImpl(actorSystem);
  }

}
