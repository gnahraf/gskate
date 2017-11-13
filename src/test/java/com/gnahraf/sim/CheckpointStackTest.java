/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 *
 */
public class CheckpointStackTest {
  
  public static class MockCheckpoint extends Checkpoint {
    
    public final int id;
    public int invocations;
    
    
    public MockCheckpoint(int id) {
      this.id = id;
    }

    @Override
    public void check() {
      ++invocations;
    }

    @Override
    public boolean equals(Object o) {
      return o == this || o instanceof MockCheckpoint && ((MockCheckpoint) o).id == id;
    }

    @Override
    public int hashCode() {
      return id;
    }
    
  }

  @Test
  public void testEmpty() {
    CheckpointStack stack = new CheckpointStack();
    assertEmpty(stack);
  }

  @Test
  public void testEmpty2() {
    CheckpointStack stack = new CheckpointStack();
    
    try {
      stack.schedule(null, 1, 0, 0);
      fail();
    } catch (IllegalArgumentException expected) {   }

    assertEmpty(stack);
  }

  @Test
  public void testEmpty3() {
    CheckpointStack stack = new CheckpointStack();
    
    try {
      stack.schedule(Checkpoint.NULL, 1, 0, 0);
      fail();
    } catch (IllegalArgumentException expected) {   }

    assertEmpty(stack);
  }

  @Test
  public void testEmpty4() {
    CheckpointStack stack = new CheckpointStack();
    
    try {
      stack.schedule(new MockCheckpoint(0), -1, 0, 0);
      fail();
    } catch (IllegalStateException expected) {   }

    assertEmpty(stack);
  }
  
  
  @Test
  public void testSingle() {
    CheckpointStack stack = new CheckpointStack();
    int id = 1;
    int tick = 2;
    int period = 0;
    int priority = 4;
    MockCheckpoint checkpoint = mockCp(id);
    stack.schedule(checkpoint, tick, period, priority);
    assertFalse(stack.isEmpty());
    assertEquals(tick, stack.getHeadTick());
    
    List<ScheduledCheckpoint> schedules = stack.getScheduledCheckpoints();
    assertEquals(1, schedules.size());
    assertFalse(schedules.get(0).isPeriodic());
    
    assertScheduled(id, tick, period, priority, stack);
    
    assertEquals(0, stack.consumeUpToTick(tick));
    assertFalse(stack.isEmpty());
    assertEquals(0, checkpoint.invocations);
    
    assertEquals(1, stack.consumeHeadTick());
    assertEmpty(stack);
  }
  
  
  @Test
  public void testSinglePeriodic() {
    CheckpointStack stack = new CheckpointStack();
    int id = 1;
    int tick = 2;
    int period = 3;
    int priority = 4;
    MockCheckpoint checkpoint = mockCp(id);
    stack.schedule(checkpoint, tick, period, priority);
    assertFalse(stack.isEmpty());
    assertEquals(tick, stack.getHeadTick());
    
    List<ScheduledCheckpoint> schedules = stack.getScheduledCheckpoints();
    assertEquals(1, schedules.size());
    assertTrue(schedules.get(0).isPeriodic());
    
    assertScheduled(id, tick, period, priority, stack);
    
    for (int count = 1; count <= 3; ++count) {
      assertEquals(1, stack.consumeHeadTick());
      assertFalse(stack.isEmpty());
      assertEquals(count, checkpoint.invocations);
      
      assertEquals(1, stack.getScheduledCheckpoints().size());
      
      assertScheduled(id, tick += period, period, priority, stack);
    }
  }
  
  
  
