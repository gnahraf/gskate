/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.math.r3;


/**
 * A 3D vector.
 * <p/>
 * <h4>Note on Mutator Methods</h4>
 * <p/>
 * The contract of this class is that while an instance's state may change by method invocation,
 * the <i>argument</i> of the method invoked is never mutated. (This rule applies generally across
 * all mutable classes, unless explicitly specified o.w.) Another rule of thuumb with <i>this
 * </i> class is that all mutator methods return the instance itself for invocation chaining.
 * Therefore, if a method returns something other than a <tt>Vector</tt>, then it is not mutating
 * state.
 */
public class Vector {
  
  private double x;
  private double y;
  private double z;

  
  public Vector() {
  }
  
  public Vector(double x, double y, double z) {
    set(x, y, z);
  }
  
  
  public Vector(Vector copy) {
    set(copy);
  }

  
  
  
  /**
   * Returns the magnitude (Euclidean length).
   */
  public double magnitude() {
    return Math.sqrt( magnitudeSq() );
  }
  
  public double magnitudeSq() {
    return x*x + y*y + z*z;
  }
  
  
  
  public double diffMagnitude(Vector v) {
    return Math.sqrt( diffMagnitudeSq(v) );
  }
  
  
  public double diffMagnitudeSq(Vector v) {
    return diffMagnitudeSq(v.x, v.y, v.z);
  }
  
  
  
  public double diffMagnitudeSq(double u, double v, double w) {
    double i = x - u;
    double j = y - v;
    double k = z - w;
    return i*i + j*j + k*k;
  }
  
  public double diffMagnitude(double u, double v, double w) {
    return Math.sqrt( diffMagnitude(u, v, w) );
  }
  
  
  
  /**
   * Sets this instance as the cross product of itself and the given
   * vector <tt>v</tt>. I.e. the new state is <br/
   * ><tt>this</tt> <bold>X</bold> <i>v</i>
   */
  public Vector cross(Vector v) {
    return setCross(this, v);
  }
  
  
  /**
   * Sets this instance as the cross product <i>u</i> <bold>X</bold> <i>v</i>.
   */
  public Vector setCross(Vector u, Vector v) {
    double i, j, k;
    i = u.y*v.z - u.z*v.y;
    j = u.z*v.x - u.x*v.z;
    k = u.x*v.y - u.y*v.x;
    x = i;
    y = j;
    z = k;
    return this;
  }
  
  public Vector toUnit() {
    return toMagnitude(1.0);
  }
  
  
  public Vector toMagnitude(double s) {
    double mag = magnitude();
    if (mag == 0)
      throw new IllegalStateException("mag " + mag);
    return multiply(s/mag);
  }
  
  
  public double dot(Vector v) {
    return x*v.x + y*v.y + z*v.z;
  }


  public double dot(double i, double j, double k) {
    return x*i + y*j + z*k;
  }
  
  
  
  
  public Vector add(Vector v) {
    x += v.x;
    y += v.y;
    z += v.z;
    return this;
  }
  
  
  public Vector subtract(Vector v) {
    x -= v.x;
    y -= v.y;
    z -= v.z;
    return this;
  }
  
  
  public Vector multiply(double s) {
    x *= s;
    y *= s;
    z *= s;
    return this;
  }
  
  
  /**
   * Flips the direction of this vector.
   */
  public Vector flip() {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }
  
  
  
  public Vector set(Vector copy) {
    x = copy.x;
    y = copy.y;
    z = copy.z;
    return this;
  }
  
  public Vector set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }
  
  
  
  
  
  
  
  
  
  public double getX() {
    return x;
  }

  public Vector setX(double x) {
    this.x = x;
    return this;
  }

  public double getY() {
    return y;
  }

  public Vector setY(double y) {
    this.y = y;
    return this;
  }

  public double getZ() {
    return z;
  }

  public Vector setZ(double z) {
    this.z = z;
    return this;
  }

  
  
  public Vector addX(double dx) {
    x += dx;
    return this;
  }

  public Vector addY(double dy) {
    y += dy;
    return this;
  }

  public Vector addZ(double dz) {
    z += dz;
    return this;
  }
  
  
  public Vector add(double dx, double dy, double dz) {
    x += dx;
    y += dy;
    z += dz;
    return this;
  }
  
  
  
  public final boolean equals(Vector other) {
    return
        this == other ||
        other != null &&
        x == other.x &&
        y == other.y &&
        z == other.z;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public final boolean equals(Object obj) {
    return this == obj || obj instanceof Vector && equals((Vector) obj);
  }
  
  
  

}
