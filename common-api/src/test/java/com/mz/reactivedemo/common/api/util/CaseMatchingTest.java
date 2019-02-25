package com.mz.reactivedemo.common.api.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class CaseMatchingTest {

  @Test
  void when() {
    List<String> param = Arrays.asList("String", "Two");
    List<Integer> testing = new ArrayList<>();


    CaseMatching.match(param)
        .when(ArrayList.class, () -> testing.add(1))
        .when(Set.class, () -> testing.add(2))
        .when(List.class, () -> testing.add(3));

    Assertions.assertTrue(testing.size() ==1);
    Assertions.assertTrue(testing.get(0) == 3);
  }
}