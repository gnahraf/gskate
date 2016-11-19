/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;

import java.util.Random;

import com.gnahraf.gskate.control.ShapeFuzzyController;
import com.gnahraf.gskate.control.TetherLengthHistory;
import com.gnahraf.gskate.gen.TetherHistory;

/**
 * A single orbit trial about Earth. Initial conditions
 * are for a circular orbit--which we need to change to an
 * elliptic one.
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
 * velocity, then we'll need to transition to a parabolic/hyperbolic 
 * orbit.
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
 * is bounded.
 */
public class SimpleEquiTrial {

  private final LonelyEarth.Constraints config;
  
  private final LonelyEarth system;
  
  private final TetherHistory history;
  
  private ShapeFuzzyController controller;
  
  
  private int navigationCommandsPerOrbit = 60;
  
  private int controlMillis = 40;
  
  private double initEnergy;
  private double initCmEnergy;
  

  /**
   * 
   */
  public SimpleEquiTrial(LonelyEarth.Constraints constraints) {
    this.config = constraints.clone();
    if (!config.isValid())
      throw new IllegalArgumentException(config.toString());
    this.system = new LonelyEarth(config);
    this.history = new TetherHistory();
    this.controller = new ShapeFuzzyController(system);
    controller.setShapeListener(new TetherLengthHistory(system, history));
    controller.setMinTetherLength(config.minTetherLength);
    controller.setMaxCompressiveForce(config.maxCompressiveForce);
    controller.setMaxTensileForce(config.maxTensileForce);
  }
  
  
  public void runRandom(Random rand) {
    double constantNavigationSeconds = estimateInitPeriod() / navigationCommandsPerOrbit;
    
  }
  
  private double estimateInitPeriod() {
    double r = system.getCraft().getBob(0).distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / system.getCraft().getBob(0).getV();
    return period;
  }
  

}
