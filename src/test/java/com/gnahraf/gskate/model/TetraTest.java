/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.model;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class TetraTest {

  @Test
  public void testEquals() {
    Tetra craft = new Tetra();
    
    Tetra b = newCraft();

    // test the test..
    {
      assertTetraslEqual(craft, new Tetra());
      boolean pass;
      try {
        assertTetraslEqual(craft, b);
        pass = false;
      } catch (Throwable expected) {
        pass = true;
      }
      assertTrue(pass);
    }
    

    assertTrue(craft.equals(craft));
    assertTrue(craft.equals(new Tetra()));
    assertFalse(craft.equals(null));
    assertFalse(craft.equals(new Object()));
    assertNotEquals(craft, b);
    
    Tetra c = new Tetra();
    
    c.getBob(0).setAcceleration(2, 3, 4);
    assertEquals(craft, c);
  }
  
  
  
  @Test
  public void testHashCode() {
    assertEquals(new Tetra().hashCode(), new Tetra().hashCode());
    assertNotEquals(new Tetra().hashCode(), newCraft().hashCode());
    Tetra craft = new Tetra();
    craft.setTether(0, 1, 29);
    assertNotEquals(new Tetra().hashCode(), craft);
  }
  

  
  
  public static void assertTetraslEqual(Tetra expected, Tetra actual) {
    for (int i = 0; i < 4; ++i)
      assertBobsEqual(expected.getBob(i), actual.getBob(i));
    
    for (int i = 0; i < 6; ++i)
      assertEquals(expected.getTetherByIndex(i), actual.getTetherByIndex(i), 0);
    
    assertEquals(expected, actual);
  }
  
  
  public static void assertBobsEqual(Bob expected, Bob actual) {
    assertEquals(expected.getX(), actual.getX(), 0);
    assertEquals(expected.getY(), actual.getY(), 0);
    assertEquals(expected.getZ(), actual.getZ(), 0);

    assertEquals(expected.getVx(), actual.getVx(), 0);
    assertEquals(expected.getVy(), actual.getVy(), 0);
    assertEquals(expected.getVz(), actual.getVz(), 0);

//    assertEquals(expected.getAx(), actual.getAx(), 0);
//    assertEquals(expected.getAy(), actual.getAy(), 0);
//    assertEquals(expected.getAz(), actual.getAz(), 0);
    
  }
  

  public static Tetra newCraft() {
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

}
