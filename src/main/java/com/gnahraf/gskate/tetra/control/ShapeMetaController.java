/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.control;


import com.gnahraf.gskate.tetra.model.Simulation;
import com.gnahraf.gskate.tetra.model.TetraShape;

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
  
  
  
  public ShapeMetaController newSnapshot(Simulation system) {
    // sanity check (but really, there are more checks to do..)
    if (system.getTime() != fuzzyControl.getSystem().getTime())
      throw new IllegalArgumentException(
          "System time mismatch " + system.getTime() + " vs. " + fuzzyControl.getSystem().getTime());
    
    ShapeFuzzyController fuzz = new ShapeFuzzyController(system);
    fuzz.copyStateFrom(fuzzyControl);
    ShapeMetaController snap = new ShapeMetaController(fuzz);
    snap.shape.copyFrom(shape);
    for (int index = 6; index-- > 0; )
      snap.lengthDeltasPerStep[index] = lengthDeltasPerStep[index];
    snap.millisPerStep = millisPerStep;
    snap.stepsRemaining = stepsRemaining;
    snap.nextSystemDecisionTime = nextSystemDecisionTime;
    return snap;
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
