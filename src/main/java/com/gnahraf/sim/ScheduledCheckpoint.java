/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

/**
 *
 */
public final class ScheduledCheckpoint {
  
  private final Checkpoint checkpoint;
  private final long tick;
  private final long periodTicks;
  private final int priority;

  /**
   * 
   */
  ScheduledCheckpoint(Checkpoint checkpoint, long tick, long periodTicks, int priority) {
    this.checkpoint = checkpoint;
    this.tick = tick;
    this.periodTicks = periodTicks;
    this.priority = priority;
  }
  
  
  public Checkpoint checkpoint() {
    return checkpoint;
  }
  
  public long tick() {
    return tick;
  }
  
  
  public long periodTicks() {
    return periodTicks;
  }
  
  
  public int priority() {
    return priority;
  }
  
  
  public boolean isPeriodic() {
    return periodTicks > 0;
  }

}
