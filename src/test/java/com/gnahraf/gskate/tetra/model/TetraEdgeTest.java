/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.model;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.gnahraf.gskate.tetra.model.TetraEdge;

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
      assertTrue(e.loBob < e.hiBob);
      assertTrue(e.loBob >= 0);
      assertTrue(e.hiBob < 4);
    }
    HashMap<Integer, HashSet<Integer>> mappings = new HashMap<>();
    for (int index = 0; index < 6; ++index) {
      TetraEdge e = TetraEdge.forIndex(index);
      mapBobs(mappings, e.loBob, e.hiBob);
      mapBobs(mappings, e.hiBob, e.loBob);
    }
    assertEquals(4, mappings.keySet().size());
    for (int bob = 0; bob < 4; ++bob) {
      assertEquals(3, mappings.get(bob).size());
      assertFalse(mappings.get(bob).contains(bob));
    }
  }
  
  
  private void mapBobs(HashMap<Integer, HashSet<Integer>> mappings, int src, int dtn) {
    Integer source = src;
    HashSet<Integer> dtns = mappings.get(source);
    if (dtns == null) {
      dtns = new HashSet<>();
      mappings.put(source, dtns);
    }
    dtns.add(dtn);
  }
  
  

}
