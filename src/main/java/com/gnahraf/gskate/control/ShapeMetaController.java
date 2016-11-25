/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;


import com.gnahraf.gskate.model.TetherController;
import com.gnahraf.gskate.model.TetraShape;

/**
 *
 */
public class ShapeMetaController extends TetherController {
  
  private final ShapeFuzzyController fuzzyControl;
  
  private final TetraShape shape = new TetraShape();
  
  private final double[] lengthDeltasPerStep = new double[6];
  
  private int millisPerStep;
  private int stepsRemaining;
  private long nextSystemDecisionTime;

  /**
   * 
   */
  public ShapeMetaController(ShapeFuzzyController controller) {
    this.fuzzyControl = controller;
    if (controller == null)
      throw new IllegalArgumentException("null");
  }
  
  
  public void setTargetShape(TetraShape target, int millisFromNow, int steps) {
    if (millisFromNow < 1)
      throw new IllegalArgumentException("millisFromNow " + millisFromNow);
    if (steps < 1)
      throw new IllegalArgumentException("steps " + steps);
    if (steps > millisFromNow)
      throw new IllegalArgumentException("steps " + steps + " > millisFromNow " + millisFromNow);
    
    if (shape.equals(target) && nextSystemDecisionTime != 0)
      return;
    
    shape.copyFrom(target);
    millisPerStep = millisFromNow / steps;
    stepsRemaining = steps;
    nextSystemDecisionTime = fuzzyControl.getSystem().getTime();
    
    TetraShape controlShape = fuzzyControl.getShape();
    for (int index = 6; index-- > 0; )
      lengthDeltasPerStep[index] = (shape.length(index) - controlShape.length(index)) / steps;
  }

  
  @Override
  public void adjustTethers() {
    long now = fuzzyControl.getSystem().getTime();
    if (stepsRemaining > 0 && now >= nextSystemDecisionTime) {
      if (--stepsRemaining == 0)
        fuzzyControl.setShape(shape);
      else
        fuzzyControl.getShape().addLengths(lengthDeltasPerStep);
      
      nextSystemDecisionTime =  now + millisPerStep;
    }
    fuzzyControl.adjustTethers();
  }

}
