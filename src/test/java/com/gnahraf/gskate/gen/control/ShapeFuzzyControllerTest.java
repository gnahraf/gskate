/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.control;



import static org.junit.Assert.*;
import static com.gnahraf.gskate.gen.le.LonelyEarthTest.*;

import org.junit.Test;

import com.gnahraf.gskate.control.ShapeFuzzyController;
import com.gnahraf.gskate.gen.le.LonelyEarth;




public class ShapeFuzzyControllerTest {

  @Test
  public void test() {
    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
    LonelyEarth system = new LonelyEarth(constraints);
    
    printTetherLengths(system);
    
    ShapeFuzzyController controller = new ShapeFuzzyController(system);
    controller.freeze();
    
    final double periodMillis = roughPeriod(system) * 1000;
    
    
    final int controlMillis = 40;
    int reportLaps = 4;
    
    final long controlStepsPerLap = (long) ((periodMillis / controlMillis) / reportLaps);
    
    while (reportLaps-- > 0) {
      for (long controlStep = controlStepsPerLap; controlStep-- > 0;) {
        system.animateMillis(controlMillis);
        controller.adjustTethers();
      }
      printCraft(system);
      printTetherLengths(system);
    }
    
  }

}
