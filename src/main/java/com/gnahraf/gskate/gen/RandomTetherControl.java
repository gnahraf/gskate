/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;

import java.util.Random;

/**
 *
 */
public class RandomTetherControl {
  
  private final static int RAND_UNIFORM_SCALE = 1000;
  private final static double MAX_CHG_FACTOR = 0.5;
  private final static double C_SWING_FACTOR = 0.1;
  private final static double T_SWING_FACTOR = 0.5;
  
  private final double maxTensileForce;
  private final double maxCompressiveForce;
  
  
  
  public RandomTetherControl(
      double maxTensileForce, double maxCompressiveForce) {
    if (maxTensileForce <= 0)
      throw new IllegalArgumentException("maxTensileForce " + maxTensileForce);
    if (maxCompressiveForce <= 0)
      throw new IllegalArgumentException("maxCompressiveForce " + maxTensileForce);
    if (maxTensileForce < maxCompressiveForce)
      throw new IllegalArgumentException(
          "maxTensileForce " + maxTensileForce + "< maxCompressiveForce " + maxTensileForce);
    this.maxTensileForce = maxTensileForce;
    this.maxCompressiveForce = maxCompressiveForce;
  }
  
  
  /**
   * Randomly adjusts the given tether value. Negative values represent attraction
   * (tensile); positive values represent repulsion (compressive).
   */
  public double newTetherValue(double oldValue, Random rand) {
    
    double scaledValue;
    if (oldValue < 0) {
      if (-oldValue > maxTensileForce)
        throw new IllegalArgumentException("|" + oldValue + "| > max " + maxTensileForce); 
      
      scaledValue = RAND_UNIFORM_SCALE + (-oldValue / maxTensileForce) * RAND_UNIFORM_SCALE;
    } else {
      if (oldValue > maxCompressiveForce)
        throw new IllegalArgumentException(oldValue + " > max " + maxCompressiveForce);
      
      scaledValue = (oldValue / maxCompressiveForce) * RAND_UNIFORM_SCALE;
    }
    
    double newScaledValue;
    boolean incrTension = rand.nextBoolean();
    if (incrTension) {
      int scaledIncrRange = (int) ((2 * RAND_UNIFORM_SCALE - scaledValue) * MAX_CHG_FACTOR);
      if (scaledIncrRange == 0)
        return oldValue;
      int scaledIncr = rand.nextInt(scaledIncrRange + 1);
      if (scaledIncr == 0)
        return oldValue;
      newScaledValue = scaledValue + scaledIncr;
    } else {
      int scaledDecrRange = (int) (scaledValue * MAX_CHG_FACTOR);
      if (scaledDecrRange == 0)
        return oldValue;
      int scaledDecr = rand.nextInt(scaledDecrRange + 1);
      if (scaledDecr == 0)
        return oldValue;
      newScaledValue = scaledValue - scaledDecr;
    }
    
    newScaledValue -= RAND_UNIFORM_SCALE;
    double newValue;
    if (newScaledValue < 0)
      newValue = newScaledValue * maxCompressiveForce / RAND_UNIFORM_SCALE;
    else
      newValue = newScaledValue * maxTensileForce / RAND_UNIFORM_SCALE;
    
    // flip the sign
    return -newValue;
  }


  /**
   * The tether's maximum pull in Newtons.
   */
  public final double getMaxTensileForce() {
    return maxTensileForce;
  }


  /**
   * The tether's maximum push in Newtons.
   */
  public final double getMaxCompressiveForce() {
    return maxCompressiveForce;
  }

}
