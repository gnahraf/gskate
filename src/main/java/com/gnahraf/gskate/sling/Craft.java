/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling;

/**
 *
 */
public class Craft {
  
  private final Sling sling;
  private final TetherLengthControl controller;

  /**
   * 
   */
  public Craft(Sling sling) {
    this.sling = sling;
    controller = new TetherLengthControl(sling);
  }

  
  
  
  
  public Sling getSling() {
    return sling;
  }

  public TetherLengthControl getController() {
    return controller;
  }

}
