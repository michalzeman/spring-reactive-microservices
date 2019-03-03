package com.mz.reactivedemo.common.api.util;

import java.util.Objects;

public class CaseMatch extends AbstractMatch {

  private boolean executed = false;

  private CaseMatch(Object o) {
    super(o);
  }



  public <T> CaseMatch when(Class<T> type, Runnable statement) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(statement);
    if (casePattern(o, type) && !executed) {
      statement.run();
      this.executed = true;
    }
    return this;
  }

  static public CaseMatch match(Object o) {
    return new CaseMatch(o);
  }

}
