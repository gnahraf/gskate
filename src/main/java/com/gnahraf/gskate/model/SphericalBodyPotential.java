/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 *
 */
public class SphericalBodyPotential extends Potential {
  
  /**
   * 
   */
  private final double g;
  
  // planet coordinates
  private double x, y, z;
  
  

  
  /**
   * Default constructor makes an earth-mass body.
   */
  public SphericalBodyPotential() {
    this(Constants.EARTH_MASS);
  }
  
  
  public SphericalBodyPotential(double mass) {
    this.g = mass * Constants.G;
  }
  

  @Override
  public void force(Bob bob) {
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
  public double pe(Bob bob) {
    return -g / bob.distance(x, y, z);
  }
  
  
  public double getG() {
    return g;
  }

}
