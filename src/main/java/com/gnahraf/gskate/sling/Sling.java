/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling;

import com.gnahraf.gskate.model.DynaVector;
import com.gnahraf.gskate.model.PointMass;
import com.gnahraf.gskate.model.Potential;

/**
 *
 */
public class Sling {
  
  private final PointMass bobA;
  private final PointMass bobB;
  
  private double tether;
  
  
  private final Potential potential;
  
  private final DynaVector work = new DynaVector();

  /**
   * 
   */
  public Sling(double massA, double massB, Potential potential) {
    bobA = new PointMass(massA);
    bobB = new PointMass(massB);
    this.potential = potential;
    if (potential == null)
      throw new IllegalArgumentException("null potential");
  }


  public void updateForces() {
    bobA.clearAcceleration();
    bobB.clearAcceleration();
    
    DynaVector a2b = work;
    {
      a2b.copyFrom(bobB);
    }
  }
  

  /**
   * Returns the tether strength in Newtons.
   * 
   * @see #setTether(double)
   */
  public double getTether() {
    return tether;
  }
  
  
  

  


  /**
   * Sets the tether strength in Newtons.
   * 
   * @param tether +/- means attractive/repulsive (tensile/strut).
   *               (Also, note change in convention!)
   */
  public void setTether(double tether) {
    this.tether = tether;
  }



  public PointMass getBobA() {
    return bobA;
  }



  public PointMass getBobB() {
    return bobB;
  }

}
