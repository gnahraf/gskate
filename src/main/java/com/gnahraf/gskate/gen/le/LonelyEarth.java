/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;

import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.SphericalBodyPotential;
import com.gnahraf.gskate.model.Tetra;

/**
 * A geo-centric simulation. The earth does not move about anything.
 * Newton's laws hold, but earth is alone.
 */
public class LonelyEarth extends Simulation {
  
  
  public static class Constraints implements Cloneable {
    
    public double initTetherLength = 100;
    public double maxTetherLength = 1e6; // 1000km
    public double minTetherLength = 50;
    
    public double minKmsAboveGround = 300;
    public double initKmsAboveGround = 1200;
    
    public double maxTensileForce = 500;
    public double maxCompressiveForce = 1;
    
    public double initTetherValue = 0;
    public double timeFineness = 6.7e-5;
    
    public Constraints clone() {
      try {
        return (Constraints) super.clone();
      } catch (CloneNotSupportedException wtf) {
        throw new RuntimeException(wtf);
      }
    }
    
    public boolean isValid() {
      return
          initTetherLength > minTetherLength &&
          minTetherLength < maxTetherLength &&
          minTetherLength > 1 &&
          minKmsAboveGround > 50 &&
          initKmsAboveGround > minKmsAboveGround &&
          maxTensileForce > 0 &&
          maxCompressiveForce > 0 &&
          initTetherValue < maxCompressiveForce &&
          initTetherValue > -maxTensileForce &&
          timeFineness > 0 &&
          timeFineness < 0.1;
    }
    
    
    @Override
    public String toString() {
      return
          "Constraints[" +
          initTetherLength + "," +
          minTetherLength + "," +
          maxTetherLength + "," +
          minKmsAboveGround + "," +
          initKmsAboveGround + "," +
          maxTensileForce + "," +
          maxCompressiveForce + "," +
          initTetherValue + "," +
          timeFineness +
          "]";
    }
    
  }
  
  private final Constraints constraints;
  
  
  
  public LonelyEarth(Constraints constraints) {
    
    super(new SphericalBodyPotential());
    
    this.constraints = constraints.clone();
    
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
      Bob bob = craft.getBob(i);
      bob.setPosition(bob.getX() + r, bob.getY(), bob.getZ());
      bob.setVelocity(0, orbitalSpeed, 0);
    }
    
    for (int i = 0; i < 6; ++i)
      craft.setTetherByIndex(i, this.constraints.initTetherValue);
    
  }
  
  
  
  
  public void animateMillis(long millis) {
    animateMillis(millis, constraints.timeFineness);
  }

}
