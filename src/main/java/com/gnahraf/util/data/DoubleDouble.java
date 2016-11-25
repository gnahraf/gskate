/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.data;


/**
 *
 */
public class DoubleDouble {
  
  private final double x;
  private final double y;

  /**
   * 
   */
  public DoubleDouble(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  
  public double x() {
    return x;
  }
  
  
  public double y() {
    return y;
  }
  
  
  @Override
  public final boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj instanceof DoubleDouble) {
      DoubleDouble other = (DoubleDouble) obj;
      return x() == other.x() && y() == other.y();
    }
    return false;
  }
  

  @Override
  public final int hashCode() {
    return Double.hashCode(x()) ^ Double.hashCode(y());
  }
  
  
  @Override
  public String toString() {
    return "[" + x() + "," + y() + "]";
  }

}
