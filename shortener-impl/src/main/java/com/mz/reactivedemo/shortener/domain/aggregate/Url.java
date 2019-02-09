package com.mz.reactivedemo.shortener.domain.aggregate;

import java.net.URI;

/**
 * Created by zemi on 30/09/2018.
 */
public class Url {

  public final String value;

  public Url(String value) {
    URI.create(value);
    this.value = value;
  }
}
