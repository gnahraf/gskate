/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.data;

/**
 * A <tt>DoubleDouble</tt> whose coordinates are bound in
 * the region [0, 1]. Type introduced to eliminate bounds
 * checking.
 */
public class NormPoint extends DoubleDouble {

  /**
   * 
   */
  public NormPoint(double x, double y) {
    super(x , y);
    if (x < 0 || x > 1 || y < 0 || y > 1)
      throw new IllegalArgumentException(toString());
  }

}
