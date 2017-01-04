/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.io;


import com.gnahraf.gskate.model.Tetra;

/**
 *
 */
public class CraftState {
  
  
  private final long time;
  private final Tetra craft;

  /**
   * 
   */
  public CraftState(long time, Tetra craft) {
    this.time = time;
    this.craft = craft;
    if (craft == null)
      throw new IllegalArgumentException("craft " + craft);
  }
  
  
  
  

  public long getTime() {
    return time;
  }

  public Tetra getCraft() {
    return craft;
  }

}
