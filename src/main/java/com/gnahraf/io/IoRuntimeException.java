/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.IOException;


/**
 * An <tt>IOException</tt> cast as an unchecked <tt>RuntimeException</tt>.
 */
@SuppressWarnings("serial")
public class IoRuntimeException extends RuntimeException {

  /**
   * 
   */
  public IoRuntimeException() {
  }

  /**
   * @param message
   */
  public IoRuntimeException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public IoRuntimeException(IOException cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public IoRuntimeException(String message, IOException cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public IoRuntimeException(
      String message, IOException cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
