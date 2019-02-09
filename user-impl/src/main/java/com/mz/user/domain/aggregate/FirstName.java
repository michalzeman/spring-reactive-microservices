package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.StringValue;

/**
 * Created by zemi on 02/01/2019.
 */
public class FirstName extends StringValue {
  public FirstName(String value) {
    super(value);
  }
}
