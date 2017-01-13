/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * A simulation experiment. This abstracts away the environment.
 * (My aim is to eventually navigate in a multi-body planetary system,
 * so I'm being really hopeful here for planning ahead :)
 */
public class Simulation {
  
  
  protected final Potential potential;
  
  protected final Tetra craft;
  
  /**
   * Time in milliseconds. Starts at 0.
   */
  private long time;
  
  
  public Simulation(Potential potential) {
    this(potential, new Tetra());
  }

  /**
   * 
   */
  public Simulation(Potential potential, Tetra craft) {
    if (potential == null)
      throw new IllegalArgumentException("null potential");
    if (craft == null)
      throw new IllegalArgumentException("null craft");
    
    this.potential = potential;
    this.craft = craft;
  }
  
  
  public Simulation(Simulation copy) {
    this(copy.potential.clone());
    this.craft.copyFrom(copy.craft);
    this.time = copy.time;
  }
  
  
  
  public Simulation(Potential potential, CraftState state) {
    this(potential, state.getCraft());
    this.time = state.getTime();
  }
  
  
  @Override
  public Simulation clone() {
    return new Simulation(this);
  }
  
  
  /**
   * Returns the animation time in milliseconds.
   */
  public long getTime() {
    return time;
  }
  
  

//  /**
//   * Sets the animation time in milliseconds. (Rather not have to..)
//   */
//  public void setTime(long time) {
//    this.time = time;
//  }
  
  
  
  public Potential getPotential() {
    return potential;
  }
  
  
  public Tetra getCraft() {
    return craft;
  }
  
  
  /**
   * Returns the total energy of the craft.
   */
  public double getEnergy() {
    return craft.getEnergy(potential);
  }
  
  
  /**
   * Returns the total energy of the craft sans its rotational energy.
   * I.e. the kinetic energy calculated by averaging out the velocity
   * vectors of the craft's bobs.
   * 
   * @see Tetra#getCmKe()
   */
  public double getCmEnergy() {
    return craft.getCmEnergy(potential);
  }
  
  
  
  public void animateMillis(long millis, double timeResolution) {
    double seconds = millis;
    seconds /= 1000;
    craft.animate(potential, seconds, timeResolution);
    time += millis;
  }
  
  
  public void animateControlledMillis(long millis, double timeResolution, TetherController controller, long controlMillis) {
    if (controlMillis < 1)
      throw new IllegalArgumentException("controlMillis " + controlMillis);
    
    long controlRuns = millis / controlMillis;
    for (long countDown = controlRuns; countDown-- > 0; ) {
      animateMillis(controlMillis, timeResolution);
      controller.adjustTethers();
    }
    animateMillis(millis - controlRuns*controlMillis, timeResolution);
  }

}
