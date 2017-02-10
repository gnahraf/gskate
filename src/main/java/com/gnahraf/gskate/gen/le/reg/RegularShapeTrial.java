/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.reg;


import java.util.ArrayList;
import java.util.List;

import com.gnahraf.gskate.control.ShapeFuzzyController;
import com.gnahraf.gskate.control.ShapeMetaController;
import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.gskate.model.Simulation;
import com.gnahraf.gskate.model.SphericalBodyPotential;
import com.gnahraf.gskate.model.TetraShape;
import com.gnahraf.util.data.NormPoint;

/**
 * Simulation of a single orbit of a {@linkplain Tetra} craft about Earth,
 * parameterized by a command-set describing the edge length of a regular
 * tetrahedron.
 */
public class RegularShapeTrial {
  
  private final List<NormPoint> commandsReceived = new ArrayList<NormPoint>();

  private final Constraints config;
  
  private final Simulation system;
  
  private final ShapeMetaController controller;

  private final CraftState initState;
  
  private final int periodMillis;
  
  
  
  private int controlMillis = 40;
  
  private int controlStepsPerProfilePoint = 1000;
  
  private RuntimeException error;
  
  private CraftState snapshot;

  /**
   * 
   */
  public RegularShapeTrial(Constraints constraints) {
    this(constraints, null);
  }
  
  
  public RegularShapeTrial(Constraints constraints, CraftState state) {
    this.config = constraints.clone();
    if (!config.isValid())
      throw new IllegalArgumentException(config.toString());
    
    this.system =
        state == null ?
            new LonelyEarth(config) :
              new Simulation(new SphericalBodyPotential(), state);
    
    this.controller = createController(system);
    this.initState = newSnapshot();

    periodMillis = estimateInitPeriodMillis();
  }
  
  
  
  private ShapeMetaController createController(Simulation system) {
    ShapeFuzzyController fuzzyControl = new ShapeFuzzyController(system);
    fuzzyControl.setMinTetherLength(config.minTetherLength);
    fuzzyControl.setMaxCompressiveForce(config.maxCompressiveForce);
    fuzzyControl.setMaxTensileForce(config.maxTensileForce);
    fuzzyControl.freeze();
    return new ShapeMetaController(fuzzyControl);
  }
  
  
  
  public RegularShapeTrial(RegularShapeTrial copy) {
    this.commandsReceived.addAll(copy.commandsReceived);
    this.config = copy.config.clone();
    this.system = copy.system.clone();
    this.controller = copy.controller.newSnapshot(this.system);
    this.initState = copy.initState;
    
    this.periodMillis = copy.periodMillis;
    
    this.controlMillis = copy.controlMillis;
    this.controlStepsPerProfilePoint = copy.controlStepsPerProfilePoint;

    this.snapshot = copy.snapshot;
  }
  
  
  
  public CraftState getInitState() {
    return initState;
  }
  
  
  /**
   * Updates the snapshot with a new one encapsulating the current state and returns it.
   * 
   * @see #getSnapshot()
   */
  public CraftState newSnapshot() {
    return snapshot = new CraftState(system.getTime(), system.getCraft());
  }
  
  
  /**
   * Returns the last snapshot taken with {@linkplain #newSnapshot()}.
   */
  public CraftState getSnapshot() {
    return snapshot;
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
  
  
  public int getTrialTime() {
    return (int) (system.getTime() - initState.getTime());
  }
  

  public boolean runToOrbitalPoint(NormPoint point) {
    if (failed())
      throw new IllegalStateException(
          "attempt to invoke with " + point + " on already-failed instance");

    final int timeToTarget = (int) (point.x() * periodMillis) - getTrialTime();
    
    try {
      
      NormPoint lastCommand;
      {
        final int count = commandsReceived.size();
        final int lastIndex = count - 1;
        
        if (count == 0) {
          if (point.x() == 0)
            throw new IllegalArgumentException(point.toString());
          
          lastCommand = null;
        } else {
          
          lastCommand = commandsReceived.get(lastIndex);
          
          if (lastCommand.x() >= point.x()) {
            
            // allow this corner case (that I sometimes hit)
            if (lastCommand.equals(point))
              return true;
            
            throw new IllegalArgumentException(
                "out of sequence: last " + lastCommand + "; next " + point);
          }
          
          
          // if we've been flat-lining just extend the last command
          if (lastCommand.y() == point.y() && count > 1 && commandsReceived.get(count- 2).y() == point.y()) {
            commandsReceived.remove(lastIndex);
          }
        }
      }

      
      commandsReceived.add(point);
      
      
      
      if (lastCommand != null && lastCommand.y() == point.y())
        return runSteady(timeToTarget);
      
      
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
  
  
  
  public boolean runSteady(int millis) {
    if (millis < 1) {
      
      if (millis == 0)
        return true;
      
      throw new IllegalArgumentException("millis " + millis);
    }
    
    try {
      system.animateControlledMillis(millis, config.timeFineness, controller, controlMillis);
    } catch (RuntimeException rx) {
      error = rx;
      return false;
    }
    
    return true;
  }
  
  
  
  public boolean runToCompleteOrbit() {
    if (failed())
      throw new IllegalStateException("attempt to invoke on already-failed instance");
    
    int remainingMillis = getPeriodMillis() - getTrialTime();
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
    return initState.getEnergy(system.getPotential());
  }




  public double getInitCmEnergy() {
    return initState.getCmEnergy(system.getPotential());
  }
  
  
  
  public double getEnergy() {
    return system.getEnergy();
  }


  public double getCmEnergy() {
    return system.getCmEnergy();
  }
  
  
  public double getRotationalEnergy() {
    return getEnergy() - getCmEnergy();
  }
  
  
  public double getRotationalEnergyGain() {
    return getEnergyGain() - getCmEnergyGain();
  }
  
  
  public double getEnergyGain() {
    return getEnergy() - getInitEnergy();
  }
  
  
  public double getCmEnergyGain() {
    return getCmEnergy() - getInitCmEnergy();
  }
  
  
  public Simulation getSystem() {
    return system;
  }
  
  
  public double getRadiusGain() {
    return
        system.getCraft().newCmBob().distance(0, 0, 0) -
        initState.getCmBob().distance(0, 0, 0);
  }
  
  public List<NormPoint> getCommandsReceived() {
    return commandsReceived;
  }


  private int estimateInitPeriodMillis() {
    return (int) (1000 * estimateInitPeriod());
  }


  /**
   * I can do better than this.. <strike>Should easily generalize to an elliptic orbit.</strike>
   * Umm.. this is actually a difficult problem for the general case. For now this will do.
   * <p/>
   * Another point is that we're just calling this the period. We could just see it as a
   * deterministic scaling factor for our normalized point commands, though.
   * We'll be keeping track of the periapsis and apoapsis vectors soon anywqy.
   */
  private double estimateInitPeriod() {
    Bob cm = system.getCraft().newCmBob();
    double r = cm.distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / cm.getV();
    return period;
  }


  /**
   * Returns a copy of the constraints. (The one used by this
   * instance is not mutable.)
   */
  public Constraints getConstraints() {
    return config.clone();
  }

}
