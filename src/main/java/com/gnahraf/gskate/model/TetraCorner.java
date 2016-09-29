/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 *
 */
public enum TetraCorner {
  
  UP(0),
  DOWN(1),
  LEFT(2),
  RIGHT(3);
  
  
  
  
  private final static TetraCorner[] CORNERS = TetraCorner.values();
  
  private final int bob;
  private final int[] adjacentBobs;
  private final TetraEdge[] edges;
  
  private TetraCorner(int bob) {
    this.bob = bob;
    adjacentBobs = new int[3];
    edges = new TetraEdge[3];
    int ai = 0;
    for (int i = 0; i < 4; ++i) {
      if (i == bob)
        continue;
      adjacentBobs[ai] = i;
      edges[ai] = TetraEdge.forBobs(bob, i);
      ++ai;
    }
  }
  
  
  
  public int adjacentBob(int orderIndex) throws IndexOutOfBoundsException {
    return adjacentBobs[orderIndex];
  }
  
  
  public TetraEdge edge(int orderIndex) throws IndexOutOfBoundsException {
    return edges[orderIndex];
  }
  
  
  
  public int bob() {
    return bob;
  }
  
  
  
  
  public static TetraCorner forBob(int bob) throws IndexOutOfBoundsException {
    return CORNERS[bob];
  }

}
