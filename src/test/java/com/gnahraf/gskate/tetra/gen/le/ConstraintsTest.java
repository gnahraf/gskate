/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.le;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.gskate.tetra.gen.le.Constraints;

/**
 *
 */
public class ConstraintsTest {
  

  @Test
  public void testDefaultIsValid() {
    Constraints c = new Constraints();
    assertTrue(c.isValid());
  }
  
  
  @Test
  public void testIsValid() {
    Constraints c;
    
    c = new Constraints();
    c.initKmsAboveGround = 0;
    assertFalse(c.isValid());
    
    c = new Constraints();
    c.initTetherLength = 0;
    assertFalse(c.isValid());
    
    c = new Constraints();
    c.minTetherLength = 0;
    assertFalse(c.isValid());
    
    // TODO: more tedious checks..
  }
  
  
  
  @Test
  public void testEquals() {
    Constraints c = new Constraints();
    assertTrue(c.equals(c));
    
    Constraints d = new Constraints();
    assertEquals(c, d);
    
    d.initKmsAboveGround = c.initKmsAboveGround + 0.1;
    
    assertNotEquals(c, d);
    
    
    // TODO: more tedious checks..
  }
  
  
  @Test
  public void testHashCode() {
    Constraints c = new Constraints();
    
    Constraints d = new Constraints();
    
    d.maxTensileForce = c.maxTensileForce + 0.1;
    assertNotEquals(c.hashCode(), d.hashCode());
  }
  
  
  
  @Test
  public void testClone() {
    Constraints c = new Constraints();
    assertEquals(c, c.clone());
  }

}
