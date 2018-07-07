/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.sim;

/**
 * The main observation here is that in any simulation of a physical
 * system, we must update the derivatives of change, before effecting
 * change. If we do this globally, then we're less prone to errors,
 * systemic artifacts, etc.
 */
public abstract class TwoStepSystem extends CheckedUniverse {

  @Override
  protected void tickImpl(double time, long steps) {
    final double timePerStep = time / steps;
    while (steps-- > 0) {
      updateForces();
      animate(timePerStep);
    }
  }

  protected abstract void updateForces();

  protected abstract void animate(double timePerStep);

}
