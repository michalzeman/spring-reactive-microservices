package com.mz.reactivedemo.shortener.domain.aggregate;

import java.net.URI;

/**
 * Created by zemi on 22/10/2018.
 */
public class ShortUrl {

  public static final String HTTP_LOCALHOST_8080_SHORTENERS = "http://localhost:8080/shorteners/map/";

  public final String value;

  public ShortUrl(String value) {
    this.value = HTTP_LOCALHOST_8080_SHORTENERS.concat(value);
    URI.create(value);
  }
}
