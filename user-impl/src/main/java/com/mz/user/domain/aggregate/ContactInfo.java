package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.Id;
import com.mz.user.dto.ContactInfoDto;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 2019-01-18.
 */
@Value.Immutable
public interface ContactInfo {

  Optional<Id> userId();

  Optional<Email> email();

  Optional<PhoneNumber> phoneNumber();

  @Value.Default
  default Instant createdAt() {
    return Instant.now();
  }

  default ContactInfoDto toDto() {
    return ContactInfoDto
        .builder()
        .createdAt(createdAt())
        .email(email().map(m -> m.value))
        .phoneNumber(phoneNumber().map(n -> n.value))
        .userId(userId().map(id -> id.value))
        .build();
  }

  static ImmutableContactInfo.Builder builder() {
    return ImmutableContactInfo.builder();
  }
}
