/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;



/**
 * A simulation engine. Some requirements:
 * <p/>
 * <ol>
 * <li>Ability to refine time resolution</li>
 * <li>Ability to set arbitrary checkpoints</li>
 * </ol>
 * <p/>
 * Checkpoints are points in time. Target uses include:
 * <ul>
 * <li>Gather statistics</li>
 * <li>Update control parameters</li>
 * <li>Update the simulation's environment, e.g. potential in
 *     multi body system.
 * </li>
 * <ul>
 * So a checkpoint is distinguishable from an {@linkplain Atom atom}
 * in that it is not really the meat of the simulation (although it
 * can definitely affect it).
 */
public class SimEngine {
  
  public final static long DEFAULT_TPS = 1000000000L;
  
  private final CheckpointStack checkpoints = new CheckpointStack();
  private final Atom model;
  
  private final long timeTps;
  private long tickTime;
  
  private long animationTps;
  private long animationTpsFactor;
  private double animationUnitTime;
  
  
  public SimEngine(Atom model) {
    this(model, DEFAULT_TPS);
  }
  
  public SimEngine(Atom model, long maxTicksPerSecond) {
    this.model = model;
    this.timeTps = maxTicksPerSecond;
    setAnimationTps(maxTicksPerSecond);
    
    if (model == null)
      throw new IllegalArgumentException("null model");
    if (maxTicksPerSecond <= 0)
      throw new IllegalArgumentException("maxTicksPerSecond" + maxTicksPerSecond);
  }



  
  
  public long animate(double seconds) {
    if (seconds < 0)
      throw new IllegalArgumentException("negative seconds " + seconds);

    final long animationTicks = (long) (seconds * animationTps);
    long animationTicksRemaining = animationTicks;
    
    
    /*
     
      | . . . |   .   |
     
     */
    while (animationTicksRemaining > 0) {
      long ticksToCheckpoint = checkpoints.getHeadTick() - tickTime;
      long animationTicksToCheckPoint =
          Math.min(ticksToCheckpoint / animationTpsFactor, animationTicksRemaining);
      
      if (animationTicksToCheckPoint == 0) {
        long finalTickTime = tickTime + animationTpsFactor;
        checkpoints.consumeUpToTick(finalTickTime);
        model.tick(animationUnitTime, 1);
        
        tickTime += animationTpsFactor;
        --animationTicksRemaining;
      } else {
        double time = animationTicksToCheckPoint / animationTps;
        model.tick(time, animationTicksToCheckPoint);
        
        tickTime += (animationTicksToCheckPoint * animationTpsFactor);
        animationTicksRemaining -= animationTicksToCheckPoint;
      }
    }
    
    return animationTicks;
  }
  
  
  
  
  
  
  public long getTickTime() {
    return tickTime;
  }



  public long getTimeTps() {
    return timeTps;
  }
  
  
  
  public double getTime() {
    return ((double) tickTime) / timeTps;
  }









  public long getAnimationTps() {
    return animationTps;
  }


  public void setAnimationTps(long ticksPerSecond) {
    if (timeTps % ticksPerSecond != 0)
      throw new IllegalArgumentException(
          timeTps + " % " + ticksPerSecond + " != 0");
    this.animationTps = ticksPerSecond;
    animationTpsFactor = timeTps / animationTps;
    animationUnitTime = 1.0d / animationTps;
  }






  public void schedule(Checkpoint checkpoint, double time, double period, int priority) {
    long tickTime = (long) (time * timeTps);
    long periodTicks = (long) (period * timeTps);
    schedule(checkpoint, tickTime, periodTicks, priority);
  }


  public void schedule(Checkpoint checkpoint, long tickTime, long periodTicks, int priority) {
    checkpoints.schedule(checkpoint, tickTime, periodTicks, priority);
  }
  
  
  public boolean remove(Checkpoint checkpoint) {
    return checkpoints.remove(checkpoint);
  }



  public long getScheduledTickTime(Checkpoint checkpoint) {
    return checkpoints.getScheduledTickTime(checkpoint);
  }

  public long getScheduledPeriodTicks(Checkpoint checkpoint) {
    return checkpoints.getScheduledPeriodTicks(checkpoint);
  }

  public int getScheduledPriority(Checkpoint checkpoint) {
    return checkpoints.getScheduledPriority(checkpoint);
  }

  public void setScheduledTickTime(Checkpoint checkpoint, long tickTime) {
    checkpoints.setScheduledTickTime(checkpoint, tickTime);
  }

  public void setScheduledPeriodTicks(Checkpoint checkpoint, long periodTicks) {
    checkpoints.setScheduledPeriodTicks(checkpoint, periodTicks);
  }

  public void setScheduledPriority(Checkpoint checkpoint, int priority) {
    checkpoints.setScheduledPriority(checkpoint, priority);
  }
  

}
