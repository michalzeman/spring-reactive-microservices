package com.mz.reactivedemo.common.util;

import java.util.Objects;
import java.util.function.Consumer;

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

  public <T> CaseMatch when(Class<T> type, Consumer<T> statement) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(statement);
    if (casePattern(o, type) && !executed) {
      statement.accept(type.cast(o));
      this.executed = true;
    }
    return this;
  }

  public <T> CaseMatch orElse(Runnable statement) {
    Objects.requireNonNull(statement);
    if (!executed) {
      statement.run();
      this.executed = true;
    }
    return this;
  }

  static public CaseMatch match(Object o) {
    return new CaseMatch(o);
  }

}
