/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class TetraCornerTest {

  @Test
  public void test() {
    for (TetraCorner corner : TetraCorner.values()) {
      System.out.println(corner);
      assertSame(corner, TetraCorner.forBob(corner.bob()));
      for (int index = 0; index < 3; ++index) {
        TetraEdge edge = corner.edge(index);
        if (edge.loBob != corner.bob() && edge.hiBob != corner.bob())
          fail("corner " + corner + " / edge " + edge + " mismatch");
      }
    }
  }

}
