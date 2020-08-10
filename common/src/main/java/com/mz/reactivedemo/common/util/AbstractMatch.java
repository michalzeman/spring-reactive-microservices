package com.mz.reactivedemo.common.util;

import java.util.Optional;

abstract class AbstractMatch {

  protected final Object o;

  protected AbstractMatch(Object o) {
    this.o = o;
  }

  protected  <T> boolean casePattern(Object obj, Class<T> type) {
    return Optional.ofNullable(type)
        .flatMap(t -> Optional.ofNullable(obj).map(t::isInstance)).orElse(false);
  }
}
