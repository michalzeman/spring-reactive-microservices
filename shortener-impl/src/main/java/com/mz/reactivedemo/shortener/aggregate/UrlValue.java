package com.mz.reactivedemo.shortener.aggregate;

import java.net.URI;

/**
 * Created by zemi on 30/09/2018.
 */
public class UrlValue {

  public final String value;

  public UrlValue(String value) {
    URI.create(value);
    this.value = value;
  }
}
