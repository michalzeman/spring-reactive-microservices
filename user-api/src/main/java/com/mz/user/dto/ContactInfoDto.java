package com.mz.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableContactInfoDto.class)
@JsonDeserialize(as = ImmutableContactInfoDto.class)
public interface ContactInfoDto {

  String userId();

  Instant createdAt();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> email();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> phoneNumber();

  static ImmutableContactInfoDto.Builder builder() {
    return ImmutableContactInfoDto.builder();
  }
}
