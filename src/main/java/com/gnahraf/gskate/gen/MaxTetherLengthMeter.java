/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;

import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Tetra;

/**
 *
 */
public class MaxTetherLengthMeter {
  
  private final Tetra craft;
  
  private int bobA, bobB;
  private double maxTetherLenSq;
  
  
  
  public MaxTetherLengthMeter(Tetra craft) {
    this.craft = craft;
    if (craft == null)
      throw new IllegalArgumentException("null arg");
  }
  
  
  
  public void measure() {
    int a = 2;
    int b = 3;
    double maxLenSq = craft.getBob(2).distanceSq(craft.getBob(3));
    for (int i = 0; i < 2; ++i) {
      Bob bob = craft.getBob(i);
      for (int j = i + 1; j < 4; ++j) {
        double d2 = bob.distanceSq(craft.getBob(j));
        if (d2 >= maxLenSq) {
          maxLenSq = d2;
          a = i;
          b = j;
        }
      }
    }
    bobA = a;
    bobB = b;
    maxTetherLenSq = maxLenSq;
  }



  public Tetra getCraft() {
    return craft;
  }



  public int getBobA() {
    return bobA;
  }



  public int getBobB() {
    return bobB;
  }



  public double getMaxTetherLenSq() {
    return maxTetherLenSq;
  }
  
  
  

}
