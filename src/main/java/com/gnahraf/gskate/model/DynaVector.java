package com.gnahraf.gskate.model;

import com.gnahraf.math.r3.Vector;



/**
 * Structure for maintaining an "atom" of dynamic state. Dynamic state comprises
 * position and velocity {@linkplain Vector vector}s; the acceleration vector is
 * (and always can be) calculated from global state of the system. So while this
 * class contains an acceleration vector, it is only used as a local buffer to
 * calculate the next state of an "atom" of state.
 * <p/>
 * Note inertial mass is <strong>not</strong> encapsulated by this class.
 * 
 *  @see #animate(double)
 *  @see PointMass
 */
public class DynaVector {
  
  private final Vector pos = new Vector();
  private final Vector vel = new Vector();
  private final Vector acc = new Vector();

//  private double x;
//  private double y;
//  private double z;
  
//  private double vx;
//  private double vy;
//  private double vz;
  
//  private double ax;
//  private double ay;
//  private double az;

  public DynaVector() {
    super();
  }

  public DynaVector(DynaVector copy) {
    copyFrom(copy);
  }
  
  
  public Vector getPos() {
    return pos;
  }
  
  
  public Vector getVel() {
    return vel;
  }
  
  
  public Vector getAcc() {
    return acc;
  }
  

  public void copyFrom(DynaVector other) {
    pos.set(other.pos);
    vel.set(other.vel);
    acc.set(other.acc);
//    x = other.x;
//    y = other.y;
//    z = other.z;
    
//    vx = other.vx;
//    vy = other.vy;
//    vz = other.vz;
    
//    ax = other.ax;
//    ay = other.ay;
//    az = other.az;
  }

  public void add(DynaVector other) {
    pos.add(other.pos);
    vel.add(other.vel);
    acc.add(other.acc);
//    x += other.x;
//    y += other.y;
//    z += other.z;
    
//    vx += other.vx;
//    vy += other.vy;
//    vz += other.vz;
    
//    ax += other.ax;
//    ay += other.ay;
//    az += other.az;
  }

  public void subtract(DynaVector other) {
    pos.subtract(other.pos);
    vel.subtract(other.vel);
    acc.subtract(other.acc);
//    x -= other.x;
//    y -= other.y;
//    z -= other.z;
    
//    vx -= other.vx;
//    vy -= other.vy;
//    vz -= other.vz;
    
//    ax -= other.ax;
//    ay -= other.ay;
//    az -= other.az;
  }

  public double dotVelocity(double i, double j, double k) {
    return vel.dot(i, j, k);
  }

  public void setPosition(double x, double y, double z) {
    pos.set(x, y, z);
//    this.x = x;
//    this.y = y;
//    this.z = z;
  }

  public void setVelocity(double vx, double vy, double vz) {
    vel.set(vx, vy, vz);
//    this.vx = vx;
//    this.vy = vy;
//    this.vz = vz;
  }

  public void addAcceleration(double dax, double day, double daz) {
    acc.add(dax, day, daz);
//    setAcceleration(ax+dax, ay+day, az+daz);
  }

  public void clearAcceleration() {
    setAcceleration(0, 0, 0);
  }

  public void setAcceleration(double ax, double ay, double az) {
    acc.set(ax, ay, az);
//    this.ax = ax;
//    this.ay = ay;
//    this.az = az;
  }

  /**
   * Advances the state of this instance by <tt>dt</tt> seconds (usually very small).
   * On invocation, the {@linkplain DynaVector#getPos() pos} and {@linkplain #getVel() vel}
   * vectors are updated, while the {@linkplain #getAcc() acc} vector is held constant.
   * Typically, following invocation, the acceleration vector is recalculated from the new
   * global state.
   * 
   * @param dt seconds
   */
  public void animate(double dt) {
    double dvx = acc.getX() * dt;
    double dx = (vel.getX() + dvx/2) * dt;
//    vx += dvx;
    
    double dvy = acc.getY() * dt;
    double dy = (vel.getY() + dvy/2) * dt;
//    vy += dvy;
    
    double dvz = acc.getZ() * dt;
    double dz = (vel.getZ() + dvz / 2) * dt;
//    vz += dvz;
    pos.add(dx, dy, dz);
    vel.add(dvx, dvy, dvz);
  }

  public double getX() {
    return pos.getX();
  }

  public double getY() {
    return pos.getY();
  }

  public double getZ() {
    return pos.getZ();
  }

  public double getVx() {
    return vel.getX();
  }

  public double getVy() {
    return vel.getY();
  }

  public double getVz() {
    return vel.getZ();
  }

  public double getV2() {
    return vel.magnitudeSq();
  }

  public double getV() {
    return vel.magnitude();
  }

  public double distanceSq(double x, double y, double z) {
    return pos.diffMagnitudeSq(x, y, z);
    
//    double dx = this.x - x;
//    double dy = this.y  - y;
//    double dz = this.z - z;
//    return dx*dx + dy*dy + dz*dz;
  }

  public double distance(double x, double y, double z) {
    return Math.sqrt( distanceSq(x, y, z) );
  }

  public double distance(DynaVector other) {
    return pos.diffMagnitude(other.pos);
//    return distance(other.x, other.y, other.z);
  }

  public double distanceSq(DynaVector other) {
    return pos.diffMagnitudeSq(other.pos);
//    return distanceSq(other.x, other.y, other.z);
  }

  public double relSpeedSq(DynaVector other) {
    return vel.diffMagnitudeSq(other.vel);
  }

  public double relSpeed(DynaVector other) {
    return Math.sqrt(relSpeedSq(other));
  }

  public double getAx() {
    return acc.getX();
  }

  public double getAy() {
    return acc.getY();
  }

  public double getAz() {
    return acc.getZ();
  }

  public final boolean equals(DynaVector other) {
    if (this == other)
      return true;
    if (other == null)
      return false;
    return
        pos.equals(other.pos) &&
        vel.equals(other.vel);
  }

  @Override
  public final boolean equals(Object o) {
    return this == o || (o instanceof DynaVector) && equals((DynaVector) o);
  }

  @Override
  public final int hashCode() {
    return (pos.hashCode() * 31) ^ vel.hashCode();
  }

}