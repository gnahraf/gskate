/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;


import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Potential;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraEdge;

/**
 * A fuzzy controller for maintaining the shape of the tetra craft.
 * Thinking in terms of the shape might be better than thinking in
 * terms of the tether forces required to maintain that shape. It also
 * might simplify (limit) the ways we morph the shape. (I tried purely
 * randomized tether forces in a genetic algo approach, but the computation
 * space seems too big.)
 * <p/>
 * 
 */
public class ShapeFuzzyController {
  
  /**
   * Target lengths of the 6 edges of the tetra. So collectively, these define the size and shape.
   */
  private final double[] lengths = new double[6];
  
  /**
   * The relative speeds along the edges. This records how fast the edges were
   * expanding (+) or contracting (-). We can always compute this in the present.
   * However, the idea here is to keep track of the virtual acceleration along the
   * edge. (The acceleration is virtual, because it's from the point of view of a
   * non-inertial reference frame.)
   */
  private final double[] lengthRates = new double[6];
  
  
  private long lrTime;
  

  private final Simulation system;
  
  
  /**
   * The goal time to the target shape.
   */
  private int fuzzyTimeToTarget = 30;
  /**
   * Max push force in Newtons.
   */
  private double maxCompressiveForce = 1.0;
  /**
   * Max pull force in Newtons.
   */
  private double maxTensileForce = 100.0;
  
  
  /**
   * Defines when 2 bobs have collided (a bad thing).
   */
  private double minTetherLength = 1.0;
  
  
  
  /**
   * 
   */
  public ShapeFuzzyController(Simulation system) {
    this.system = system;
    
    if (system == null)
      throw new IllegalArgumentException("null system");
  }
  
  
  
  
  public void freeze() {
    Tetra craft = system.getCraft();
    
    Bob work = new Bob();
    
    for (int index = 0; index < 6; ++index) {
      TetraEdge edge = TetraEdge.forIndex(index);
      lengths[index] = craft.getBob(edge.loBob).distance(craft.getBob(edge.hiBob));
      lengthRates[index] = getTetherLengthRate(index, work);
    }
    lrTime = system.getTime();
  }
  
  
  
  public void adjustTethers() {
    
    // Taking the naive approach first.. see if it works
    //
    // More sophisticated approaches may include following steps..
    //  * CM acceleration vector factored out
    //  * Angular moment vector factored in
    //  * Linear optimization problem for 6 tethers with goal of minimizing some
    //    length diff function (measure)
    //
    // But like the class name suggests, we're going for fuzzy..
    
    // Gonna ignore acceleration
//    craft.updateForces(system.getPotential());
    
    if (system.getTime() <= lrTime) {
      if (system.getTime() == lrTime)
        return;
      
      throw new IllegalStateException("system time " + system.getTime() + " < lrTime " + lrTime);
    }
    
    Bob work = new Bob();
    for (int i = 0; i < 6; ++i)
      adjustTether(i, work);
    
    lrTime = system.getTime();
  }
  
  
  
