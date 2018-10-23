package com.mz.reactivedemo.common.model;

/**
 * Created by zemi on 02/10/2018.
 */
public class ErrorMessage {

  private String error;

  public ErrorMessage(String message) {
    this.error = message;
  }

  public ErrorMessage() {
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
