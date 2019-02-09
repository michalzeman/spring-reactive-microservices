package com.mz.user.domain.aggregate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by zemi on 03/01/2019.
 */
public class PhoneNumberTest {

  @Test
  public void testNumber_OK() {
    String phoneNum1 = "+421 911 888 222";

    PhoneNumber phoneNumber1 = new PhoneNumber(phoneNum1);
    Assertions.assertEquals(phoneNumber1.value, phoneNum1);

    String phoneNum2 = "00421 911 888 222";

    PhoneNumber phoneNumber2 = new PhoneNumber(phoneNum2);
    Assertions.assertEquals(phoneNumber2.value, phoneNum2);
  }

  @Test
  public void testNumber_Failed() {
    Assertions.assertThrows(RuntimeException.class, () -> new PhoneNumber("+421a911888"));
  }
}
