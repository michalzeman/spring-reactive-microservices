package com.mz.reactivedemo.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class TryTest {

  @Test
  void of() {
    Assertions.assertTrue(Try.of(() -> {
      throw new Exception();
    }).isFailure());

    Assertions.assertTrue(Try.of(() -> "success").isSuccess());
  }

  @Test
  void map() {
    Try<Boolean> resultS = Try.of(() -> 2).map(n -> Integer.toString(n)).map(s -> !s.isEmpty());
    Assertions.assertTrue(resultS.isSuccess());
    Assertions.assertFalse(resultS.isFailure());
    Assertions.assertTrue(resultS.get());
    Assertions.assertTrue(resultS.toOptional().isPresent());

    Try<Boolean> resultF = Try.of(() -> "Ano").map(n -> Integer.valueOf(n)).map(s -> s > 0);
    Assertions.assertFalse(resultF.isSuccess());
    Assertions.assertTrue(resultF.isFailure());
    Assertions.assertThrows(NoSuchElementException.class, () -> resultF.get());
    Assertions.assertFalse(resultF.toOptional().isPresent());
    Assertions.assertTrue(resultF.getOrElse(() -> true));
  }

  @Test
  void flatMap() {
    Try<Boolean> resultS = Try.of(() -> 2).map(n -> Integer.toString(n)).flatMap((s -> Try.of(() -> !s.isEmpty())));
    Assertions.assertTrue(resultS.isSuccess());
    Assertions.assertFalse(resultS.isFailure());
    Assertions.assertTrue(resultS.get());
    Assertions.assertTrue(resultS.toOptional().isPresent());

    Try<Boolean> resultF = Try.of(() -> "Ano").flatMap(n -> Try.of(() -> Integer.valueOf(n))).map(s -> s > 0);
    Assertions.assertFalse(resultF.isSuccess());
    Assertions.assertTrue(resultF.isFailure());
    Assertions.assertThrows(NoSuchElementException.class, () -> resultF.get());
    Assertions.assertFalse(resultF.toOptional().isPresent());
  }
}
