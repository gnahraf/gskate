/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.control;


import com.gnahraf.gskate.tetra.model.TetraShape;

/**
 * Callback for a change in state of a {@linkplain TetraShape}.
 */
public interface ShapeListener {
  
  void shapeChanged(TetraShape shape);

}
