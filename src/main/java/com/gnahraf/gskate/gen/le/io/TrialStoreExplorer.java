/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;

import java.util.ArrayList;

import com.gnahraf.gskate.model.CraftState;





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
  
  
  
  public void printStates() {
    ArrayList<CraftState> states = new ArrayList<>();
    store.getStateManager()
      .streamObjects()
      .forEach(states::add);
    
    
  }
  

}
