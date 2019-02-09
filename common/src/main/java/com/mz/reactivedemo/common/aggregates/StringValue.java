package com.mz.reactivedemo.common.aggregates;

import java.util.Objects;

/**
 * Created by zemi on 30/09/2018.
 */
public class StringValue {
  public final java.lang.String value;

  public StringValue(java.lang.String value) {
    if (Objects.isNull(value) || value.isEmpty()) {
      throw new RuntimeException("String value is null or empty!");
    }
    this.value = value;
  }
}
