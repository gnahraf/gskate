/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 * The basic unit of a simulation is a function with
 * side effects. It's poor functional style, but hey, if
 * it's time we're simulating, it has side effects.
 *
 */
public abstract class Atom {
  
  public final static Atom NULL = new Atom() {
    @Override
    public void tick(TickTime time) {  }
  };
  
  
  /**
   * Advances the state of the instance.
   * 
   * @param time the time interval
   */
  public abstract void tick(TickTime time);
  

}
