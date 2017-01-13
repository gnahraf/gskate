/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.util.list;

import java.util.AbstractList;

/**
 *
 */
public class IntArrayList extends AbstractList<Integer> {

  private final int[] array;
  
  /**
   * 
   */
  public IntArrayList(int[] array) {
    this.array = array;
    if (array == null)
      throw new IllegalArgumentException("null array");
  }

  @Override
  public Integer get(int index) {
    return array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

}
