package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregate.StringValue;

import java.util.regex.Pattern;

/**
 * Created by zemi on 02/01/2019.
 */
public class Email extends StringValue {

  private static String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

  private static Pattern pattern = Pattern.compile(regex);

  public Email(String email) {
    super(email);
    validateEmail(email);
  }

  private void validateEmail(String email) {
    if (!pattern.matcher(email).matches()) throw new RuntimeException("Email validation error");
  }
}
