/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.SphericalBodyPotential;

/**
 * A geo-centric simulation. The earth does not move about anything.
 * Newton's laws hold, but earth is alone.
 */
public class LonelyEarth extends Simulation {
  
  
  /**
   * Configuration. Everything in standard metric units unless otherwise
   * specified (e.g. kilometers instead of meters).
   */
  public static class Constraints implements Cloneable {
    
    public double initTetherLength = 100;
    public double steadyStateTetherLength = 250;
    public double maxTetherLength = 100000; // 100km
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
          initTetherLength < maxTetherLength &&

          steadyStateTetherLength > minTetherLength &&
          steadyStateTetherLength < maxTetherLength &&
          
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
    public int hashCode() {
      int hash = Double.hashCode(initTetherValue);
      hash ^= Double.hashCode(-7 * steadyStateTetherLength);
      hash ^= Double.hashCode(maxTetherLength);
      hash ^= Double.hashCode(minTetherLength);
      hash ^= Double.hashCode(minKmsAboveGround);
      hash ^= Double.hashCode(-initKmsAboveGround);
      
      hash ^= Double.hashCode(-maxTensileForce);
      hash ^= Double.hashCode(maxCompressiveForce);
      hash ^= Double.hashCode(initTetherValue + .001);
      hash ^= Double.hashCode(timeFineness);
      return hash;
    }
    
    
    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      else if (o instanceof Constraints) {
        Constraints other = (Constraints) o;
        return
            initTetherLength == other.initTetherLength &&
            steadyStateTetherLength == other.steadyStateTetherLength &&
            maxTetherLength == other.maxTetherLength &&
            minTetherLength == other.minTetherLength &&
            initKmsAboveGround == other.initKmsAboveGround &&
            maxTensileForce == other.maxTensileForce &&
            maxCompressiveForce == other.maxCompressiveForce &&
            initTetherValue == other.initTetherValue &&
            timeFineness == other.timeFineness;
      } else
        return false;
    }
    
    
    
    
    @Override
    public String toString() {
      return
          "Constraints[" +
          initTetherLength + "," +
          steadyStateTetherLength + "," +
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
