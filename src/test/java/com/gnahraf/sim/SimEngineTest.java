/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.sim.CheckpointStackTest.MockCheckpoint;

/**
 *
 */
public class SimEngineTest {
  
  static class MockSystem extends Universe {
    
    double timeSum;
    long stepsSum;

    @Override
    public void tick(double time, long steps) {
      timeSum += time;
      stepsSum += steps;
    }
    
  }
  
  
  @Test
  public void testIllegalTpsInConstructor() {
    try {
      new SimEngine(new MockSystem(), 0);
      fail();
    } catch (IllegalArgumentException expected) {  }
  }
  

  @Test
  public void testIllegalAnimationTps() {
    MockSystem model = new MockSystem();
    SimEngine engine = new SimEngine(model);
    assertEquals(SimEngine.DEFAULT_TPS, engine.getTimeTps());
    assertEquals(SimEngine.DEFAULT_TPS, engine.getAnimationTps());
    try {
      engine.setAnimationTps(1300);
      fail();
    } catch (IllegalArgumentException expected) {   }
  }
  
  
  @Test
  public void testWithOneCheckpoint() {
    MockSystem model = new MockSystem();
    SimEngine engine = new SimEngine(model);
    
    long animationTps = SimEngine.DEFAULT_TPS / 10;
    
    engine.setAnimationTps(animationTps);
    
    MockCheckpoint cp1 = new MockCheckpoint(1);
    long startTick1 = 1000;
    long period1 = 1000;
    int priority1 = 0;
    
    engine.schedule(cp1, startTick1, period1, priority1);
    long steps = engine.animate(1);
    assertEquals(animationTps, steps);
    assertEquals(animationTps, model.stepsSum);
    assertEquals(1.0, model.timeSum, .0000001);
    
    
    long expectedCpInvocations = engine.getTickTime() / period1 - 1;
    assertEquals(expectedCpInvocations, cp1.invocations);
    
    long nextSteps = engine.animate(1.000001/animationTps);
    assertEquals(1, nextSteps);
    assertEquals(expectedCpInvocations + 1, cp1.invocations);
  }

}
