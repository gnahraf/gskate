/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 * The system modeled. A Newtonian model, with a notion of absolute time.
 *
 */
public abstract class Universe {
  
  /**
   * Advances the state of the universe by the given time in the specified
   * number of steps--obviously with side effects.
   * 
   * @param time in seconds and always positive
   * @param steps number of steps (the more, the finer the time resolution)
   */
  public abstract void tick(double time, long steps);

}
