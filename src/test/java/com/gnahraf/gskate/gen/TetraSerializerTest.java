/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;


import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Tetra;

/**
 *
 */
public class TetraSerializerTest {

  @Test
  public void testTheTest() {
    Tetra craft = newCraft();
    
    assertTetraslEqual(craft, craft);
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
  
  
  
  private Tetra newCraft() {
    Tetra craft = new Tetra();
    double x = 0, y = 1, z = 2;
    for (int i = 0; i < 4; ++i) {
      Bob bob = craft.getBob(i);
      x = x + 1.5;
      y = y + 1.5;
      z = z + 1.5;
      bob.setPosition(x, y, z);
      x = x + 1.5;
      y = y + 1.5;
      z = z + 1.5;
      bob.setAcceleration(x, y, z);
      x = x + 1.5;
      y = y + 1.5;
      z = z + 1.5;
      bob.setVelocity(x, y, z);
    }
    
    double t = 0;
    for (int i = 0; i < 6; ++i) {
      t += 2.2;
      craft.setTetherByIndex(i, t);
    }
    return craft;
  }
  
  
  private void assertTetraslEqual(Tetra expected, Tetra actual) {
    for (int i = 0; i < 4; ++i)
      assertBobsEqual(expected.getBob(i), actual.getBob(i));
    
    for (int i = 0; i < 6; ++i)
      assertEquals(expected.getTetherByIndex(i), actual.getTetherByIndex(i), 0);
  }
  
  
  private void assertBobsEqual(Bob expected, Bob actual) {
    assertEquals(expected.getX(), actual.getX(), 0);
    assertEquals(expected.getY(), actual.getY(), 0);
    assertEquals(expected.getZ(), actual.getZ(), 0);

    assertEquals(expected.getVx(), actual.getVx(), 0);
    assertEquals(expected.getVy(), actual.getVy(), 0);
    assertEquals(expected.getVz(), actual.getVz(), 0);

    assertEquals(expected.getAx(), actual.getAx(), 0);
    assertEquals(expected.getAy(), actual.getAy(), 0);
    assertEquals(expected.getAz(), actual.getAz(), 0);
    
  }

}