  private double getTetherLengthRate(int tid, Bob work) {
    Tetra craft = system.getCraft();
    Bob diff = work;
    
    TetraEdge edge = TetraEdge.forIndex(tid);
    // make diff a relative position and relative velocity vector
    // from lo-index bob to hi-index bob
    // (we don't care about the relative acceleration)
    diff.copyFrom(craft.getBob(edge.hiBob));
    diff.subtract(craft.getBob(edge.loBob));
    
    double edgeLen = diff.distance(0, 0, 0);
    if (edgeLen < minTetherLength)
      throw new IllegalStateException("bobs collided " + edge + "; distance " + edgeLen);
    
    
    // take the dot product of the [relative] velocity vectory
    // with the [normalized] relative position vector..
    // this gives us the relative speed of the higher [index] bob relative to
    // the lower bob. (So negative speed means edge length is contracting)
    double relativeSpeed = diff.dotVelocity(diff.getX(), diff.getY(), diff.getZ()) / edgeLen;
    
    return relativeSpeed;
  }
  
  
  private void adjustTether2(int tid, Bob work) {
    Tetra craft = system.getCraft();
    Bob diff = work;
    
    TetraEdge edge = TetraEdge.forIndex(tid);
    // make diff a relative position and relative velocity vector
    // from lo-index bob to hi-index bob
    // (we don't care about the relative acceleration)
    diff.copyFrom(craft.getBob(edge.hiBob));
    diff.subtract(craft.getBob(edge.loBob));
    
    double edgeLen = diff.distance(0, 0, 0);
    if (edgeLen < minTetherLength)
      throw new IllegalStateException("bobs collided " + edge + "; distance " + edgeLen);
    
    
    // take the dot product of the [relative] velocity vectory
    // with the [normalized] relative position vector..
    // this gives us the relative speed of the higher [index] bob relative to
    // the lower bob. (So negative speed means edge length is contracting)
    double relativeSpeed = diff.dotVelocity(diff.getX(), diff.getY(), diff.getZ()) / edgeLen;
    
    double oldRelativeSpeed = lengthRates[tid];
    
    double timeDelta = (system.getTime() - lrTime) / 1000.0;
    
    double acceleration = (relativeSpeed - oldRelativeSpeed) / timeDelta;
    
    
    
    // negative edge diff means we need to contract
    double edgeDiff = lengths[tid] - edgeLen;
    
    // projected edge length change at time fuzzyTimeToTarget
    double projectedLengthChange =
        (relativeSpeed + acceleration * fuzzyTimeToTarget / 2) * fuzzyTimeToTarget;
    
    // 
    double projectedEdgeDiff = edgeDiff - projectedLengthChange;
    if (Math.abs(projectedEdgeDiff) < IGNORABLE_DELTA)
      return;

    // I'm not good at remembering signs, so..
    // negative edge length diff  means we need to pull more
    boolean pullMore;
    int deltaSign;
    if (projectedEdgeDiff < 0) {
      pullMore = true;
      deltaSign = -1;
    } else {
      pullMore = false;
      deltaSign = 1;
    }
    
    
    
    double ratio = Math.abs(projectedEdgeDiff / lengths[tid]);
    double tetherForce = craft.getTetherByIndex(tid);
    
    double deltaForce;
    
    if (ratio > 0.25)
      deltaForce = 0.05;
    else if (ratio > 0.125)
      deltaForce = 0.02;
    else if (ratio > 0.0625)
      deltaForce = 0.01;
    else if (ratio > 0.03125)
      deltaForce = 0.005;
    else
      deltaForce = 0.001;
    
    
    tetherForce += deltaSign * deltaForce;
    if (tetherForce < -maxTensileForce)
      tetherForce = -maxTensileForce;
    else if (tetherForce > maxCompressiveForce)
      tetherForce = maxCompressiveForce;
    
    craft.setTetherByIndex(tid, tetherForce);
  }
  
  
  private void adjustTether(int tid, Bob work) {
    Tetra craft = system.getCraft();
    Bob diff = work;
    
    TetraEdge edge = TetraEdge.forIndex(tid);
    // make diff a relative position and relative velocity vector
    // from lo-index bob to hi-index bob
    // (we don't care about the relative acceleration)
    diff.copyFrom(craft.getBob(edge.hiBob));
    diff.subtract(craft.getBob(edge.loBob));
    
    double edgeLen = diff.distance(0, 0, 0);
    if (edgeLen < minTetherLength)
      throw new IllegalStateException("bobs collided " + edge + "; distance " + edgeLen);
    
    
    // take the dot product of the [relative] velocity vectory
    // with the [normalized] relative position vector..
    // this gives us the relative speed of the higher [index] bob relative to
    // the lower bob. (So negative speed means edge length is contracting)
    double relativeSpeed = diff.dotVelocity(diff.getX(), diff.getY(), diff.getZ()) / edgeLen;
    
    
    // negative edge diff means we need to contract
    double edgeDiff = lengths[tid] - edgeLen;
    
    // negative target speed means we're contracting
    double targetSpeed = edgeDiff / (1.0 * fuzzyTimeToTarget);
    
    double speedDelta = targetSpeed - relativeSpeed;
    if (Math.abs(speedDelta) < IGNORABLE_DELTA)
      return;

    // I'm not good at remembering signs, so..
    // negative speed delta from target means we need to pull more
    boolean pullMore = speedDelta < 0;
    int deltaSign;
    if (speedDelta < 0) {
      pullMore = true;
      deltaSign = -1;
    } else {
      pullMore = false;
      deltaSign = 1;
    }
    
    
    
    double ratio = Math.abs(speedDelta / relativeSpeed);
    double tetherForce = craft.getTetherByIndex(tid);
    
    double deltaForce;
    
    if (ratio > 0.25)
      deltaForce = 0.05;
    else if (ratio > 0.125)
      deltaForce = 0.02;
    else if (ratio > 0.0625)
      deltaForce = 0.01;
    else if (ratio > 0.03125)
      deltaForce = 0.005;
    else
      deltaForce = 0.0001;
    
    
    tetherForce += deltaSign * deltaForce;
    if (tetherForce < -maxTensileForce)
      tetherForce = -maxTensileForce;
    else if (tetherForce > maxCompressiveForce)
      tetherForce = maxCompressiveForce;
    
    craft.setTetherByIndex(tid, tetherForce);
  }
  
  private final static double IGNORABLE_DELTA = 0.05;

}
