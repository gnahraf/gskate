/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;

/**
 * Represents a constraint violation, such as when the craft crashes, or its tethers grow
 * too long.
 */
public class ConstraintViolationException extends RuntimeException {

  public ConstraintViolationException() {
  }

  public ConstraintViolationException(String message) {
    super(message);
  }

  public ConstraintViolationException(Throwable cause) {
    super(cause);
  }

  public ConstraintViolationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConstraintViolationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
