/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim.attic;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.sim.attic.TickTime;

/**
 *
 */
public class TickTimeTest {

  @Test
  public void testConstructor() {
    
    new TickTime(1, 1);
    new TickTime(Long.MAX_VALUE, Long.MAX_VALUE);
    
    try {
      new TickTime(1, 0);
      fail();
    } catch (IllegalArgumentException expected) {    }

    try {
      new TickTime(0, 1);
      fail();
    } catch (IllegalArgumentException expected) {    }
  }
  

  @Test
  public void testEquals() {
    TickTime a = new TickTime(2, 3);
    
    assertReflexiveEquals(a, new TickTime(2, 3));
    assertReflexiveEquals(a, a);
    
    assertFalse(a.equals((Object) null));
    assertFalse(a.equals((TickTime) null));
    
    TickTime ne = new TickTime(1, 3);
    
    assertFalse(a.equals(ne));
    assertFalse(a.equals((Object) ne));
    
    ne = new TickTime(3, 2);
    
    assertFalse(a.equals(ne));
    assertFalse(a.equals((Object) ne));
    
    assertReflexiveEquals(a, new TickTime(4, 6));
    
    final long thirdMaxLong = Long.MAX_VALUE / 3;
    
    TickTime b = new TickTime(11, thirdMaxLong);
    TickTime c = new TickTime(22, thirdMaxLong * 2);
    assertReflexiveEquals(b, c);
    
    b = new TickTime(2, thirdMaxLong);
    c = new TickTime(4, thirdMaxLong * 2);
    assertReflexiveEquals(b, c);
  }
  
  
  
  @Test
  public void testHashCode() {
    TickTime a = new TickTime(2, 3);
    TickTime b = new TickTime(4, 6);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }
  
  
  @Test
  public void testCompareTo() {
    TickTime a = new TickTime(13, 31);
    assertEquals(0, a.compareTo(a));
    assertEquals(0, a.compareTo(new TickTime(13, 31)));
    
    TickTime b = new TickTime(14, 31);
    assertOrder(a, b);
    
    b = new TickTime(13, 30);
    assertOrder(a, b);

    
    final long thirdMaxLong = Long.MAX_VALUE / 3;
    
    a = new TickTime(thirdMaxLong, thirdMaxLong);
    b = new TickTime(thirdMaxLong, thirdMaxLong - 1);
    assertOrder(a, b);

    b = new TickTime(thirdMaxLong + 1, thirdMaxLong);
    assertOrder(a, b);
    

    b = new TickTime(thirdMaxLong * 2, thirdMaxLong * 2);
    assertEquals(0, a.compareTo(b));
    assertEquals(0, b.compareTo(a));
  }
  
  
  private void assertReflexiveEquals(TickTime expected, TickTime actual) {
    assertTrue(expected.equals(actual));
    assertTrue(expected.equals((Object) actual));
    assertTrue(actual.equals(expected));
    assertTrue(actual.equals((Object) expected));
  }
  
  
  private void assertOrder(TickTime lesser, TickTime greater) {
    assertTrue(lesser.compareTo(greater) < 0);
    assertTrue(greater.compareTo(lesser) > 0);
  }

}
