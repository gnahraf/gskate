/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.util.tree;

/**
 * An enumeration of forward steps across ascending regions.
 * 
 * <h3>Model</h3>
 * If we have N regions, then they are numbered 0 thru N-1. Each instance of this class
 * represents the state of a <em>k</em>-step traversal across the numbered regions in
 * ascending order.
 */
public class RegionProgression extends TreeNode {
  
  private final int regionsRemaining;
  private final int stepsRemaining;
  
  private final RegionProgression parent;
  private final int region;
  
  /**
   * Creates a root instance.
   * 
   * @param regions the number of regions covered
   * @param steps   the number of forward steps across the regions
   */
  public RegionProgression(int regions, int steps) {
    if (steps < 1)
      throw new IllegalArgumentException("steps " + steps);
    if (regions < steps)
      throw new IllegalArgumentException("regions " + regions + " < steps " + steps);
    
    this.regionsRemaining = regions;
    this.stepsRemaining = steps;
    this.parent = this;
    this.region = -1;
  }
  
  
  private RegionProgression(RegionProgression parent, int siblingIndex) {
    this.regionsRemaining = parent.regionsRemaining - siblingIndex - 1;
    this.stepsRemaining = parent.stepsRemaining - 1;
    this.parent = parent;
    this.region = parent.region + siblingIndex + 1;
  }
  
  

  @Override
  public int children() {
    return stepsRemaining == 0 ? 0 : regionsRemaining - stepsRemaining + 1;
  }

  @Override
  public RegionProgression child(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= children())
      throw new IndexOutOfBoundsException("index " + index + "; children " + children());
    
    return new RegionProgression(this, index);
  }

  @Override
  public RegionProgression parent() {
    return parent;
  }

  @Override
  public int siblingIndex() {
    return region - parent.region - 1;
  }
  
  
  public int region() {
    return region;
  }
  
  
  @Override
  public RegionProgression nextSibling() {
    return (RegionProgression) super.nextSibling();
  }
  

}
