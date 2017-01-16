/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.io.store.Encoder;

/**
 *
 */
class ConstraintsEncoder implements Encoder<Constraints> {

  
  
  @Override
  public void write(Constraints constraints, ByteBuffer dtn) throws BufferOverflowException {

    dtn.putDouble(constraints.initTetherLength);
    dtn.putDouble(constraints.steadyStateTetherLength);
    dtn.putDouble(constraints.maxTetherLength);
    dtn.putDouble(constraints.minTetherLength);
    dtn.putDouble(constraints.minKmsAboveGround);
    dtn.putDouble(constraints.initKmsAboveGround);
    dtn.putDouble(constraints.maxTensileForce);
    dtn.putDouble(constraints.maxCompressiveForce);
    dtn.putDouble(constraints.timeFineness);
  }

  @Override
  public int maxBytes() {
    return 128;
  }

}
