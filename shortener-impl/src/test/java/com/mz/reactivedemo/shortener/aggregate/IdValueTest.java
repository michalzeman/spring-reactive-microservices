package com.mz.reactivedemo.shortener.aggregate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Created by zemi on 06/10/2018.
 */
public class IdValueTest {

  @Test
  public void createTest() {
    IdValue id = new IdValue(UUID.randomUUID().toString());
    Assertions.assertFalse(id.value.isEmpty());
  }

  @Test
  public void createTest_Error() {
    Assertions.assertThrows(RuntimeException.class, () -> new IdValue(""));
    Assertions.assertThrows(RuntimeException.class, () -> new IdValue(null));
  }


}
