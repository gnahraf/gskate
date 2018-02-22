/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * The simplest of potentials.
 */
public class SphericalBodyPotential extends Potential {
  
  /**
   * Mass times gravitational constant G.
   */
  private final double g;
  
  // planet coordinates
  private double x, y, z;
  
  

  
  /**
   * Default constructor makes an earth-mass body.
   * Don't change this, o.w. it'll break repeatibility of trials.
   */
  public SphericalBodyPotential() {
    this(Constants.EARTH_MASS);
  }
  
  
  public SphericalBodyPotential(double mass) {
    this.g = mass * Constants.G;
    if (mass <= 0)
      throw new IllegalArgumentException("mass " + mass);
  }
  
  
  public SphericalBodyPotential(SphericalBodyPotential copy) {
    this.g = copy.g;
    this.x = copy.x;
    this.y = copy.y;
    this.z = copy.z;
  }
  

  @Override
  public void force(DynaVector bob) {
    // construct vector from bob to center of planet
    double dx = x - bob.getX();
    double dy = y - bob.getY();
    double dz = z - bob.getZ();
    
    double distanceSq = dx*dx + dy*dy + dz*dz;
    double force = g / distanceSq;
    
    // normalize bob -> planet vector
    double distance = Math.sqrt(distanceSq);
    dx /= distance;
    dy /= distance;
    dz /= distance;
    
    double ax = dx * force;
    double ay = dy * force;
    double az = dz * force;
    
    bob.addAcceleration(ax, ay, az);
  }

  
  @Override
  public double pe(DynaVector bob) {
    return -g / bob.distance(x, y, z);
  }
  
  
  /**
   * Noop. Machinery for this to be developed elsewhere.
   */
  @Override
  public void update(double seconds) {  }
  
  
  public double getG() {
    return g;
  }


  @Override
  public SphericalBodyPotential clone() {
    return new SphericalBodyPotential(this);
  }

}
