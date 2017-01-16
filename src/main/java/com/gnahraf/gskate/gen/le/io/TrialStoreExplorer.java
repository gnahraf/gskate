/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;





/**
 *
 */
public class TrialStoreExplorer {
  
  
  private final TrialStore store;

  
  /**
   * 
   */
  public TrialStoreExplorer(TrialStore store) {
    this.store = store;
    
    if (store == null)
      throw new IllegalArgumentException("null store");
  }
  
  
  

}
