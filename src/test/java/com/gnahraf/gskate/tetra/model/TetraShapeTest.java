/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.model;


import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.gskate.tetra.model.TetraEdge;
import com.gnahraf.gskate.tetra.model.TetraShape;


public class TetraShapeTest {
  
  private final static double UNIT_VOLUME = Math.sqrt(2) / 12;
  private final static double TOLERANCE = 1E-9;
  
  @Test
  public void testDefault() {
    TetraShape shape = new TetraShape();
    for (int edge = 0; edge < 6; ++edge)
      assertEquals(1, shape.length(edge), 0);
  }
  
  
  @Test
  public void testDefaultVolume() {
    TetraShape shape = new TetraShape();
    assertEquals(UNIT_VOLUME, shape.getVolume(), TOLERANCE);
  }

  @Test
  public void testStretchCorner() {
    double factor = 3;
    for (int corner = 0; corner < 4; ++corner) {
      TetraShape shape = new TetraShape();
      shape.stretchCorner(corner, factor);
      for (int e = 0; e < 6; ++e) {
        TetraEdge edge = TetraEdge.forIndex(e);
        double expectedlength;
        if (edge.loBob == corner || edge.hiBob == corner)
          expectedlength = factor;
        else
          expectedlength = 1;
        assertEquals(expectedlength, shape.length(e), 0);
      }
    }
  }

}
