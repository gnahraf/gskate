/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.poc;

import com.gnahraf.gskate.poc.BigPlanet.AnimationListener;

/**
 *
 */
public class SeparationFuzzyController implements AnimationListener {
  
  private final BigPlanet system;
  
  private volatile int targetDistance = 100;
  
  private int maxAcceleration = 100;
  private int fuzzyTimeToTarget = 30;
  
  
  public SeparationFuzzyController(BigPlanet system) {
    this.system = system;
    system.addAnimationListener(this);
  }

  public int getTargetDistance() {
    return targetDistance;
  }

  public void setTargetDistance(int targetDistance) {
    if (targetDistance < 0)
      throw new IllegalArgumentException("targetDistance " + targetDistance);
    this.targetDistance = targetDistance;
  }
  
  
  
  
  private void adjustTether() {
    Blobule a = system.getA();
    Blobule b = system.getB();
    double abX = b.getX() - a.getX();
    double abY = b.getY() - a.getY();
    double abLen = Math.sqrt(abX*abX + abY*abY);
    
    double targetDelta = targetDistance - abLen;
    if (abLen < .1)
      throw new IllegalStateException("abLen " + abLen);
    
    // make abX, abY unit vector along ab
    abX /= abLen;
    abY /= abLen;
    
    // get the relative velocity vector
    double vx = b.getVx() - a.getVx();
    double vy = b.getVy() - a.getVy();
    
    // take its dot product along ab, the speed from a to b
    double abV = vx*abX + vy*abY;
    
    double targetSpeed = targetDelta / fuzzyTimeToTarget;
    double targetSpeedDelta = targetSpeed - abV;
    
    double accelerationToTargetSpeed = 3 * targetSpeedDelta / fuzzyTimeToTarget;
    if (Math.abs(accelerationToTargetSpeed) > maxAcceleration)
      accelerationToTargetSpeed = maxAcceleration * Math.signum(accelerationToTargetSpeed);
    system.setTetheredAccel(accelerationToTargetSpeed);
  }

  public void blobulesMoved(double ax, double ay, double bx, double by) {
    adjustTether();
  }

}
