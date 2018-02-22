/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.sim;

/**
 *
 */
public class TwoStepSystem extends CheckedUniverse {

  /* (non-Javadoc)
   * @see com.gnahraf.sim.Atom#tick(double, long)
   */
  @Override
  protected void tickImpl(double time, long steps) {
    final double timePerStep = time / steps;
    while (steps-- > 0) {
      updateForces();
      animate(timePerStep);
    }
  }

  protected void updateForces() {
    // TODO Auto-generated method stub
    
  }

  protected void animate(double timePerStep) {
    // TODO Auto-generated method stub
    
  }

}
