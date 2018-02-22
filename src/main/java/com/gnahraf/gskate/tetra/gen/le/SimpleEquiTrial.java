/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.le;


import java.util.ArrayList;
import java.util.List;

import com.gnahraf.gskate.model.DynaVector;
import com.gnahraf.gskate.tetra.control.ShapeFuzzyController;
import com.gnahraf.gskate.tetra.control.ShapeMetaController;
import com.gnahraf.gskate.tetra.model.Simulation;
import com.gnahraf.gskate.tetra.model.TetraShape;
import com.gnahraf.util.data.DoubleDouble;

/**
 * A single orbit trial about Earth. Initial conditions
 * are for a circular orbit--which we need to change to an
 * elliptic one.
 * 
 * <p/>
 * Why? The basic maneuver involves pulling the bobs in
 * towards each other in order to perform work against the
 * gravitational well. The greater the rate of change of the
 * slope of the well, the greater the tidal forces, and hence
 * the greater the opportunity to do work. All else being equal,
 * the magnitude of this tidal force is inversely proportional
 * to the cube of R, the distance to the center of Earth. At the
 * perigee, where R is minimized, then, the tidal force is
 * greatest. Moreover, if we hope to incrementally achieve escape
 * velocity, then we'll need to transition from an elliptic orbit
 * to a parabolic/hyperbolic trajectory.
 * 
 * <h4>Tether Length /Time Profie</h4>
 * <p/>
 * Here we experiment with [targeting] different tether lengths over
 * the course of a single orbit. Below, an example of what such a plot
 * over time might look like.
 * <p/>
 * <pre>
 * {@literal
 * 
 * tether length
 *  |       
 *  |
 *  |      * * *
 *  |    *       *
 *  |   *          *
 *  |* *              *  * *
 *  |_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ time
 * 
 * }
 * </pre>
 * <p/>
 * Note the maximum tether length will be kept fixed across an ensemble of trials.
 *
 * <h4>Length Profile</h4>
 * <p/>
 * We want to specify the length profile in scale-independent terms. Specifically,
 * the profile is characterized by
 * <p/>
 * <ol>
 * <li>time, the horizontal axis, is scaled to the interval [0, 1] with
 *     1 unit per orbit. This, of course, drifts against real time as the craft
 *     gains orbital energy and the orbital period increases, and</li>
 * <li>length, the vertical axis, is also scaled to the interval [0, 1] with 1
 *     being the maximum length.</li>
 * </ol>
 * 
 * <h4>Cost Function</h4>
 * <p/>
 * Things we want.
 * <ul>
 * <li>Maximize potential energy</li>
 * <li>Maximize translational energy</li>
 * <li>Minimize rotational energy</li>
 * </ul>
 * 
 * <p/>
 * The reason why we need to cap the rotational energy is that our tether strength
 * (and length) is bounded.
 */
public class SimpleEquiTrial {
  
  
  public static class Profile {
    
    private final List<DoubleDouble> points = new ArrayList<DoubleDouble>();
    
    
    public List<DoubleDouble> getPoints() {
      return points;
    }
    
    
    public void addNextPoint(double time, double size) {
      addNextPoint(new DoubleDouble(time, size));
    }
    
    public void addNextPoint(DoubleDouble point) {
      if (point.y() <= 0 || point.y() > 1 || point.x() <= 0 || point.x() > 1)
        throw new IllegalArgumentException(point.toString());
      if (!points.isEmpty() && points.get(points.size() - 1).x() >= point.x())
        throw new IllegalArgumentException(
            "out of sequence: last " + points.get(points.size() - 1) + "; next " + point);
      
      points.add(point);
    }
    
    
  }

  private final Constraints config;
  
  private final Profile profile;
  
  private final LonelyEarth system;
  
  private final ShapeFuzzyController fuzzyControl;
  
  private final ShapeMetaController controller;
  
  private final int periodMillis;
  
  private final double initEnergy;
  private final double initCmEnergy;
  private final double initRadius;
  
  private int controlMillis = 40;
  
  private int controlStepsPerProfilePoint = 1000;
  
  private RuntimeException error;
  

  /**
   * 
   */
  public SimpleEquiTrial(Constraints constraints, Profile profile) {
    this.config = constraints.clone();
    if (!config.isValid())
      throw new IllegalArgumentException(config.toString());
    this.profile = profile;
    if (profile.getPoints().size() < 3)
      throw new IllegalArgumentException("profile size " + profile.getPoints().size());
    this.system = new LonelyEarth(config);
    this.fuzzyControl = new ShapeFuzzyController(system);
    fuzzyControl.setMinTetherLength(config.minTetherLength);
    fuzzyControl.setMaxCompressiveForce(config.maxCompressiveForce);
    fuzzyControl.setMaxTensileForce(config.maxTensileForce);
    fuzzyControl.freeze();
    controller = new ShapeMetaController(fuzzyControl);
    periodMillis = (int) (1000 * estimateInitPeriod());
    initEnergy = getEnergy();
    initCmEnergy = getCmEnergy();
    initRadius = system.getCraft().newCmBob().distance(0, 0, 0);
  }
  
  
  public boolean failed() {
    return error != null;
  }
  
  public RuntimeException getException() {
    return error;
  }
  
  public void runOneOrbit() {

    TetraShape targetShape = new TetraShape();
    int trialTime = 0;
    
    try {
      for (DoubleDouble point : profile.getPoints()) {
        
        int time = (int) (point.x() * periodMillis) - trialTime;
        double edgeLength = point.y() * config.maxTetherLength;
        
        targetShape.setLengths(edgeLength);
        controller.setTargetShape(targetShape, time, controlStepsPerProfilePoint);
        system.animateControlledMillis(time, config.timeFineness, controller, controlMillis);
        trialTime += time;
      }
      
      int millisRemaining = periodMillis - trialTime;
      assert millisRemaining >= 0;
      system.animateControlledMillis(millisRemaining, config.timeFineness, controller, controlMillis);
    } catch (RuntimeException rx) {
      error = rx;
    }
  }
  
  
  
  
  
  
  
  
  
  
  public Profile getProfile() {
    return profile;
  }




  public double getInitEnergy() {
    return initEnergy;
  }




  public double getInitCmEnergy() {
    return initCmEnergy;
  }
  
  
  
  public double getEnergy() {
    return system.getCraft().getEnergy(system.getPotential());
  }


  public double getCmEnergy() {
    return system.getCraft().getCmEnergy(system.getPotential());
  }
  
  
  public double getRotationalEnergy() {
    return getEnergy() - getCmEnergy();
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


  private double estimateInitPeriod() {
    DynaVector cm = system.getCraft().newCmBob();
    double r = cm.distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / cm.getV();
    return period;
  }
  

}
