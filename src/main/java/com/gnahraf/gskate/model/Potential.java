/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * Potential energy function.
 */
public abstract class Potential {
  
  /**
   * Updates the acceleration vector of the given bob by adding the
   * gravitational force of this field.
   * 
   * @see Bob#addAcceleration(double, double, double)
   */
  public abstract void force(Bob bob);
  
  
  /**
   * Returns the potential energy of the given bob, that is, Joules per kg.
   */
  public abstract double pe(Bob bob);



}
