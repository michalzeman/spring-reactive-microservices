package com.mz.reactivedemo.common.utils;

import org.apache.commons.logging.Log;

import java.util.function.Supplier;

/**
 * Created by zemi on 22/06/2018.
 */
public class Logger {

  private final Log logger;

  public Logger(Log logger) {
    this.logger = logger;
  }

  public void debug(Supplier<String> sp) {
    if (logger.isDebugEnabled()) logger.debug(sp.get());
  }

  public Log log() {
    return logger;
  }
}
