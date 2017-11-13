/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 * A stateful atom of computation. The basic unit of a simulation is a function with
 * side effects. It's poor functional style, but hey, if
 * it's time we're simulating, it has side effects.
 * <p/>
 *
 */
public abstract class Atom {
  
  public static Atom NULL = new Atom() {
    @Override
    public void tick(double time, long steps) {  }
  };
  
  /**
   * 
   * @param time in seconds and always positive
   * @param steps number of steps (the more, the finer the time resolution)
   */
  public abstract void tick(double time, long steps);

}
