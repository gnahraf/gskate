/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.tree;

/**
 *
 */
public abstract class DecisionTreeProcessor<N extends TreeNode> {
  
  
  
  
  
  private TreeNode node;

  /**
   * 
   */
  public DecisionTreeProcessor(N node) {
    this.node = node;
    if (!node.isRoot())
      throw new IllegalArgumentException(node + " not root");
    if (node.isLeaf())
      throw new IllegalArgumentException("root " + node + "is empty");
  }
  
  
  /**
   * Processes the tree in a pre-order traversal.
   */
  public void processTree() {
    node = node.child(0);
    while (true) {
      @SuppressWarnings("unchecked")
      
      boolean ok = processNode((N) node);
      if (ok) {
        node = node.nextPreOrder();
        if (node == null)
          return;
        continue;
      }

      
      while (node.isLastChild()) {
        node = node.parent();
        if (node.isRoot())
          return;
      }
      node = node.nextSibling();
    }
  }
  
  
  protected abstract boolean processNode(N node);

}
