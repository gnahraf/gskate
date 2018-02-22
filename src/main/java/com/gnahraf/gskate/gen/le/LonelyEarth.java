/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.DynaVector;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.SphericalBodyPotential;

/**
 * A geo-centric simulation. The earth does not move about anything.
 * Newton's laws hold, but earth is alone.
 */
public class LonelyEarth extends Simulation {
  
  
  private final Constraints constraints;
  
  
  
  public LonelyEarth(Constraints constraints) {
    
    super(new SphericalBodyPotential());
    
    this.constraints = constraints.clone();
    
    if (!this.constraints.isValid())
      throw new IllegalArgumentException("invalid " + this.constraints);
    
    double r = Constants.EARTH_RADIUS + this.constraints.initKmsAboveGround*1000;
    
    double orbitalSpeed = Math.sqrt(((SphericalBodyPotential) potential).getG() / r);
    
    // our tetra starts out with each edge about 100 meters wide. One orientation for
    // the coordinates of a regular tetrahedron with edge length 2 centered about
    // the origin is
    //
    // [1,  0, -1/root(2)]
    // [-1, 0, -1/root(2)]
    // [0,  1, 1/root(2)]
    // [0, -1, 1/root(2)]
    // 
    
    double initEdgeLength = constraints.initTetherLength;
    
    double sine45 = 1.0 / Math.sqrt(2);
    
    double scale = initEdgeLength / 2;
    double scaled45 = scale*sine45;
    
    
    craft.getBob(0).setPosition( scale, 0, -scaled45);
    craft.getBob(1).setPosition(-scale, 0, -scaled45);
    craft.getBob(2).setPosition(0,  scale,  scaled45);
    craft.getBob(3).setPosition(0, -scale,  scaled45);
    
    for (int i = 0; i < 4; ++i) {
      DynaVector bob = craft.getBob(i);
      bob.setPosition(bob.getX() + r, bob.getY(), bob.getZ());
      bob.setVelocity(0, orbitalSpeed, 0);
    }
    
  }
  
  
  
  
  public void animateMillis(long millis) {
    animateMillis(millis, constraints.timeFineness);
  }

}
