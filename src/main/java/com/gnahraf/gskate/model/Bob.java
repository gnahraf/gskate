/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * A bob of mass 1 kg. Or if you prefer, this is a per kilogram calculation.
 * We're using these for animation, of course. The general contract here is
 * that the physical state of the instance is encapsulated in its positional
 * and velocity vectors, and the {@linkplain Potential potential} energy.
 * The acceleration vector encoded in this class, on the other hand, should
 * properly be understood as just a work buffer that anyone can use. When you
 * need to know it, you calculate it from scratch.
 * <p/>
 * Note this class also has some rudimentary support for related vector operations.
 * The mutator methods taking another instance as argument always affect <em>this</em>
 * instance, not the argument.
 */
public class Bob {
  
  private double x, y, z;
  private double vx, vy, vz;
  private double ax, ay, az;
  
  
  public Bob() {  }
  
  
  public Bob(Bob copy) {
    copyFrom(copy);
  }
  
  
  public void copyFrom(Bob other) {
    x = other.x;
    y = other.y;
    z = other.z;
    
    vx = other.vx;
    vy = other.vy;
    vz = other.vz;
    
    ax = other.ax;
    ay = other.ay;
    az = other.az;
  }
  
  
  
  public void add(Bob bob) {
    x += bob.x;
    y += bob.y;
    z += bob.z;
    
    vx += bob.vx;
    vy += bob.vy;
    vz += bob.vz;
    
    ax += bob.ax;
    ay += bob.ay;
    az += bob.az;
  }
  
  
  public void subtract(Bob bob) {
    x -= bob.x;
    y -= bob.y;
    z -= bob.z;
    
    vx -= bob.vx;
    vy -= bob.vy;
    vz -= bob.vz;
    
    ax -= bob.ax;
    ay -= bob.ay;
    az -= bob.az;
  }
  
  
  public double dotVelocity(double i, double j, double k) {
    return vx*i + vy*j + vz*k;
  }
  
  
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
  
  
  
  public double relSpeedSq(Bob other) {
    double dvx = vx - other.vx;
    double dvy = vy - other.vy;
    double dvz = vz - other.vz;
    
    return dvx*dvx + dvy*dvy + dvz*dvz;
  }
  
  
  public double relSpeed(Bob other) {
    return Math.sqrt(relSpeedSq(other));
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
