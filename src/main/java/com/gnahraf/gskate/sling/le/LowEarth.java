/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling.le;

import com.gnahraf.gskate.model.SphericalBodyPotential;
import com.gnahraf.gskate.sling.Craft;
import com.gnahraf.sim.TwoStepSystem;


/**
 *
 */
public class LowEarth extends TwoStepSystem {
  
  private final Craft craft;
  // seems redundant to declare these..
  // but they're looked up at high frequency
//  // so caching the lookup
//  private final Sling sling;
//  private final TetherLengthControl controller;

  /**
   * 
   */
  public LowEarth(Craft craft) {
    this.craft = craft;
    if (! (craft.getSling().getPotential() instanceof SphericalBodyPotential))
      throw new IllegalArgumentException("I smell a rat");
  }
  
  
  
  public Craft getCraft() {
    return craft;
  }
  

  @Override
  protected void updateForces() {
    craft.getSling().updateForces();;
  }

  @Override
  protected void animate(double timePerStep) {
    craft.getSling().animate(timePerStep);
  }

}
