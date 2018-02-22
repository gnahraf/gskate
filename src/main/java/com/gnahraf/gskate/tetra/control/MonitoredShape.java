/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.control;

import com.gnahraf.gskate.tetra.model.TetraShape;

/**
 *
 */
public class MonitoredShape extends TetraShape {
  

  private final static ShapeListener NULL_LISTENER =
      new ShapeListener() {
        public void shapeChanged(TetraShape shape) {  }
      };
  
  private ShapeListener listener;

  /**
   * 
   */
  public MonitoredShape() {
    listener = NULL_LISTENER;
  }

  /**
   * 
   */
  public MonitoredShape(ShapeListener listener) {
    setListener(listener);
  }
  
  
  
  
  public void setListener(ShapeListener listener) {
    this.listener = listener == null ? NULL_LISTENER : listener;
  }
  
  public ShapeListener getListener() {
    return listener == NULL_LISTENER ? null : listener;
  }

  @Override
  public void setLengths(double[] edges) {
    super.setLengths(edges);
    listener.shapeChanged(this);
  }

  @Override
  public void setLengths(double equiLength) {
    super.setLengths(equiLength);
    listener.shapeChanged(this);
  }

  @Override
  public void copyFrom(TetraShape other) {
    super.copyFrom(other);
    listener.shapeChanged(this);
  }
  
  
  public void silentCopyFrom(TetraShape other) {
    super.copyFrom(other);
  }

}
