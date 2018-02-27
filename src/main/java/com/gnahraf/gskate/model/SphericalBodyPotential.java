/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

import com.gnahraf.math.r3.Vector;

/**
 * The simplest of potentials.
 * 
 * <h4>Not Safe Under Concuurent Access!</h4>
 * Clone a copy if you need to.
 */
public class SphericalBodyPotential extends Potential {
  
  /**
   * Mass times gravitational constant G.
   */
  private final double g;
  
  /**
   * Center of planet coordinates.
   */
  private final Vector pos = new Vector();
  
  

  // work vector recycling ok since single threaded
  private final Vector work = new Vector();
  
  

  
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
    this.pos.set(copy.pos);
  }
  

  @Override
  public void force(DynaVector bob) {
    // construct vector from bob to center of planet
    Vector s = work.set(pos).subtract(bob.getPos());
    
    double distanceSq = s.magnitudeSq();
    double force = g / distanceSq;  // force per kg
    
    // set *s* to be the force in direction of bob -> planet vector
    double distance = Math.sqrt(distanceSq);
    double scale = force / distance; // the R-cubed scale
    s.multiply(scale);
    bob.getAcc().add(s);
  }

  
  @Override
  public double pe(DynaVector bob) {
    return -g / bob.getPos().diffMagnitude(pos);
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
