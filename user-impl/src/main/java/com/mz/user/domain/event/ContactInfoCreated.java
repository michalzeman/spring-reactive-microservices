package com.mz.user.domain.event;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface ContactInfoCreated extends DomainEvent {

  Instant createdAt();

  Optional<String> email();

  Optional<String> phoneNumber();

  Long userVersion();

  static ImmutableContactInfoCreated.Builder builder() {
    return ImmutableContactInfoCreated.builder();
  }

}
