package com.mz.reactivedemo.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Match<R> extends AbstractMatch {

  private Optional<R> result = Optional.empty();

  private Match(Object o) {
    super(o);
  }

  public <T> Match<R> when(Class<T> type, Function<T, ? extends R> statement) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(statement);
    if (casePattern(o, type) && !this.result.isPresent()) {
      this.result = Optional.ofNullable(statement.apply(type.cast(o)));
    }
    return this;
  }

  public Optional<R> get() {
    return this.result;
  }

  public R orElseGet(Supplier<? extends R> other) {
    return result.orElseGet(other::get);
  }

  static public <R> Match<R> match(Object o) {
    return new Match<>(o);
  }
}

