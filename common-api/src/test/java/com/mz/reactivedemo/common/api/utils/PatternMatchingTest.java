package com.mz.reactivedemo.common.api.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PatternMatchingTest {

    @Test
    void build() {

        List<String> list = Arrays.asList("list");

        String result = PatternMatching.<String>match(list)
            .when(List.class, type -> "List")
            .when(ArrayList.class, type -> "ArrayList")
            .when(String.class, type -> "String")
            .when(List.class, type -> "List2")
            .result().get();

        assertTrue(result.equals("List"));
    }
}