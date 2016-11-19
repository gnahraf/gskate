/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.list;


import java.util.AbstractList;
import java.util.List;

/**
 * List of <tt>Double</tt>s backed by an array of primitives.
 */
public class DoubleArrayList extends AbstractList<Double> {
  
  
  private final double[] array;

  /**
   * 
   */
  public DoubleArrayList(double[] array) {
    this.array = array;
    if (array == null)
      throw new IllegalArgumentException("null");
  }

  @Override
  public Double get(int index) {
    return array[index];
  }

  @Override
  public int size() {
    return array.length;
  }
  
  
  public static List<Double> asList(double[] array) {
    return new DoubleArrayList(array);
  }

}
