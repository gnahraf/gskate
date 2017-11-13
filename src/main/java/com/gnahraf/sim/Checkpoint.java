/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 *
 */
public abstract class Checkpoint {
  
  public final static Checkpoint NULL = new Checkpoint() {
    @Override
    public void check() {  }
  };
  
  
  
  
  
  public abstract void check();

}
