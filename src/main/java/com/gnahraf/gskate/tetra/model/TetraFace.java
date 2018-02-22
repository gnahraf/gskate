/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.model;

/**
 *
 */
public enum TetraFace {
  
  UP(0),
  DOWN(1),
  LEFT(2),
  RIGHT(3);
  
  
  private final static TetraFace[] FACES = TetraFace.values();
  
  private final int oppositeBob;
  private final TetraEdge[] edges;

  private TetraFace(int oppositeBob) {
    this.oppositeBob = oppositeBob;
    edges = new TetraEdge[3];
    int a = (++oppositeBob) % 4;
    int b = (++oppositeBob) % 4;
    int c = (++oppositeBob) % 4;
    edges[0] = TetraEdge.forBobs(a, b);
    edges[1] = TetraEdge.forBobs(b, c);
    edges[2] = TetraEdge.forBobs(c, a);
  }
  
  
  

  
  public int oppositeBob() {
    return oppositeBob;
  }
  
  
  public TetraEdge edge(int ei) throws IndexOutOfBoundsException {
    return edges[ei];
  }
  
  
  
  public TetraCorner oppositeCorner() {
    return TetraCorner.forBob(oppositeBob);
  }
  
  
  
  
  public static TetraFace forOppositeBob(int bob) throws IndexOutOfBoundsException {
    return FACES[bob];
  }
  

}
