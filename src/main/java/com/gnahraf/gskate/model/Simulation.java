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
  
  

  
  
  /**
   * Returns the animation time in milliseconds.
   */
  public long getTime() {
    return time;
  }
  
  /**
   * Sets the animation time in milliseconds.
   */
  public void setTime(long time) {
    this.time = time;
  }
  
  
  
  public Potential getPotential() {
    return potential;
  }
  
  
  public Tetra getCraft() {
    return craft;
  }
  
  
  
  public void animateMillis(long millis, double timeResolution) {
    double seconds = millis;
    seconds /= 1000;
    craft.animate(potential, seconds, timeResolution);
    time += millis;
  }

}
