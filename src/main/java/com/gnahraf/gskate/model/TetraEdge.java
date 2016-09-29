/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 *
 */
public enum TetraEdge {
  
  A(0, 0, 1),
  B(1, 0, 2),
  C(2, 0, 3),
  D(3, 1, 2),
  E(4, 1, 3),
  F(5, 2, 3);
  
  
  
  private final static TetraEdge[] EDGES = TetraEdge.values();
  

  public final int index;
  public final int loBob;
  public final int hiBob;
  
  
  private TetraEdge(int index, int loBob, int hiBob) {
    this.index = index;
    this.loBob = loBob;
    this.hiBob = hiBob;
  }
  
  
  public static TetraEdge forBobs(int bobA, int bobB) {
    
    boolean reverse = bobA > bobB;
    if (reverse) {
      int t = bobA;
      bobA = bobB;
      bobB = t;
    } else if (bobA == bobB) {
      throw new IllegalArgumentException("bobA " + bobA + ", bobB " + bobB);
    }
    
    if (bobA < 0 || bobB > 5) {
      // restore arg order for better debugging..
      if (reverse) {
        int t = bobA;
        bobA = bobB;
        bobB = t;
      }
      throw new IllegalArgumentException("bobA " + bobA + ", bobB " + bobB);
    }
    
    switch (bobA) {
    case 0:
      return EDGES[bobB - 1];
    case 1:
      return EDGES[bobB + 1];
    default:
      return EDGES[5];
    }
  }
  
  
  
  public static TetraEdge forIndex(int index) throws IndexOutOfBoundsException {
    return EDGES[index];
  }
  
  
  
  @Override
  public String toString() {
    return name() + "[" + index + ":" + loBob + "," + hiBob + "]";
  }

}
