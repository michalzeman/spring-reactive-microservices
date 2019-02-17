package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.ContactInfoDto;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface ContactInfoCreated extends Event {

  String userId();

  Instant createdAt();

  Optional<String> email();

  Optional<String> phoneNumber();

  Optional<Long> userVersion();

  static ImmutableContactInfoCreated.Builder builder() {
    return ImmutableContactInfoCreated.builder();
  }

}
