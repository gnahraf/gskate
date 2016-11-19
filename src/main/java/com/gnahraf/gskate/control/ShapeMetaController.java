/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;

import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.TetherController;
import com.gnahraf.gskate.model.TetraShape;

/**
 *
 */
public class ShapeMetaController extends TetherController {
  
  private final ShapeFuzzyController controller;
  
  private final TetraShape shape = new TetraShape();

  /**
   * 
   */
  public ShapeMetaController(ShapeFuzzyController controller) {
    this.controller = controller;
    if (controller == null)
      throw new IllegalArgumentException("null");
  }
  
  
  public void setTargetShape(double[] lengths, int millisFromNow, int steps) throws IndexOutOfBoundsException {
    if (millisFromNow < 1)
      throw new IllegalArgumentException("millisFromNow " + millisFromNow);
    if (steps < 1)
      throw new IllegalArgumentException("steps " + steps);
    if (steps > millisFromNow)
      throw new IllegalArgumentException("steps " + steps + " > millisFromNow " + millisFromNow);
  }

  /* (non-Javadoc)
   * @see com.gnahraf.gskate.model.TetherController#adjustTethers()
   */
  @Override
  public void adjustTethers() {
    // TODO Auto-generated method stub

  }

}
