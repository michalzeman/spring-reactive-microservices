package com.mz.reactivedemo.shortener.aggregate;

import java.net.URI;

/**
 * Created by zemi on 22/10/2018.
 */
public class ShortUrlValue {

  public static final String HTTP_LOCALHOST_8080_SHORTENERS = "http://localhost:8080/shorteners/map/";

  public final String value;

  public ShortUrlValue(String value) {
    this.value = HTTP_LOCALHOST_8080_SHORTENERS.concat(value);
    URI.create(value);
  }
}
