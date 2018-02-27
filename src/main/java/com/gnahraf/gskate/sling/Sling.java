/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling;


import com.gnahraf.gskate.model.PointMass;
import com.gnahraf.gskate.model.Potential;
import com.gnahraf.math.r3.Vector;

/**
 *
 */
public class Sling {
  
  private final PointMass bobA;
  private final PointMass bobB;
  
  private double tether;
  
  
  private final Potential potential;
  
  // work vector recycling ok since single threaded
  private final Vector work = new Vector();
  
  
  /**
   * Creates a new 1 kilogram sling with the specified ratio of
   * mass distribution. I imagine this is convenient since it
   * gives us a per kg view, and all calculations are linear in
   * mass anyway.
   * 
   * @param a2bRatio must be greater than 1 (bob A is the heavier one)
   */
  public static Sling new1kgSling(double a2bRatio, Potential potential) {
    if (a2bRatio < 1)
      throw new IllegalArgumentException("a2bRatio " + a2bRatio);
    // a + b = 1
    // a/b + 1 = 1/b
    // b = 1 / (a/b + 1)
    double b = 1 / (a2bRatio + 1);
    double a = 1 - b;
    return new Sling(a, b, potential);
  }

  /**
   * 
   */
  public Sling(double massA, double massB, Potential potential) {
    if (massA < massB) {
      double tmp = massA;
      massA = massB;
      massB = tmp;
    }
    bobA = new PointMass(massA);
    bobB = new PointMass(massB);
    this.potential = potential;
    if (potential == null)
      throw new IllegalArgumentException("null potential");
  }
  
  


  public void updateForces() {
    bobA.clearAcceleration();
    bobB.clearAcceleration();
    
    potential.force(bobA);
    potential.force(bobB);
    
    // (note below we *could check for the tether strength being zero,
    //  but not worthwhile since that case should be relatively rare)
    Vector a2bTether =
        work.set(bobB.getPos()).subtract(bobA.getPos()).toMagnitude(tether);
    bobA.addForce(a2bTether);
    bobB.addForce(a2bTether.flip());
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



  /**
   * If the sling is skewed, then this is the heavier bob.
   */
  public PointMass getBobA() {
    return bobA;
  }



  /**
   * If the sling is skewed, then this is the lighter bob.
   */
  public PointMass getBobB() {
    return bobB;
  }

}
