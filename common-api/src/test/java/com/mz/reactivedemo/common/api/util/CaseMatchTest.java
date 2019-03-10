package com.mz.reactivedemo.common.api.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class CaseMatchTest {

  @Test
  void when() {
    List<String> param = Arrays.asList("String", "Two");
    List<Integer> testing = new ArrayList<>();


    CaseMatch.match(param)
        .when(ArrayList.class, () -> testing.add(1))
        .when(Set.class, () -> testing.add(2))
        .when(List.class, () -> testing.add(3));

    Assertions.assertTrue(testing.size() ==1);
    Assertions.assertTrue(testing.get(0) == 3);
  }

  @Test
  void whenConsumer() {
    List<String> param = Arrays.asList("String", "Two");
    List<Integer> testing = new ArrayList<>();


    CaseMatch.match(param)
        .when(ArrayList.class, c -> testing.add(1))
        .when(Set.class, () -> testing.add(2))
        .when(List.class, () -> testing.add(3));

    Assertions.assertTrue(testing.size() ==1);
    Assertions.assertTrue(testing.get(0) == 3);
  }

  @Test
  void orElse() {
    List<String> param = Arrays.asList("String", "Two");
    List<Integer> testing = new ArrayList<>();


    CaseMatch.match(param)
        .when(Map.class, c -> testing.add(1))
        .when(Set.class, () -> testing.add(2))
        .orElse(() -> testing.add(-1));

    Assertions.assertTrue(testing.size() ==1);
    Assertions.assertTrue(testing.get(0) == -1);
  }
}