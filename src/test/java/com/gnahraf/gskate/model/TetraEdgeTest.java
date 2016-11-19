/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class TetraEdgeTest {

  @Test
  public void test() {
    for (TetraEdge e : TetraEdge.values()) {
      System.out.println(e);
      assertSame(e, TetraEdge.forIndex(e.index));
      assertSame(e, TetraEdge.forBobs(e.loBob, e.hiBob));
      assertSame(e, TetraEdge.forBobs(e.hiBob, e.loBob));
    }
  }
  
  

}
