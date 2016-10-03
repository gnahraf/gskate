/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.control;



import static org.junit.Assert.*;
import static com.gnahraf.gskate.gen.le.LonelyEarthTest.*;

import org.junit.Test;

import com.gnahraf.gskate.control.ShapeFuzzyController;
import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.TetraCorner;



/**
 * This is hardly an automated unit test. More like a demo.
 */
public class ShapeFuzzyControllerTest {

  @Test
  public void testConstant() {
    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
    constraints.initKmsAboveGround = 300;
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
      printTetherForces(system);
    }
  }
  

  @Test
  public void testReflation() {
    
    final double minEdgeLength = 100;
    final double maxEdgeLength = 360100;
    final int navigationCommandsPerOrbit = 72;
    final int controlMillis = 40;
    final double navEdgeLengthDelta = (maxEdgeLength - minEdgeLength) * 2 / navigationCommandsPerOrbit;

    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
    constraints.initKmsAboveGround = 1200;
    constraints.initTetherLength = minEdgeLength;
    
    LonelyEarth system = new LonelyEarth(constraints);
    
    final double initEnergy = system.getCraft().getEnergy(system.getPotential());
    final double initCmEnergy = system.getCraft().getCmEnergy(system.getPotential());
    
    ShapeFuzzyController controller = new ShapeFuzzyController(system);
    controller.freeze();
    
    final double periodMillis = roughPeriod(system) * 1000;
    final double constantShapeMillis = periodMillis / navigationCommandsPerOrbit;
    
    final long controlStepsPerConstantShape = (long) (constantShapeMillis / controlMillis);
    
    int reportLaps = 4;
    
    
    final int constantShapesPerReportLap = navigationCommandsPerOrbit / reportLaps;
    double currentEdgeLength = minEdgeLength;

    System.out.println();
    System.out.println("============");
    System.out.println("============");
    System.out.println();
    printCraft(system);
    printTetherLengths(system);
    printTetherForces(system);
    
    while (reportLaps-- > 0) {
      int deltaLengthSign = reportLaps >= 2 ? 1 : -1;
      for (int shape = constantShapesPerReportLap; shape-- > 0; ) {
        if (shape % 4 == 2) {
          printTetherLengths(system);
          printTetherForces(system);
        }
        for (long controlStep = controlStepsPerConstantShape; controlStep-- > 0; ) {
          system.animateMillis(controlMillis);
          controller.adjustTethers();
        }
        currentEdgeLength += deltaLengthSign * navEdgeLengthDelta;
        controller.setEquiEdgeLength(currentEdgeLength);
      }
      System.out.println();
      System.out.println("============");
      System.out.println("============");
      System.out.println();
      printCraft(system);
      printTetherLengths(system);
      printTetherForces(system);
    }

    final double finalEnergy = system.getCraft().getEnergy(system.getPotential());
    final double finalCmEnergy = system.getCraft().getCmEnergy(system.getPotential());
    
    double gain = finalEnergy - initEnergy;
    System.out.println();
    System.out.println("Energy gain: " + FORMAT.format(gain) + " J");
    assertTrue(gain > 0);
    
    gain = finalCmEnergy - initCmEnergy;
    System.out.println("CM Energy gain: " + FORMAT.format(gain) + " J");
//    assertTrue(gain > 0);
  }
  
  @Test
  public void testStrechCornerTarget() {

    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
    constraints.initKmsAboveGround = 1200;
    constraints.initTetherLength = 100;

    final int controlMillis = 40;

    
    LonelyEarth system = new LonelyEarth(constraints);
    
    final double initEnergy = system.getCraft().getEnergy(system.getPotential());
    final double initCmEnergy = system.getCraft().getCmEnergy(system.getPotential());
    
    ShapeFuzzyController controller = new ShapeFuzzyController(system);
    controller.freeze();
    
    final double periodMillis = roughPeriod(system) * 1000;
    
    double expansionFactor = 1.05;
    double compressionFactor = 1 / expansionFactor;
    
    // we're going to stetch the lowest lying corner 20 times..
    final int actions = 20;
    
    // ..over a period of 1/16th of orbit
    final double expansionMillis = periodMillis / 16;
    
    final int millisAcrossAction = (int) (expansionMillis / actions);
    
    TetraCorner corner = TetraCorner.forBob( system.getCraft().getLoBob(system.getPotential()) );
    
//    controller.setFuzzyTimeToTarget((int) (expansionMillis / 1000));
    
    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchCornerTarget(corner, expansionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    
    system.animateControlledMillis((int) (periodMillis * 3 / 16), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    
    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchCornerTarget(corner, compressionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    
    system.animateControlledMillis((int) (periodMillis * 3 / 16), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    system.animateControlledMillis((int) (periodMillis / 4), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    system.animateControlledMillis((int) (periodMillis / 4), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    
  }
  
  
  
  @Test
  public void testStretchCornerAndFace() {

    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
    constraints.initKmsAboveGround = 1200;
    constraints.initTetherLength = 100;

    final int controlMillis = 40;

    
    LonelyEarth system = new LonelyEarth(constraints);
    
    final double initEnergy = system.getCraft().getEnergy(system.getPotential());
    final double initCmEnergy = system.getCraft().getCmEnergy(system.getPotential());
    
    ShapeFuzzyController controller = new ShapeFuzzyController(system);
    controller.freeze();
    
    final double periodMillis = roughPeriod(system) * 1000;
    
    double cornerExpansionFactor = 1.05;
    double cornerCompressionFactor = 1 / cornerExpansionFactor;
    
    double faceExpansionFactor = 1.03;
    double faceCompressionFactor = 1 / faceExpansionFactor;
    
    // we're going to stetch the lowest lying corner 20 times..
    final int actions = 20;
    
    // ..over a period of 1/16th of orbit
    final double expansionMillis = periodMillis / 16;
    
    final int millisAcrossAction = (int) (expansionMillis / actions);
    
    TetraCorner corner = TetraCorner.forBob( system.getCraft().getLoBob(system.getPotential()) );
    
    
    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchCornerTarget(corner, cornerExpansionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    
    // then expand the opposite face the same way..
    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchFaceTarget(corner.bob(), faceExpansionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    
    system.animateControlledMillis((int) (periodMillis * 3 / 8), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    
    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchCornerTarget(corner, cornerCompressionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    

    for (int actionCountDown = actions; actionCountDown-- > 0; ) {
      system.animateControlledMillis(millisAcrossAction, constraints.timeFineness, controller, controlMillis);
      controller.stretchFaceTarget(corner.bob(), faceCompressionFactor);
      if (actionCountDown % 5 == 0)
        printInterim(initEnergy, initCmEnergy, system);
    }
    
    
    system.animateControlledMillis((int) (periodMillis * 3 / 8), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    system.animateControlledMillis((int) (periodMillis / 4), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    system.animateControlledMillis((int) (periodMillis / 4), constraints.timeFineness, controller, controlMillis);
    printReport(initEnergy, initCmEnergy, system);
    
  }
  
  
  
  private void printInterim(double initEnergy, double initCmEnergy, LonelyEarth system) {
    printTetherLengths(system);
    printTetherForces(system);

    final double finalEnergy = system.getCraft().getEnergy(system.getPotential());
    final double finalCmEnergy = system.getCraft().getCmEnergy(system.getPotential());
    
    double gain = finalEnergy - initEnergy;
    System.out.println();
    System.out.println("Energy gain: " + FORMAT.format(gain) + " J");
    
    gain = finalCmEnergy - initCmEnergy;
    System.out.println("CM Energy gain: " + FORMAT.format(gain) + " J");
    
  }
  
  
  private void printReport(double initEnergy, double initCmEnergy, LonelyEarth system) {

    System.out.println();
    System.out.println("============");
    System.out.println("============");
    System.out.println();
    printCraft(system);
    printInterim(initEnergy, initCmEnergy, system);
  }

}
