/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim.attic;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.sim.attic.Atom2;
import com.gnahraf.sim.attic.AtomScheduler2;
import com.gnahraf.sim.attic.TickTime;

/**
 *
 */
public class AtomSchedulerTest {

  
  @Test
  public void testTrivial() {
    
    final int[] tickCount = { 0 };
    final int[] invocationCount = { 0 };
    Atom2 testAtom = new Atom2() {
      @Override
      public void tick(TickTime time) {
        tickCount[0] += time.ticks; // .. surprised this compiles (!)
        ++invocationCount[0];
      }
    };
    
    final int ticksPerSecond = 12;
    AtomScheduler2 scheduler = new AtomScheduler2(testAtom, ticksPerSecond, 0);
    
    final TickTime timeResolution = scheduler.getTimeResolution();
    assertEquals(1, timeResolution.ticks);
    assertEquals(ticksPerSecond, timeResolution.ticksPerSecond);
    
    assertEquals(0, scheduler.getTicks());
    
    final int count = 5;
    TickTime run = timeResolution.asTickTime(count);
    scheduler.tick(run);
    
    assertEquals(count, scheduler.getTicks());
    assertEquals(count, tickCount[0]);
    assertEquals(1, invocationCount[0]);
    
    final int factor = 3;
    TickTime chunky = new TickTime(7, ticksPerSecond / factor);
    scheduler.tick(chunky);
    
    assertEquals(count + chunky.ticks * factor, scheduler.getTicks());
    assertEquals(scheduler.getTicks(), tickCount[0]);
    assertEquals(2, invocationCount[0]);
  }
  
  
  
  @Test
  public void testTwo() {
    final TestAtom baseAtom = new TestAtom();
    final int ticksPerSecond = 1000;
    AtomScheduler2 scheduler = new AtomScheduler2(baseAtom, ticksPerSecond);
    {
      final TickTime timeResolution = scheduler.getTimeResolution();
      assertEquals(1, timeResolution.ticks);
      assertEquals(ticksPerSecond, timeResolution.ticksPerSecond);
      
      assertEquals(0, scheduler.getTicks());
    }
    
    TickTime t40ms = new TickTime(4, 100);
    final TestAtom t2 = new TestAtom();
    
    scheduler.schedule(t2, t40ms, 3, 0);
    
    TickTime t1s = new TickTime(1, 1);
    scheduler.tick(t1s);
  }
  
  
  

}


class TestAtom extends Atom2 {
  private long tickCount;
  int invocationCount;
  
  @Override
  public void tick(TickTime time) {
    tickCount += time.ticks;
    ++invocationCount;
  }
  
  
}
