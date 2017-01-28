/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * Potential energy function. Note this is generally a time-varying
 * function, itself often a product of a simulation (as in a
 * multibody problem).
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
  
  
  
  /**
   * Updates this instance with the passage of the given <tt>seconds</tt>.
   * This step may involve a side-simulation.
   * 
   * @param seconds a non-negative value, typically very close to zero,
   *                depending on the time-resolution of the simulation.
   */
  public abstract void update(double seconds);
  
  
  
  public abstract Potential clone();



}
