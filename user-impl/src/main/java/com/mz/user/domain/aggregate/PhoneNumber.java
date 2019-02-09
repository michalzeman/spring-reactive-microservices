package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.StringValue;

import java.util.regex.Pattern;

/**
 * Created by zemi on 02/01/2019.
 */
public class PhoneNumber extends StringValue {

  private static String regex = "^(\\+|00)(?:[0-9] ?){6,14}[0-9]$";

  private static Pattern pattern = Pattern.compile(regex);

  public PhoneNumber(String phoneNumber) {
    super(phoneNumber);
    validate(phoneNumber);
  }

  private void validate(String phoneNumber) {
    if (!pattern.matcher(phoneNumber).matches()) throw new RuntimeException("Phone number validation error");
  }
}
