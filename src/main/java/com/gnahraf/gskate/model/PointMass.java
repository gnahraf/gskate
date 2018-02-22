/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.model;

import com.gnahraf.math.r3.Vector;


/**
 * Encapsulates an "atom" of state including its inertial mass.
 */
public class PointMass extends DynaVector {
  
  private final double mass;

  /**
   * 
   */
  public PointMass(double mass) {
    super();
    if (mass <= 0)
      throw new IllegalArgumentException("mass " + mass);
    this.mass = mass;
  }

  /**
   * @param copy
   */
  public PointMass(PointMass copy) {
    super(copy);
    this.mass = copy.mass;
  }

  public final double getMass() {
    return mass;
  }
  
  
  public void addForce(double fx, double fy, double fz) {
    double dax = fx / mass;
    double day = fy / mass;
    double daz = fz / mass;
    addAcceleration(dax, day, daz);
  }
  
  
  public void addForce(Vector newtons) {
    double dax = newtons.getX() / mass;
    double day = newtons.getY() / mass;
    double daz = newtons.getZ() / mass;
    addAcceleration(dax, day, daz);
  }
  
  
  

}
