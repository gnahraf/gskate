/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io.store;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 *
 */
public interface Encoder<T> {
  
  void write(T item, ByteBuffer dtn) throws BufferOverflowException;
  
  int maxBytes();
}
