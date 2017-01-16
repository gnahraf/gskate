/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io.store;


import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


/**
 *
 */
public interface Codec<T> extends Encoder<T> {
  
  T read(ByteBuffer src) throws BufferUnderflowException;

}
