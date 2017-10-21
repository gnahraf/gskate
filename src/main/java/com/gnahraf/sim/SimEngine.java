/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

import com.gnahraf.sim.attic.Atom;
import com.gnahraf.sim.attic.AtomScheduler;


/**
 *
 */
public class SimEngine {
  
  protected final AtomScheduler scheduler;

  /**
   * 
   */
  public SimEngine(Atom finest) {
    // TODO Auto-generated constructor stub
    scheduler = new AtomScheduler(finest);
  }

}
