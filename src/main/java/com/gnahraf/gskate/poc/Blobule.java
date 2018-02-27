/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.poc;

/**
 *
 */
public final class Blobule {
  
  // circular coordinates; planet (or other large object) at origin
  private double x;
  private double y;
  // we leave z fixed (0)
  
  private double vx;
  private double vy;
  
  
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  
  public void setVelocity(double vx, double vy) {
    this.vx = vx;
    this.vy = vy;
  }
  
  
  public void updateImpulse(double ax, double ay, double dt) {
    double dvx = ax * dt;
    x += (vx + dvx/2) * dt;
    vx += dvx;
    
    double dvy = ay * dt;
    y += (vy + dvy/2) * dt;
    vy += dvy;
  }


  public double getX() {
    return x;
  }


  public double getY() {
    return y;
  }
  
  
  public double getR() {
    return Math.sqrt(getR2());
  }
  
  
  public double getR2() {
    return x*x + y*y;
  }


  public double getVx() {
    return vx;
  }


  public double getVy() {
    return vy;
  }
  
  
  public double getV2() {
    return vx*vx + vy*vy;
  }
  
  
  public double getV() {
    return Math.sqrt(getV2());
  }

}
