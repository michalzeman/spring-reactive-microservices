package com.mz.reactivedemo.common.api.utils;

import java.util.Optional;

abstract class AbstractMatching {

  protected final Object o;

  protected AbstractMatching(Object o) {
    this.o = o;
  }

  protected  <T> boolean casePattern(Object obj, Class<T> type) {
    return Optional.ofNullable(type)
        .flatMap(t -> Optional.ofNullable(obj).map(t::isInstance)).orElse(false);
  }
}
