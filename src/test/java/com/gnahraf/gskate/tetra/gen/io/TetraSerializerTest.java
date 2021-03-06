/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.io;


import static com.gnahraf.gskate.tetra.model.TetraTest.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.gnahraf.gskate.tetra.gen.io.TetraSerializer;
import com.gnahraf.gskate.tetra.model.Tetra;

/**
 *
 */
public class TetraSerializerTest {

  @Test
  public void testTheTest() {
    assertTetraslEqual(newCraft(), newCraft());
  }
  
  
  @Test
  public void testReadWrite() {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Tetra craft = newCraft();
    
    TetraSerializer serializer = new TetraSerializer();
    serializer.write(craft, buffer);
    
    buffer.flip();
    
    Tetra copy = serializer.read(buffer);
    assertTetraslEqual(craft, copy);
  }
  
  
  

}
