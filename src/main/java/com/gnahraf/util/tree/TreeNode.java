/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.tree;

/**
 *
 */
public abstract class TreeNode {
  
  private final static int HASH_FUDGE = 37;

  
  public abstract int children();
  
  
  public boolean isLeaf() {
    return children() == 0;
  }
  
  
  /**
   * Returns the child at the given <tt>index</tt>. For a large tree, an implementation
   * ought create the returned node on demand on not maintain a reference a to it; o.w.
   * you might run out of memory.
   */
  public abstract TreeNode child(int index) throws IndexOutOfBoundsException;
  
  /**
   * Returns the parent node, or <tt>this</tt>, if this instance
   * is the root node. Note, if the returned node is root, it still
   * need not be the <em>same</em> root instance that is returned on
   * subsequent invocations.
   */
  public abstract TreeNode parent();
  
  public boolean isRoot() {
    return this == parent();
  }
  
  /**
   * Returns <tt>true</tt> if this instance is the last child of its parent.
   * <em>The root node is its own last child and returns </em><tt>true</tt>.
   */
  public boolean isLastChild() {
    return isRoot() || parent().children() - siblingIndex() == 1;
  }
  
  
  public TreeNode nextSibling() {
    return isLastChild() ? null : parent().child(siblingIndex() + 1);
  }
  
  
  
  /**
   * Returns the next node in pre-order, or <tt>null</tt>, if <tt>this</tt>
   * is the last such node in a pre-order traversal.
   */
  public TreeNode nextPreOrder() {
    if (!isLeaf())
      return child(0);
    
    
    TreeNode node = this;
    do {
      
      TreeNode next = node.nextSibling();
      if (next != null)
        return next;
      
      node = node.parent();
    
    } while (!node.isRoot());
    
    return null;
  }
  
  
  
  /**
   * Returns the index from {@linkplain #parent()}.{@linkplain #child(int)}
   * that retrieves this instance, or -1 is this instance is root.
   */
  public abstract int siblingIndex();
  
  
  public int level() {
    int level = 0;
    for (TreeNode n = this; !n.isRoot(); n = n.parent())
      ++level;
    return level;
  }
  
  
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (isRoot())
      return false;
    else if (obj instanceof TreeNode) {
      TreeNode other = (TreeNode) obj;
      return siblingIndex() == other.siblingIndex() && parent().equals(other.parent());
    } else
      return false;
  }
  
  
  
  @Override
  public int hashCode() {
    return isRoot() ? super.hashCode() : (parent().hashCode() ^ siblingIndex()) * HASH_FUDGE;
  }

}
