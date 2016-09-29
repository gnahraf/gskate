/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import java.util.Random;

import com.gnahraf.gskate.gen.ConstraintViolationException;
import com.gnahraf.gskate.gen.MaxTetherLengthMeter;
import com.gnahraf.gskate.gen.RandomTetherControl;
import com.gnahraf.gskate.gen.TetherHistory;
import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.Tetra;

/**
 *
 */
public class Trial {
  
  
  private final LonelyEarth.Constraints constraints;
  private final double minR2;
  private final double maxTetherLenSq;
  
  private final TetherHistory history;
  private final LonelyEarth system;
  
  
  private final Tetra initialState;
  
  private final Tetra snapshot = new Tetra();
  

  private Random rand;
  private RandomTetherControl randomTetherControl;
  private MaxTetherLengthMeter tetherLengthMeter;
  
  
  private int maxConsecutiveViolations = 1000;
  
  
  
  
  public Trial(Tetra initialState, TetherHistory history, LonelyEarth.Constraints constraints) {
    this.constraints = constraints == null ? new LonelyEarth.Constraints() : constraints;
    {
      double minR = this.constraints.minKmsAboveGround * 1000 + Constants.EARTH_RADIUS;
      this.minR2 = minR * minR;
      
      double maxT = this.constraints.maxTetherLength;
      this.maxTetherLenSq = maxT * maxT;
      
    }
    this.history = history == null ? new TetherHistory() : history;
    this.system = new LonelyEarth(this.constraints);
    
    if (!this.history.isEmpty())
      system.setTime(this.history.getLastTimestamp());
    
    if (initialState == null) {
      if (!this.history.isEmpty())
        throw new IllegalArgumentException("initialState must be specified with history");
      this.initialState = new Tetra();
      this.initialState.copyFrom(system.getCraft());
    } else {
      this.initialState = initialState;
      system.getCraft().copyFrom(this.initialState);
    }
    
    this.rand = new Random();
    this.randomTetherControl =
        new RandomTetherControl(
            this.constraints.maxTensileForce, this.constraints.maxCompressiveForce);
    
    this.tetherLengthMeter = new MaxTetherLengthMeter(system.getCraft());
  }
  
  
  
  public void setRandomSeed(long seed) {
    rand.setSeed(seed);
  }
  
  
  public void simulateRandomDecisions(long seconds, int decisionPoints) throws ConstraintViolationException {
    if (decisionPoints < 0)
      throw new IllegalArgumentException("decisionPoints " + decisionPoints);
    if (seconds < 1)
      throw new IllegalArgumentException("seconds " + seconds);
    snapshot.copyFrom(system.getCraft());
    long snapshotTime = system.getTime();
    int snapshotHistoryCount = history.count();
    int violationCountDown = maxConsecutiveViolations + 1;
    while (violationCountDown-- > 0) {
      if (!simulateRandomDecisionsImpl(seconds, decisionPoints)) {
        system.setTime(snapshotTime);
        system.getCraft().copyFrom(snapshot);
        history.trimCount(snapshotHistoryCount);
      }
    }
    if (violationCountDown == -1)
      throw new ConstraintViolationException();
  }
  
  
  
  
  
  
  public double getCmEnergy() {
    return system.getCraft().getCmEnergy(system.getPotential());
  }
  
  
  public LonelyEarth getSystem() {
    return system;
  }
  
  
  
  public int getMaxConsecutiveViolations() {
    return maxConsecutiveViolations;
  }


  public void setMaxConsecutiveViolations(int maxConsecutiveViolations) {
    if (maxConsecutiveViolations < 0)
      throw new IllegalArgumentException("maxConsecutiveViolations " + maxConsecutiveViolations);
    this.maxConsecutiveViolations = maxConsecutiveViolations;
  }


  private boolean simulateRandomDecisionsImpl(long seconds, int decisionPoints) {
    long millis = seconds * 1000;
    long millisPerDecision = millis / (decisionPoints + 1);
    
    for (int adjustments = decisionPoints; adjustments-- < 0; ) {
      adjustTethers(system, history);
      system.animateMillis(millisPerDecision);
      if (constraintsViolated(system))
        return false;
    }
    adjustTethers(system, history);
    system.animateMillis(millis - millisPerDecision * decisionPoints);
    return !constraintsViolated(system);
  }
  
  
  
  




  protected void adjustTethers(LonelyEarth system, TetherHistory history) {
    Tetra craft = system.getCraft();
    for (int index = 0; index < 6; ++index) {
      double tetherValue = randomTetherControl.newTetherValue(craft.getTetherByIndex(index), rand);
      craft.setTetherByIndex(index, tetherValue);
      history.add(system.getTime(), index, tetherValue);
    }
  }
  
  
  protected boolean constraintsViolated(LonelyEarth system) {
    Tetra craft = system.getCraft();
    for (int index = 0; index < 4; ++index) {
      if (craft.getBob(index).distanceSq(0, 0, 0) < minR2)
        return true;
    }
    
    tetherLengthMeter.measure();
    return tetherLengthMeter.getMaxTetherLenSq() > maxTetherLenSq;
  }

}
