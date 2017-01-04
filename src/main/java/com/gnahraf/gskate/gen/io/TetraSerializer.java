/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.io;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.gnahraf.gskate.model.Tetra;

/**
 * Reads and writes <tt>Tetra<tt>s to memory buffers. Suitable for serializing
 * and de-serializing to and from streams. Instances are stateless and therefore
 * safe under concurrent access.
 */
public class TetraSerializer {
  
  private final BobSerializer bobSerializer = new BobSerializer();
  
  
  public void write(Tetra craft, ByteBuffer buffer) throws BufferOverflowException {
    for (int i = 0; i < 4; ++i)
      bobSerializer.write(craft.getBob(i), buffer);
    
    for (int i = 0; i < 6; ++i)
      buffer.putDouble(craft.getTetherByIndex(i));
  }
  
  
  
  public Tetra read(ByteBuffer buffer) throws BufferUnderflowException {
    Tetra craft = new Tetra();
    for (int i = 0; i < 4; ++i)
      craft.getBob(i).copyFrom(bobSerializer.read(buffer));
    
    for (int i = 0; i < 6; ++i)
      craft.setTetherByIndex(i, buffer.getDouble());
    
    return craft;
  }
  
  

}