  @Test
  public void testPriorityCollision() {
    CheckpointStack stack = new CheckpointStack();
    int id = 1;
    int tick = 2;
    int period = 3;
    int priority = 4;
    MockCheckpoint checkpoint = mockCp(id);
    stack.schedule(checkpoint, tick, period, priority);
    

    MockCheckpoint checkpoint2 = mockCp(id + 1);
    
    try {
      stack.schedule(checkpoint2, tick + 1, period, priority);
      fail();
    } catch (IllegalStateException expected) {   }
  }
  
  
  @Test
  public void testTwo() {
    CheckpointStack stack = new CheckpointStack();
    int id = 1;
    int tick = 2;
    int period = 0;
    int priority = 4;
    MockCheckpoint checkpoint = mockCp(id);
    stack.schedule(checkpoint, tick, period, priority);
    
    
    int id2 = 2;
    int tick2 = 3;
    int period2 = 0;
    int priority2 = 5;
    MockCheckpoint checkpoint2 = mockCp(id2);
    stack.schedule(checkpoint2, tick2, period2, priority2);
    assertScheduled(id2, tick2, period2, priority2, stack);

    assertFalse(stack.isEmpty());
    assertEquals(tick, stack.getHeadTick());
    
    assertEquals(2, stack.getScheduledCheckpoints().size());
    
    
    assertEquals(1, stack.consumeHeadTick());
    
    assertScheduled(id2, tick2, period2, priority2, stack);

    assertFalse(stack.isEmpty());
    assertEquals(tick2, stack.getHeadTick());
    
    assertEquals(1, stack.getScheduledCheckpoints().size());
    
    assertEquals(1, stack.consumeHeadTick());
    assertEmpty(stack);
    
  }
  
  
  @Test
  public void testTwoPeriodic() {
    CheckpointStack stack = new CheckpointStack();
    int id = 1;
    int tick = 2;
    int period = 2;
    int priority = 5;
    MockCheckpoint checkpoint = mockCp(id);
    stack.schedule(checkpoint, tick, period, priority);
    
    
    int id2 = 2;
    int tick2 = 2;
    int period2 = 3;
    int priority2 = 4;
    MockCheckpoint checkpoint2 = mockCp(id2);
    stack.schedule(checkpoint2, tick2, period2, priority2);

    {
      List<ScheduledCheckpoint> schedules = stack.getScheduledCheckpoints();
      assertEquals(2, schedules.size());
      assertEquals(checkpoint, schedules.get(0).checkpoint());
      assertEquals(checkpoint2, schedules.get(1).checkpoint());
    }
    assertEquals(tick, stack.getHeadTick());
    
    // consume
    assertEquals(2, stack.consumeHeadTick());

    assertEquals(2, stack.getScheduledCheckpoints().size());
    assertScheduled(id, tick += period, period, priority, stack);
    assertScheduled(id2, tick2 += period2, period2, priority2, stack);
    assertEquals(tick, stack.getHeadTick());
    
    {
      List<ScheduledCheckpoint> schedules = stack.getScheduledCheckpoints();
      assertEquals(2, schedules.size());
      assertEquals(checkpoint, schedules.get(0).checkpoint());
      assertEquals(checkpoint2, schedules.get(1).checkpoint());
    }
    assertEquals(1, checkpoint.invocations);
    assertEquals(1, checkpoint2.invocations);

    // consume
    assertEquals(1, stack.consumeHeadTick());
    
    {
      List<ScheduledCheckpoint> schedules = stack.getScheduledCheckpoints();
      assertEquals(2, schedules.size());
      assertEquals(checkpoint2, schedules.get(0).checkpoint());
      assertEquals(checkpoint, schedules.get(1).checkpoint());
    }
    assertEquals(2, checkpoint.invocations);
    assertEquals(1, checkpoint2.invocations);
  }
  
  
  
  
  
  
  
  
  
  
  private MockCheckpoint mockCp(int id) {
    return new MockCheckpoint(id);
  }
  
  
  private void assertEmpty(CheckpointStack stack) {
    assertTrue(stack.isEmpty());
    assertTrue(stack.getScheduledCheckpoints().isEmpty());
    assertEquals(0, stack.consumeHeadTick());
  }
  
  private void assertScheduled(int id, int tick, int period, int priority, CheckpointStack stack) {
    Checkpoint cp = mockCp(id);
    ScheduledCheckpoint scp = stack.findScheduled(cp);
    assertEquals(tick, scp.tick());
    assertEquals(period, scp.periodTicks());
    assertEquals(priority, scp.priority());
    assertEquals(cp, scp.checkpoint());
  }

}
