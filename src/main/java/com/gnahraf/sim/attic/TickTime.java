/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim.attic;

import java.math.BigInteger;

import com.gnahraf.math.IntMath;

/**
 *
 */
public final class TickTime implements Comparable<TickTime> {
  
  
  public final static TickTime MAX = new TickTime(Long.MAX_VALUE, 1);
  
  
  
  public final long ticks;
  public final long ticksPerSecond;
  
  
  public TickTime(long ticks, long ticksPerSecond) {
    this.ticks = ticks;
    this.ticksPerSecond = ticksPerSecond;
    
    if (ticks <= 0)
      throw new IllegalArgumentException("ticks " + ticks);
    if (ticksPerSecond <= 0)
      throw new IllegalArgumentException("ticksPerSecond " + ticksPerSecond);
  }

  
  
  
  public TickTime(long ticks, TickTime base) {
    this.ticks = ticks;
    this.ticksPerSecond = base.ticksPerSecond;

    if (ticks <= 0)
      throw new IllegalArgumentException("ticks " + ticks);
  }
  
  
  
  private TickTime(long ticks, long ticksPerSecond, boolean nocheck) {
    this.ticks = ticks;
    this.ticksPerSecond = ticksPerSecond;
  }
  
  
  
  
  public TickTime asTickTime(long ticks) {
    return this.ticks == ticks ? this : new TickTime(ticks, this);
  }
  
  
  public TickTime asMultipleOf(TickTime fineTime) {
    if (ticksPerSecond == fineTime.ticksPerSecond)
      return this;
    
    long ratio = fineTime.ticksPerSecond / ticksPerSecond;
      
    if (ratio * ticksPerSecond != fineTime.ticksPerSecond)
      throw new IllegalArgumentException(this + " not a TPS mutliple of " + fineTime);
    
    return new TickTime(ticks * ratio, fineTime.ticksPerSecond, false);
  }
  
  
  
  

  @Override
  public int hashCode() {
    
    long gcd = IntMath.gcd(ticks, ticksPerSecond);
    return Long.hashCode((ticks / gcd) ^ (ticksPerSecond * 127 / gcd));
  }


  
  



  public double getSecondsPerTick() {
    return 1.0d / ticksPerSecond;
  }
  
  
  public double asSeconds() {
    return ((double) ticks) / ticksPerSecond;
  }




  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof TickTime && compareTo((TickTime) obj) == 0;
  }





  public boolean equals(TickTime other) {
    return other != null && compareTo(other) == 0;
  }




  @Override
  public String toString() {
    return "[" + ticks + "/" + ticksPerSecond + "]";
  }









  @Override
  public int compareTo(TickTime other) {
    if (other.ticksPerSecond == ticksPerSecond)
      return Long.compare(ticks, other.ticks);
    if (Long.MAX_VALUE / ticksPerSecond > other.ticks &&
        Long.MAX_VALUE / other.ticksPerSecond > ticks) {
      return Long.compare(ticks * other.ticksPerSecond, other.ticks * ticksPerSecond);
    }
    BigInteger thisTime = BigInteger.valueOf(ticks).multiply(BigInteger.valueOf(other.ticksPerSecond));
    BigInteger otherTime = BigInteger.valueOf(other.ticks).multiply(BigInteger.valueOf(ticksPerSecond));
    return thisTime.compareTo(otherTime);
  }

}
