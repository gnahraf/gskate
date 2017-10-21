/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim.attic;


/**
 * The basic unit of a simulation is a function with
 * side effects. It's poor functional style, but hey, if
 * it's time we're simulating, it has side effects.
 */
public abstract class Atom {
  
  public final static Atom NULL =
      new Atom() {
        @Override
        public void tick(long count, long ticksPerSecond) {
//          if (count < 0)
//            throw new IllegalArgumentException("count " + count);
        }
        @Override
        public void tick(long ticksPerSecond) {  }
      };
  
  
      



      
  
      
      
  public void tick(long count, long ticksPerSecond) {
//    if (count < 0)
//      throw new IllegalArgumentException("count " + count);
    while (count-- > 0)
      tick(ticksPerSecond);
  }
  
  
  
  public abstract void tick(long ticksPerSecond);

}
