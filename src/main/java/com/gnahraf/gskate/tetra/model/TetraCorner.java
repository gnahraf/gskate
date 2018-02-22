/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.model;

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
  private final TetraEdge[] edges;
  
  private TetraCorner(int bob) {
    this.bob = bob;
    edges = new TetraEdge[3];
    for (int i = 0; i < 3; ++i) {
      int adjacentBob = (bob + i + 1) % 4;
      edges[i] = TetraEdge.forBobs(bob, adjacentBob);
    }
  }
  
  
  
  
  
  public TetraEdge edge(int orderIndex) throws IndexOutOfBoundsException {
    return edges[orderIndex];
  }
  
  
  
  public int bob() {
    return bob;
  }
  
  
  public TetraFace oppositeFace() {
    return TetraFace.forOppositeBob(bob);
  }
  
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder(16);
    str.append(name()).append('[').append(bob).append(':');
    str.append(edge(0).index).append(',');
    str.append(edge(1).index).append(',');
    str.append(edge(2).index).append(']');
    
    return str.toString();
  }
  
  public static TetraCorner forBob(int bob) throws IndexOutOfBoundsException {
    return CORNERS[bob];
  }

}
