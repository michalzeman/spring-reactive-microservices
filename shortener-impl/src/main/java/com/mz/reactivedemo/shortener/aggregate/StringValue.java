package com.mz.reactivedemo.shortener.aggregate;

import java.util.Objects;

/**
 * Created by zemi on 30/09/2018.
 */
public class StringValue {
  public final String value;

  public StringValue(String value) {
    if (Objects.isNull(value) || value.isEmpty()) {
      throw new RuntimeException("String value is null or empty!");
    }
    this.value = value;
  }
}
