/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.gnahraf.io.store.Codec;
import com.gnahraf.util.data.NormPoint;

/**
 *
 */
class NormPointCodec implements Codec<NormPoint> {

  @Override
  public void write(NormPoint item, ByteBuffer dtn) throws BufferOverflowException {
    dtn.putDouble(item.x());
    dtn.putDouble(item.y());
  }

  @Override
  public int maxBytes() {
    return 16;
  }

  @Override
  public NormPoint read(ByteBuffer src) throws BufferUnderflowException {
    double x = src.getDouble();
    double y = src.getDouble();
    return new NormPoint(x, y);
  }

  

}
