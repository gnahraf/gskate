/*
 * Copyright 2016 Babak Farhang
 */

package com.gnahraf.util.tree;


import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.util.tree.RegionProgression;



/**
 *
 */
public class RegionProgressionTest {

  @Test
  public void testTrivial() {
    RegionProgression root = new RegionProgression(1, 1);
    assertRoot(root);
    assertTrue(root.equals(root));
    

    assertEquals(1, root.children());
    
    RegionProgression child = root.child(0);
    assertLeaf(child, 0, 0);
    assertLastChild(child);
    
    assertEquals(child, root.child(0));
    assertTerminalBranch(root);
  }
  
  
  @Test
  public void test2x1() {
    RegionProgression root = new RegionProgression(2, 1);
    assertRoot(root);

    assertEquals(2, root.children());
    
    RegionProgression child = root.child(0);
    assertLeaf(child, 0, 0);
    
    RegionProgression nextChild = child.nextSibling();
    assertEquals(child.parent().child(child.siblingIndex() + 1), nextChild);
    
    assertLeaf(nextChild, 1, 1);
    assertLastChild(nextChild);
  }
  
  
  @Test
  public void test2x2Trivial() {
    RegionProgression root = new RegionProgression(2, 2);
    assertRoot(root);
    assertTerminalBranch(root);
  }
  
  
  @Test
  public void test3x3Trivial() {
    RegionProgression root = new RegionProgression(3, 3);
    assertRoot(root);
    assertTerminalBranch(root);
  }
  
  
  @Test
  public void test3x2() {
    RegionProgression root = new RegionProgression(3, 2);
    assertRoot(root);
    
    assertEquals(2, root.children());
    
    RegionProgression node = root.child(0);
    assertEquals(0, node.siblingIndex());
    assertEquals(0, node.region());
    assertEquals(2, node.children());
    
    node = node.child(0);
    assertLeaf(node, 0, 1);
    
    node = node.nextSibling();
    assertLeaf(node, 1, 2);
    
    node = root.child(1);
    assertEquals(1, node.siblingIndex());
    assertEquals(1, node.region());
    assertTerminalBranch(node);
    node = node.child(0);
    assertLeaf(node, 0, 2);
  }
  
  
  @Test
  public void test3x1() {
    RegionProgression root = new RegionProgression(3, 1);
    assertRoot(root);

    assertEquals(3, root.children());
    
    RegionProgression child = root.child(0);
    assertLeaf(child, 0, 0);
    
    child = child.nextSibling();
    
    assertLeaf(child, 1, 1);
    
    child = child.nextSibling();
    
    assertLeaf(child, 2, 2);
    assertLastChild(child);
  }
  
  
  
  
  
  
  
  
  
  private void assertLeaf(RegionProgression child, int expectedSiblingIndex, int expectedRegion) {
    assertFalse(child.isRoot());
    assertTrue(child.isLeaf());
    assertEquals(0, child.children());
    assertEquals(expectedSiblingIndex, child.siblingIndex());
    assertEquals(expectedRegion, child.region());
    assertEquals(child, child.parent().child(expectedSiblingIndex));
  }
  
  
  private void assertLastChild(RegionProgression child) {
    assertTrue(child.isLastChild());
    assertNull(child.nextSibling());
    assertEquals(child.parent().children(), child.siblingIndex() + 1);
  }
  
  
  private void assertRoot(RegionProgression root) {
    assertSame(root, root.parent());
    assertTrue(root.isRoot());
    assertFalse(root.isLeaf());
    assertTrue(root.isLastChild());
    assertNull(root.nextSibling());
    assertEquals(-1, root.siblingIndex());
    assertEquals(0, root.level());
  }
  
  
  private void assertTerminalBranch(RegionProgression node) {
    while (!node.isLeaf()) {
      assertEquals(1, node.children());
      node = node.child(0);
      assertLastChild(node);
    }
  }

}
