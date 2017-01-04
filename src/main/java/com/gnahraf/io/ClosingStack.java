/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

/**
 *
 */
public class ClosingStack implements AutoCloseable {
  
  private final ArrayDeque<AutoCloseable> stack = new ArrayDeque<>();
  
  
  
  
  
  public void push(AutoCloseable resource) {
    stack.push(resource);
  }
  
  
  public AutoCloseable pop() throws NoSuchElementException {
    return stack.pop();
  }
  

  @Override
  public void close() {
    while (!stack.isEmpty())
      try {
        stack.pop().close();
      } catch (Exception x) {
        suppressClosingException(x);
      }
  }

  
  /**
   * Noop hook.
   */
  protected void suppressClosingException(Exception x) {
  }

}
