/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate;

/**
 * The idea here is to negate the angular momentum of the tethered craft through
 * a series of maneovers that don't lower the dynamic energy. One strategy is to
 * lengthen the tether at opportune intervals so that a gravitational torque is
 * applied against the rotation. Consider the diagram below:
 * <pre>
 * 
 *              A o
 *                   .
 *                      .
 *                         .
 *                            .
 *                               o B
 *                               
 *                               
 *                               
 *                               
 *                               
 *                             .              .
 *                   .
 *           .
 *      .
 *  .
 * </pre>
 * In this situation, tbe net torque is clockwise because the lower blobule (B) is
 * tugged by a greater gravitational force than is blobule (A).  So the strategy here
 * is to lessen the moment when the torque is working against us--by shortening the
 * tether, and conversely amplify the moment when working for us--by lengthening the
 * tether.
 * 
 */
public class CounterRotationModulator {
  
  private final BigPlanet system;
  
  public CounterRotationModulator(BigPlanet system) {
    this.system = system;
  }
  
  
  
  

}
