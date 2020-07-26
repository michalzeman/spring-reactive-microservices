package com.mz.reactivedemo.common.util;

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

  public void debug(Supplier<Object> sp) {
    if (logger.isDebugEnabled()) logger.debug(sp.get());
  }

  public void debug(Supplier<Object> sp, Throwable error) {
    if (logger.isDebugEnabled()) logger.debug(sp.get(), error);
  }

  public void warning(Supplier<Object> sp) {
    if (logger.isWarnEnabled()) logger.warn(sp.get());
  }

  public void warning(Supplier<Object> sp, Throwable error) {
    if (logger.isWarnEnabled()) logger.warn(sp.get(), error);
  }

  public void info(Supplier<Object> sp) {
    if (logger.isInfoEnabled()) logger.info(sp.get());
  }

  public void info(Supplier<Object> sp, Throwable error) {
    if (logger.isInfoEnabled()) logger.info(sp.get(), error);
  }

  public Log log() {
    return logger;
  }

}
