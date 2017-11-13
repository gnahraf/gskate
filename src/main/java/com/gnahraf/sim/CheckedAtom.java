/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 *
 */
public abstract class CheckedAtom extends Atom {

  @Override
  public final void tick(double time, long steps) {
    if (time <= 0 || steps <= 0)
      throw new IllegalArgumentException(time + "; " + steps);
    tickImpl(time, steps);
  }
  
  
  protected abstract void tickImpl(double time, long steps);

}
