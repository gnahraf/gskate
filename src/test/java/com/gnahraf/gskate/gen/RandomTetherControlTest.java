/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

/**
 *
 */
public class RandomTetherControlTest {

  @Test
  public void testA() {
    Random rand = new Random(2);
    RandomTetherControl control = new RandomTetherControl(100, 1);
    double tether = 0.2;
    System.out.println(tether);
    for (int i = 0; i < 1000; ++i) {
      tether = control.newTetherValue(tether, rand);
      if (i == 448) {
        System.out.println("D");
      }
      System.out.println(i + ".\t" + tether);
    }
  }

}
