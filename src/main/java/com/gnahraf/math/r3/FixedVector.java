/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.math.r3;

import com.gnahraf.math.matrix.Matrix;

/**
 * Immutable vector. The superclass mutator methods return
 * a new instance this class rather than a modified instance.
 * <h4>Pattern Programming Note</h4>
 * <p/>
 * I'm exploring this pattern for rendering something mutable
 * immutable, rather than the opposite approach where you begin
 * with the immutable base version and add mutability in a
 * subclass.
 * <p/>
 * To be specific, the pattern here is
 * <ol>
 * <li>All mutator methods return a mutated instance. (The fact you can
 *     chain invocations is a side benefit.)
 * </li>
 * <li>An implementation can choose whether the <i>same</i> (mutable)
 *     instance or a new instance is returned (more functional style with
 *     less side effects).
 * </li>
 * </ol>
 * <p/>
 * Moreover, the issue of the mutability with the returned instance
 * is orthogonal to item 2 above. However, if you're gonna be
 * returning new instances, there's little reason why not to codify the
 * immutability of the returned type.
 */
public class FixedVector extends Vector {

  public FixedVector(double x, double y, double z) {
    super(x, y, z);
  }

  public FixedVector(Vector copy) {
    super(copy);
  }

  public FixedVector(Matrix copy) {
    super(copy);
  }
  
  

  @Override
  public FixedVector cross(Vector v) {
    return new Vector(this).cross(v).toFixed();
  }

  @Override
  public FixedVector leftCross(Vector v) {
    return new Vector(this).leftCross(v).toFixed();
  }

  @Override
  public FixedVector setCross(Vector u, Vector v) {
    return new Vector(this).setCross(u, v).toFixed();
  }

  @Override
  public FixedVector toUnit() {
    return new Vector(this).toUnit().toFixed();
  }

  @Override
  public FixedVector toMagnitude(double s) {
    return new Vector(this).toMagnitude(s).toFixed();
  }

  @Override
  public FixedVector add(Vector v) {
    return new Vector(this).add(v).toFixed();
  }

  @Override
  public FixedVector add(Vector v, double scale) {
    return new Vector(this).add(v, scale).toFixed();
  }

  @Override
  public FixedVector subtract(Vector v) {
    return new Vector(this).subtract(v).toFixed();
  }

  @Override
  public FixedVector divide(double s) {
    return new Vector(this).divide(s).toFixed();
  }

  @Override
  public FixedVector multiply(double s) {
    return new Vector(this).multiply(s).toFixed();
  }

  @Override
  public FixedVector flip() {
    return new Vector(this).flip().toFixed();
  }

  @Override
  public FixedVector set(Vector copy) {
    return new Vector(this).set(copy).toFixed();
  }

  @Override
  public FixedVector set(double x, double y, double z) {
    return new Vector(this).set(x, y, z).toFixed();
  }

  @Override
  public FixedVector set(Matrix copy) {
    return new Vector(this).set(copy).toFixed();
  }

  @Override
  public FixedVector add(double dx, double dy, double dz) {
    return new Vector(this).add(dx, dy, dz).toFixed();
  }
  

  @Override
  public final boolean isFixed() {
    return true;
  }

  @Override
  public final FixedVector toFixed() {
    return this;
  }
  
  

}
