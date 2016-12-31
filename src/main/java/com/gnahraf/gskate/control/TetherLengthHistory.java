/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.control;

import com.gnahraf.gskate.gen.io.TetherHistory;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.TetraShape;

/**
 * Records the history of the {@linkplain ShapeFuzzyController}
 */
public class TetherLengthHistory implements ShapeListener {

  
  private final Simulation system;
  private final TetherHistory history;
  
  
 
  public TetherLengthHistory(Simulation system, TetherHistory history) {
    this.system = system;
    this.history = history == null ? new TetherHistory() : history;
    
    if (system == null)
      throw new IllegalArgumentException("null system");
    
    if (history.getLastTimestamp() > system.getTime())
      throw new IllegalArgumentException(
          "history.getLastTimestamp() " + history.getLastTimestamp() +
          " > system.getTime() " + system.getTime());
  }
  
  
  
  public void shapeChanged(TetraShape newShape) {
    long time = system.getTime();
    for (int edge = 0; edge < 6; ++edge)
      history.add(time, edge, newShape.length(edge));
  }
  
  
  public TetherHistory getHistory() {
    return history;
  }

}
