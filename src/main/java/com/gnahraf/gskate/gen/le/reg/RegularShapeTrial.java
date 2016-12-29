/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.reg;


import java.util.ArrayList;
import java.util.List;

import com.gnahraf.gskate.control.ShapeFuzzyController;
import com.gnahraf.gskate.control.ShapeMetaController;
import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraShape;
import com.gnahraf.util.data.NormPoint;

/**
 *
 */
public class RegularShapeTrial {
  
  private final List<NormPoint> commandsReceived = new ArrayList<NormPoint>();

  



  private final LonelyEarth.Constraints config;
  
  private final Simulation system;
  
//  private final ShapeFuzzyController fuzzyControl;
  
  private final ShapeMetaController controller;
  
  private final int periodMillis;


  private final long initSystemTime;
  private final double initEnergy;
  private final double initCmEnergy;
  private final double initRadius;
  
  private int controlMillis = 40;
  
  private int controlStepsPerProfilePoint = 1000;
  
  private RuntimeException error;
  
  // should later be encapsulated in an object all its own, these 2..
  private long snapSystemTime;
  private Tetra snapCraft = new Tetra();

  /**
   * 
   */
  public RegularShapeTrial(LonelyEarth.Constraints constraints) {
    this.config = constraints.clone();
    if (!config.isValid())
      throw new IllegalArgumentException(config.toString());
    this.system = new LonelyEarth(config);
    this.initSystemTime = system.getTime();
    ShapeFuzzyController fuzzyControl = new ShapeFuzzyController(system);
    fuzzyControl.setMinTetherLength(config.minTetherLength);
    fuzzyControl.setMaxCompressiveForce(config.maxCompressiveForce);
    fuzzyControl.setMaxTensileForce(config.maxTensileForce);
    fuzzyControl.freeze();
    this.controller = new ShapeMetaController(fuzzyControl);
    periodMillis = (int) (1000 * estimateInitPeriod());
    initEnergy = getEnergy();
    initCmEnergy = getCmEnergy();
    initRadius = system.getCraft().newCmBob().distance(0, 0, 0);
    updateSnapshot();
  }
  
  
  
  public RegularShapeTrial(RegularShapeTrial copy) {
    this.commandsReceived.addAll(copy.commandsReceived);
    this.config = copy.config.clone();
    this.system = copy.system.clone();
    this.controller = copy.controller.newSnapshot(this.system);
    this.periodMillis = copy.periodMillis;
    this.initSystemTime = copy.initSystemTime;
    this.initEnergy = copy.initEnergy;
    this.initCmEnergy = copy.initCmEnergy;
    this.initRadius = copy.initRadius;
    
    this.controlMillis = copy.controlMillis;
    this.controlStepsPerProfilePoint = copy.controlStepsPerProfilePoint;
    
    this.snapSystemTime = copy.snapSystemTime;
    this.snapCraft.copyFrom(copy.snapCraft);
  }
  
  
  public void updateSnapshot() {
    snapSystemTime = system.getTime();
    snapCraft.copyFrom(system.getCraft());
  }
  
  
  
  public long getSnapSystemTime() {
    return snapSystemTime;
  }
  
  public Tetra getSnapCraft() {
    return snapCraft;
  }
  
  
  public boolean failed() {
    return error != null;
  }
  
  public RuntimeException getException() {
    return error;
  }

  
  public int getPeriodMillis() {
    return periodMillis;
  }
  
  
  public int trialTime() {
    return (int) (system.getTime() - initSystemTime);
  }
  

  public boolean runToOrbitalPoint(NormPoint point) {
    if (failed())
      throw new IllegalStateException(
          "attempt to invoke with " + point + " on already-failed instance");
    try {
      
      // check the argument
      {
        int lastIndex = commandsReceived.size() - 1;
        
        if (lastIndex == -1) {
          if (point.x() == 0)
            throw new IllegalArgumentException(point.toString());
        } else {
          NormPoint lastCommand = commandsReceived.get(lastIndex);
          if (lastCommand.x() >= point.x())
            throw new IllegalArgumentException(
                "out of sequence: last " + lastCommand + "; next " + point);
        }
      }
      
      commandsReceived.add(point);
      
      int timeToTarget = (int) (point.x() * periodMillis) - trialTime();
      double edgeLength = point.y() * config.maxTetherLength;
      
      TetraShape targetShape = new TetraShape();
      targetShape.setLengths(edgeLength);
      controller.setTargetShape(targetShape, timeToTarget, controlStepsPerProfilePoint);
      system.animateControlledMillis(timeToTarget, config.timeFineness, controller, controlMillis);
      
      
    } catch (RuntimeException rx) {
      error = rx;
      return false;
    }
    return true;
  }
  
  
  
  public boolean runToCompleteOrbit() {
    if (failed())
      throw new IllegalStateException("attempt to invoke on already-failed instance");
    
    int remainingMillis = getPeriodMillis() - trialTime();
    if (remainingMillis < 1)
      return true;
    
    try {
      
      system.animateControlledMillis(remainingMillis, config.timeFineness, controller, controlMillis);
    
    } catch (RuntimeException rx) {
      error = rx;
      return false;
    }
    
    return true;
  }



  public double getInitEnergy() {
    return initEnergy;
  }




  public double getInitCmEnergy() {
    return initCmEnergy;
  }
  
  
  
  public double getEnergy() {
    return getEnergy(system.getCraft());
  }
  
  
  public double getEnergy(Tetra craft) {
    return craft.getEnergy(system.getPotential());
  }


  public double getCmEnergy() {
    return getCmEnergy(system.getCraft());
  }


  public double getCmEnergy(Tetra craft) {
    return craft.getCmEnergy(system.getPotential());
  }
  
  
  public double getRotationalEnergy() {
    return getRotationalEnergy(system.getCraft());
  }
  
  
  public double getRotationalEnergy(Tetra craft) {
    return getEnergy(craft) - getCmEnergy(craft);
  }
  
  
  public double getRotationalEnergyGain() {
    return getEnergyGain() - getCmEnergyGain();
  }
  
  
  public double getEnergyGain() {
    return getEnergy() - initEnergy;
  }
  
  
  public double getCmEnergyGain() {
    return getCmEnergy() - initCmEnergy;
  }
  
  
  public Simulation getSystem() {
    return system;
  }
  
  
  public double getRadiusGain() {
    return system.getCraft().newCmBob().distance(0, 0, 0) - initRadius;
  }
  
  public List<NormPoint> getCommandsReceived() {
    return commandsReceived;
  }


  


  /**
   * I can do better than this.. Should easily generalize to an elliptic orbit
   */
  private double estimateInitPeriod() {
    Bob cm = system.getCraft().newCmBob();
    double r = cm.distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / cm.getV();
    return period;
  }

}
