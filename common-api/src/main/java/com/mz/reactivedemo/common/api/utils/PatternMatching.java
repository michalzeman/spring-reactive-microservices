package com.mz.reactivedemo.common.api.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class PatternMatching<R> {

  private final Object o;

  private Optional<R> result = Optional.empty();

  private PatternMatching(Object o) {
    this.o = o;
  }

  private <C> boolean casePattern(Object obj, Class<C> type) {
    return Optional.ofNullable(type)
        .flatMap(t -> Optional.ofNullable(obj).map(t::isInstance)).orElse(false);
  }

  public <T> PatternMatching<R> when(Class<T> type, Function<T, R> statement) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(statement);
    if (casePattern(o, type) && !this.result.isPresent()) {
      this.result = Optional.ofNullable(statement.apply(type.cast(o)));
    }
    return this;
  }

  public Optional<R> result() {
    return this.result;
  }

  static public <R> PatternMatching<R> match(Object o) {
    return new PatternMatching<>(o);
  }
}

