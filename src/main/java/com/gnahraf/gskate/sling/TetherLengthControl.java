/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling;

import com.gnahraf.gskate.model.PointMass;
import com.gnahraf.math.r3.FixedVector;
import com.gnahraf.math.r3.Vector;

/**
 * A fuzzy-like controller that maintains the length of the sling's tether by
 * adjusting its strength.
 * 
 * @see #adjustTether()
 */
public class TetherLengthControl {
  
  private final Sling craft;
  
  
  private double targetLength = 100;
  private double fuzzyTimeToTarget = 30;
  private double maxTetherStength = 100;
  private double maxTetherIncrement = maxTetherStength / 1000;
  

  /**
   * 
   */
  public TetherLengthControl(Sling craft) {
    this.craft = craft;
    if (craft == null)
      throw new IllegalArgumentException("null craft");
  }
  
  
  /**
   * Adjusts the craft's tether in an attempt to reach the target
   * length. The idea here is that the more frequently this method is
   * invoked, the better the control.
   * <p/>
   * The basic algorithm here is to calculate the desired relative acceleration
   * (deceleration) along the tether endpoints and then work backwards from
   * there to calculate the incremental change in tether force.
   * <p/>
   * Note, performance gains for optimizing this code should be minimal;
   * we cycle through control invocations much less frequently than the
   * rest of the simulation.
   */
  public void adjustTether() {
    PointMass a = craft.getBobA();
    PointMass b = craft.getBobB();

    double length = a.getPos().diffMagnitude(b.getPos());
    
    double distanceDelta = targetLength - length;
    
    
    
    /* We're trying to do a simple linear
     * 
     *    distanceDelta = a2bSpeed * t - a * t^2 / 2
     * 
     * calculation to find the appropriate a.
     * 
     * But this is complicated by the fact that the system is spinning..
     */
    
    
    
    // we want to determine the residual acceleration (net of centripetal)
    
    
    // For the purpose of this back-of-envelop comment,
    // the capitalized parameters denote those of say bob A;
    // the uncapitalized, bob B..
    //
    // V^2/R = v^2/r            ... (1)
    //
    // where
    //
    // R and r are the distances of the respective bobs
    // from center of mass, and
    //
    // V and v, the components of [relative] velocity
    // perpendicular to R` and r` (r` means vector form of r)
    //
    // Note, it's kinda cool above doesn't involve M and m (already baked into
    // the definition of R and r).
    //
    // Does equation (1) hold in an accelerated reference frame?
    // I say it must. Though our calculations are Newtonian, GR's
    // equivalence principle must still hold.
    //
    // So the idea is that we'll calculate the relative accelerations
    // of bobs A and B and then net out the centripetal components.
    // That'll give us a picture of how the current state is progressing
    // towards our desired target (in the fuzzy allotted time *fuzzyTimeToTarget)
    //

    FixedVector cmPos = craft.getCm().toFixed();  // note: this lies along the tether
    FixedVector cmVel = craft.getCmVel().toFixed();
    
    double aRadius;
    FixedVector aRadiusHat;  // points to A from the CM (or alternatively, from B)
    {
      Vector v = new Vector(a.getPos()).subtract(cmPos);
      aRadius = v.magnitude();
      aRadiusHat = v.toUnit().toFixed();
    }
    
    double bRadius = length - aRadius;
    FixedVector bRadiusHat = aRadiusHat.flip();  // points to B from A
    
    FixedVector aCentripetal = computeCentripetal(a, cmVel, aRadiusHat, aRadius);
    
    FixedVector bCentripetal = computeCentripetal(b, cmVel, bRadiusHat, bRadius);
    
    // Thus far, our calculations have been independent of the tether strength;
    // the calculated accelerations have been geometric in nature.
    
    // Let's now account for tether and gravitational [tidal] forces..
    craft.updateForces();
    

    //  Vector aAdjAcc = new Vector(a.getAcc()).subtract(aCentripetal);
    //  Vector bAdjAcc = new Vector(b.getAcc()).subtract(bCentripetal);
    //  FixedVector bRel2aAdjAcc = bAdjAcc.subtract(aAdjAcc).toFixed();
    //  double tetherLengthAcc = bRel2aAdjAcc.dot(aRadiusHat);
    //
    // the above, but slightly more efficient..
    //
    double tetherLengthAcc =
        new Vector(b.getAcc()).subtract(bCentripetal)
        .subtract(a.getAcc()).add(aCentripetal)
        .dot(aRadiusHat);

    
    // now we're only concerned with the component of
    // the velocity along the tether (the line connecting bobs A and B)
    double a2bSpeed = new Vector(b.getVel()).subtract(a.getVel()).dot(bRadiusHat);
    // so, negative *a2bSpeed* means the a2b distance is contracting
    
    double targetTetherLengthAcc;
    {
      /*
       * s = ut + at^2/2
       * a = 2(s - ut)/t^2
       *   = 2(s/t - u)/t
       */
      double t = fuzzyTimeToTarget;
      double s = distanceDelta;
      double u = a2bSpeed;
      targetTetherLengthAcc = 2*(s/t - u)/t;
    }
    
    double deltaForce = (targetTetherLengthAcc - tetherLengthAcc) * craft.getMass();
    if (Math.abs(deltaForce) > maxTetherIncrement)
      deltaForce = Math.signum(deltaForce) * maxTetherIncrement;
    
    double tether = craft.getTether() + deltaForce;
    if (tether < 0)
      tether = 0;
    else if (tether > maxTetherStength)
      tether = maxTetherStength;
    
    craft.setTether(tether);
  }
  
  
  private FixedVector computeCentripetal(
      PointMass bob, FixedVector cmVel, FixedVector radiusHat, double radius) {
    Vector cm2bobVel = new Vector(bob.getVel()).subtract(cmVel);
    double alongRadius = cm2bobVel.dot(radiusHat);
    Vector cm2aVelAxial = cm2bobVel.add(radiusHat, -alongRadius);
    return radiusHat.multiply(-cm2aVelAxial.magnitudeSq() / radius);
  }


  public double getTargetLength() {
    return targetLength;
  }


  public void setTargetLength(double targetDistance) {
    if (targetDistance < 1)
      throw new IllegalArgumentException("targetDistance " + targetDistance);
    this.targetLength = targetDistance;
  }


  public double getFuzzyTimeToTarget() {
    return fuzzyTimeToTarget;
  }


  public void setFuzzyTimeToTarget(double fuzzyTimeToTarget) {
    if (fuzzyTimeToTarget <= 0)
      throw new IllegalArgumentException("fuzzyTimeToTarget " + fuzzyTimeToTarget);
    this.fuzzyTimeToTarget = fuzzyTimeToTarget;
  }


  public double getMaxTetherStength() {
    return maxTetherStength;
  }


  public void setMaxTetherStength(double maxTetherStength) {
    if (maxTetherStength < 10)
      throw new IllegalArgumentException("maxTetherStength " + maxTetherStength);
    this.maxTetherStength = maxTetherStength;
  }


  public double getMaxTetherIncrement() {
    return maxTetherIncrement;
  }


  public void setMaxTetherIncrement(double maxTetherIncrement) {
    if (maxTetherIncrement <= 0)
      throw new IllegalArgumentException("maxTetherIncrement " + maxTetherIncrement);
    this.maxTetherIncrement = maxTetherIncrement;
  }

}
