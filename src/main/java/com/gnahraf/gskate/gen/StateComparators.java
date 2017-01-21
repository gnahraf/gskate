/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen;


import java.util.Comparator;

import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.gskate.model.Potential;
import com.gnahraf.util.BaseComparator;

/**
 *
 */
public class StateComparators {

  private StateComparators() {  }
  
  
  
  
  public static Comparator<CraftState> newCmEnergyComparator(final Potential potential) {
    return
        new BaseComparator<CraftState>() {

          @Override
          public int compare(CraftState a, CraftState b) {
            double eA = a.getCmEnergy(potential);
            double eB = b.getCmEnergy(potential);
            
            return eA > eB ? 1 : eA < eB ? -1 : 0;
          }
        };
  }
  
  
  
  public static Comparator<CraftState> newRotationalEnergyComparator() {
    return
        new BaseComparator<CraftState>() {

          @Override
          public int compare(CraftState a, CraftState b) {
            double eA = a.getRotationalEnergy();
            double eB = b.getRotationalEnergy();
            
            return eA > eB ? 1 : eA < eB ? -1 : 0;
          }
        };
  }

}
