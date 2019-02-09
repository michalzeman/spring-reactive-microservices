package com.mz.reactivedemo.common.errors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created by zemi on 02/10/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableErrorMessage.class)
@JsonDeserialize(as = ImmutableErrorMessage.class)
public interface ErrorMessage {

  String error();

  static ImmutableErrorMessage.Builder builder() {
    return ImmutableErrorMessage.builder();
  }
}
