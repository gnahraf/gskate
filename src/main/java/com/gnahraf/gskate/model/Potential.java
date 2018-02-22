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
   * Updates the acceleration vector of the given thing by adding the
   * gravitational acceleration (force) induced by this field.
   * 
   * @see DynaVector#addAcceleration(double, double, double)
   */
  public abstract void force(DynaVector bob);
  
  
  /**
   * Returns the potential energy of the given thing in Joules per kg.
   */
  public abstract double pe(DynaVector bob);
  
  
  
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
