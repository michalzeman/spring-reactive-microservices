package com.mz.reactivedemo.common.api.utils;

import java.util.Objects;

public class CaseMatching extends AbstractMatching {

  private boolean executed = false;

  private CaseMatching(Object o) {
    super(o);
  }



  public <T> CaseMatching when(Class<T> type, Runnable statement) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(statement);
    if (casePattern(o, type) && !executed) {
      statement.run();
      this.executed = true;
    }
    return this;
  }

//  public void build() {
//    statement.ifPresent(s -> s.run());
//  }

  static public CaseMatching match(Object o) {
    return new CaseMatching(o);
  }

}
