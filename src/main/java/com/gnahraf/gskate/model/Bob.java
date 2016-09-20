/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * A bob of mass 1 kg. Or if you prefer, this is a per kilogram calculation.
 */
public class Bob {
  
  private double x, y, z;
  private double vx, vy, vz;
  private double ax, ay, az;
  
  
  public void setPosition(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void setVelocity(double vx, double vy, double vz) {
    this.vx = vx;
    this.vy = vy;
    this.vz = vz;
  }
  
  public void addAcceleration(double dax, double day, double daz) {
    setAcceleration(ax+dax, ay+day, az+daz);
  }
  
  
  public void clearAcceleration() {
    setAcceleration(0, 0, 0);
  }
  
  public void setAcceleration(double ax, double ay, double az) {
    this.ax = ax;
    this.ay = ay;
    this.az = az;
  }
  
  
  public void animate(double dt) {
    double dvx = ax * dt;
    x += (vx + dvx/2) * dt;
    vx += dvx;
    
    double dvy = ay * dt;
    y += (vy + dvy/2) * dt;
    vy += dvy;
    
    double dvz = az * dt;
    z += (vz + dvz/2) * dt;
    vz += dvz;
  }



  public double getX() {
    return x;
  }


  public double getY() {
    return y;
  }


  public double getZ() {
    return z;
  }
  
  



  public double getVx() {
    return vx;
  }


  public double getVy() {
    return vy;
  }


  public double getVz() {
    return vz;
  }
  
  
  public double getV2() {
    return vx*vx + vy*vy + vz*vz;
  }
  
  
  public double getV() {
    return Math.sqrt(getV2());
  }
  
  
  public double distanceSq(double x, double y, double z) {
    double dx = this.x - x;
    double dy = this.y  - y;
    double dz = this.z - z;
    return dx*dx + dy*dy + dz*dz;
  }
  
  
  public double distance(double x, double y, double z) {
    return Math.sqrt( distanceSq(x, y, z) );
  }
  
  
  public double distance(Bob other) {
    return distance(other.x, other.y, other.z);
  }
  
  
  public double distanceSq(Bob other) {
    return distanceSq(other.x, other.y, other.z);
  }



  public double getAx() {
    return ax;
  }


  public double getAy() {
    return ay;
  }


  public double getAz() {
    return az;
  }
}
