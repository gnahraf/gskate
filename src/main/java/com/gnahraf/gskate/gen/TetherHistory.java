/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;

import java.util.ArrayList;

/**
 *
 */
public class TetherHistory {
  
  private final static int INIT_CAPACITY = 16;
  
  /**
   * Event timestamps in millis.
   */
  private long[] timestamps;
  private int[] tetherIds;
  private double[] tetherValues;
  
  private int index;
  
  
  
  public TetherHistory() {
    timestamps = new long[INIT_CAPACITY];
    tetherIds = new int[INIT_CAPACITY];
    tetherValues = new double[INIT_CAPACITY];
  }
  
  
  
  private void ensureCapacity() {
    if (index == timestamps.length) {
      int cap = timestamps.length * 2;
      long[] stamps = new long[cap];
      int[] ids = new int[cap];
      double[] values = new double[cap];
      for (int i = index; i-- > 0; ) {
        stamps[i] = timestamps[i];
        ids[i] = tetherIds[i];
        values[i] = tetherValues[i];
      }
      timestamps = stamps;
      tetherIds = ids;
      tetherValues = values;
    }
  }

}
