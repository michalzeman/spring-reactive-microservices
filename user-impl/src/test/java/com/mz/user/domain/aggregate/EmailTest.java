package com.mz.user.domain.aggregate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by zemi on 03/01/2019.
 */
public class EmailTest {

  @Test
  public void testEmail_OK() {
    String emailValue = "test@test.org";

    Email email = new Email(emailValue);

    Assertions.assertNotNull(email);
    Assertions.assertEquals(email.value, emailValue);
  }

  @Test
  public void testEmail_Failed() {
    String emailValue = "@test.org";
    Assertions.assertThrows(RuntimeException.class, ()-> new Email(emailValue));
  }

}
