/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.math;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class IntMathTest {

  @Test
  public void testGcd() {
    assertGcd(3, 21, 30);
    assertGcd(15, 225, 30);
    assertGcd(75, 225, 150);
    assertGcd(1, 229, 1);
    assertGcd(229, 0, 229);
    assertGcd(0, 0, 0);  // (defined this way)
    
    try {
      IntMath.gcd(6, -1);
      fail();
    } catch (IllegalArgumentException expected) {  }
  }

  private void assertGcd(int expected, int u, int v) {
    long actual = IntMath.gcd(u, v);
    assertEquals(expected, actual);
  }

}
