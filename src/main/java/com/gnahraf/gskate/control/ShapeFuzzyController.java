/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;


import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.TetherController;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraEdge;
import com.gnahraf.gskate.model.TetraShape;

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
public class ShapeFuzzyController extends TetherController {
  

      
  /**
   * The target shape.
   */
  private final MonitoredShape shape = new MonitoredShape();
  
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
  
  
  
  
  public void copyStateFrom(ShapeFuzzyController copy) {
    this.shape.copyFrom(copy.shape);
    for (int index = 6; index-- > 0; )
      this.lengthRates[index] = copy.lengthRates[index];
    this.lrTime = copy.lrTime;
    this.fuzzyTimeToTarget = copy.fuzzyTimeToTarget;
    this.maxCompressiveForce = copy.maxCompressiveForce;
    this.maxTensileForce = copy.maxTensileForce;
    this.minTetherLength = copy.minTetherLength;
  }
  

  
  
  public void setShapeListener(ShapeListener listener) {
    shape.setListener(listener);
  }
  
  
  public ShapeListener getShapeListener() {
    return shape.getListener();
  }
  
  
  /**
   * Sets the target edge lengths to the current edge lengths.
   */
  public void freeze() {
    
    shape.copyFrom(system.getCraft().getShape());
    
    Bob work = new Bob();
    
    for (int index = 0; index < 6; ++index)
      lengthRates[index] = getTetherLengthRate(index, work);
    
    lrTime = system.getTime();
  }
  
  
  
  
  public double getCurrentEdgeLength(TetraEdge edge) {
    Tetra craft = system.getCraft();
    return craft.getBob(edge.loBob).distance(craft.getBob(edge.hiBob));
  }
  
  
  
  
  public void setEquiEdgeLength(double newLength) {
    if (newLength < minTetherLength)
      throw new IllegalArgumentException(
          "newLength " + newLength + " < minTetherLength " + minTetherLength);
    
    shape.setLengths(newLength);
  }
  
  
  
  public void setEdgeLengths(double[] edges) {
    shape.setLengths(edges);
    
  }
  
  
  public void setShape(TetraShape shape) {
    this.shape.copyFrom(shape);
  }
  
  
  public TetraShape getShape() {
    return shape;
  }
  
  
  
  @Override
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
    
    
    // take the dot product of the [relative] velocity vector
    // with the [normalized] relative position vector..
    // this gives us the relative speed of the higher [index] bob relative to
    // the lower bob. (So negative speed means edge length is contracting)
    double relativeSpeed = diff.dotVelocity(diff.getX(), diff.getY(), diff.getZ()) / edgeLen;
    
    return relativeSpeed;
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
    
    
    // take the dot product of the [relative] velocity vector
    // with the [normalized] relative position vector..
    // this gives us the relative speed of the higher [index] bob relative to
    // the lower bob. (So negative speed means edge length is contracting)
    double relativeSpeed = diff.dotVelocity(diff.getX(), diff.getY(), diff.getZ()) / edgeLen;
    
    double oldRelativeSpeed = lengthRates[tid];
    // update
    this.lengthRates[tid] = relativeSpeed;
    
    
    double timeDelta = (system.getTime() - lrTime) / 1000.0;
    
    double acceleration = (relativeSpeed - oldRelativeSpeed) / timeDelta;
    
    
    
    // negative edge diff means we need to contract
    double edgeDiff = shape.length(tid) - edgeLen;
    
    // projected edge length change at time fuzzyTimeToTarget
    double projectedLengthChange =
        (relativeSpeed + acceleration * fuzzyTimeToTarget / 2) * fuzzyTimeToTarget;
    
    // 
    double projectedEdgeDiff = edgeDiff - projectedLengthChange;
    if (Math.abs(projectedEdgeDiff) < IGNORABLE_DELTA)
      return;

    // I'm not good at remembering signs, so..
    // negative edge length diff  means we need to pull more
    int deltaSign = projectedEdgeDiff < 0 ? -1 : 1;
    
    
    
    double ratio = Math.abs( projectedEdgeDiff / shape.length(tid) );
    double tetherForce = craft.getTetherByIndex(tid);
    
    double deltaForce = fuzzyDeltaForce(ratio);
    
    tetherForce += deltaSign * deltaForce;
    if (tetherForce < -maxTensileForce)
      tetherForce = -maxTensileForce;
    else if (tetherForce > maxCompressiveForce)
      tetherForce = maxCompressiveForce;
    
    craft.setTetherByIndex(tid, tetherForce);
  }
  
  
  
  private double fuzzyDeltaForce(double ratio) {
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
    
    return deltaForce;
  }
  
  private final static double IGNORABLE_DELTA = 0.005;

  
  
  
  
  
  
  
  
  
  
  
  // Typically one-time setup properties..

  public double getMaxCompressiveForce() {
    return maxCompressiveForce;
  }



  public void setMaxCompressiveForce(double maxCompressiveForce) {
    if (maxCompressiveForce < 0)
      throw new IllegalArgumentException("maxCompressiveForce " + maxCompressiveForce);
    this.maxCompressiveForce = maxCompressiveForce;
  }



  public double getMaxTensileForce() {
    return maxTensileForce;
  }



  public void setMaxTensileForce(double maxTensileForce) {
    if (maxTensileForce <= 0)
      throw new IllegalArgumentException("maxTensileForce " + maxTensileForce);
    this.maxTensileForce = maxTensileForce;
  }



  public double getMinTetherLength() {
    return minTetherLength;
  }



  /**
   * Defines the distance at which 2 bobs are considered collided.
   */
  public void setMinTetherLength(double minTetherLength) {
    if (minTetherLength <= 0)
      throw new IllegalArgumentException("minTetherLength " + minTetherLength);
    this.minTetherLength = minTetherLength;
  }



  public int getFuzzyTimeToTarget() {
    return fuzzyTimeToTarget;
  }



  public void setFuzzyTimeToTarget(int fuzzyTimeToTarget) {
    if (fuzzyTimeToTarget < 1)
      throw new IllegalArgumentException("fuzzyTimeToTarget " + fuzzyTimeToTarget);
    this.fuzzyTimeToTarget = fuzzyTimeToTarget;
  }

  
  
  public Simulation getSystem() {
    return system;
  }
  
  
}
