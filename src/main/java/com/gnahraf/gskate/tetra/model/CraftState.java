/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.tetra.model;

import com.gnahraf.gskate.model.DynaVector;
import com.gnahraf.gskate.model.Potential;



/**
 * An immutable snapshot of a <tt>Tetra</tt> craft's state.
 * 
 * <h4>Note on mutable objects as a design choice</h4>
 * 
 * I don't know how to do animation in a functional style, i.e. without side-effects,
 * using immutable types, and still be reasonably efficient. So in order to create
 * immutability, we're reduced to copying. It doesn't matter here, because these are
 * not high churn objects.
 */
public class CraftState {
  
  
  private final long time;
  private final Tetra craft;

  /**
   * Constructor makes a defensive copy of the <tt>craft</tt>.
   */
  public CraftState(long time, Tetra craft) {
    this.time = time;
    this.craft = new Tetra(craft);
  }
  
  
  
  

  public final long getTime() {
    return time;
  }

  
  /**
   * Returns a copy of the craft.
   */
  public final Tetra getCraft() {
    return new Tetra(craft);
  }
  
  
  
  public final boolean equals(CraftState other) {
    if (this == other)
      return true;
    if (other == null)
      return false;
    return time == other.time && craft.equals(other.craft);
  }
  
  
  @Override
  public final boolean equals(Object o) {
    return this == o || o instanceof CraftState && equals((CraftState) o);
  }
  
  
  @Override
  public int hashCode() {
    return Long.hashCode(time) ^ craft.hashCode();
  }
  
  
  
  public double getEnergy(Potential potential) {
    return craft.getEnergy(potential);
  }
  
  
  public double getCmEnergy(Potential potential) {
    return craft.getCmEnergy(potential);
  }
  
  
  public double getPe(Potential potential) {
    return craft.getPe(potential);
  }
  
  
  public double getRotationalEnergy() {
    return craft.getRotationalEnergy();
  }
  
  
  public DynaVector getCmBob() {
    return craft.newCmBob();
  }

}
