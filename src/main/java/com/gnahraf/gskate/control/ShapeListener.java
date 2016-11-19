/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;


import com.gnahraf.gskate.model.TetraShape;

/**
 * Callback for a change in state of a {@linkplain TetraShape}.
 */
public interface ShapeListener {
  
  void shapeChanged(TetraShape shape);

}
