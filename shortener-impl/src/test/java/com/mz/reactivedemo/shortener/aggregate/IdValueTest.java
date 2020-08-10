package com.mz.reactivedemo.shortener.aggregate;

import com.mz.reactivedemo.common.aggregate.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Created by zemi on 06/10/2018.
 */
public class IdValueTest {

  @Test
  public void createTest() {
    Id id = new Id(UUID.randomUUID().toString());
    Assertions.assertFalse(id.value.isEmpty());
  }

  @Test
  public void createTest_Error() {
    Assertions.assertThrows(RuntimeException.class, () -> new Id(""));
    Assertions.assertThrows(RuntimeException.class, () -> new Id(null));
  }


}
