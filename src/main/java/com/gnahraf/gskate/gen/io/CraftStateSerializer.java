/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.io;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.gnahraf.gskate.model.Tetra;

/**
 * Reads and writes <tt>CraftState<tt>s to memory buffers. Suitable for serializing
 * and de-serializing to and from streams. Instances are stateless and therefore
 * safe under concurrent access.
 */
public class CraftStateSerializer {
  
  /**
   * 
     Size in bytesto allocate.
     Calculation goes like..
     <pre>
     Bob: 2 * 3 * 8 = 48
     Tetra: 4 * Bob + 6 * 8 = 5 * 48 = 240
     State: Tetra + 8 = 248
     </pre>
   */
  public final static int BUFFER_ALLOC_SIZE = 256;
  
  private final TetraSerializer tetraCodec = new TetraSerializer();
  
  
  public void write(CraftState state, ByteBuffer out) throws BufferOverflowException {
    out.putLong(state.getTime());
    tetraCodec.write(state.getCraft(), out);
  }
  
  
  public CraftState read(ByteBuffer in) throws BufferUnderflowException {
    long time = in.getLong();
    Tetra craft = tetraCodec.read(in);
    return new CraftState(time, craft);
  }

}
