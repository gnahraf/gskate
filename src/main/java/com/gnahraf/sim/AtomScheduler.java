/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;



/**
 *
 */
public class AtomScheduler extends Atom {
  
  private final TickTime timeResolution;
  private long ticks;
  
  /**
   * A sorted linked list of scheduled atoms.
   */
  private Schedule head;
  

  

  /**
   * 
   */
  public AtomScheduler(Atom tickAtom, long ticksPerSecond) {
    this(tickAtom, ticksPerSecond, 0);
  }
  

  /**
   * 
   */
  public AtomScheduler(Atom tickAtom, long ticksPerSecond, int priority) {
    timeResolution = new TickTime(1, ticksPerSecond);
    
    head = new Schedule(Atom.NULL, Long.MAX_VALUE, Integer.MIN_VALUE);
    head.scheduledTick = Long.MAX_VALUE;
    
    schedule(tickAtom, timeResolution, priority, 0);
  }

  
  @Override
  public void tick(TickTime time) {
    time = time.asMultipleOf(timeResolution);
    
    final long finalTicks = ticks + time.ticks;
    
    if (finalTicks < 0)
      throw new IllegalStateException(ticks + " + " + time.ticks + " = " + finalTicks);
    
    while (ticks < finalTicks) {
      
      // invariant: head.scheduledTick == ticks
      
      long freeTicks = head.next.scheduledTick - ticks;
      
      if (freeTicks == 0) {
        
        do {
          head.atom.tick(timeResolution);
          advanceHead();
          
        }  while (head.scheduledTick == ticks);

        ++ticks;
        continue;
      }
      
      {
        
        
        {
          long remainingTicks = finalTicks - ticks;
          if (freeTicks > remainingTicks)
            freeTicks = remainingTicks;
        }
        
        head.atom.tick(timeResolution.asTickTime(freeTicks));
        advanceHead();
        ticks += freeTicks;
      }
    }
  }
  
  
  
  public final long getTicks() {
    return ticks;
  }
  
  
  public void schedule(Atom atom, TickTime period, int priority, long phase) {
    
    if (phase > 0)
      phase = period.asTickTime(phase).asMultipleOf(timeResolution).ticks;
    
    else if (phase < 0)
      throw new IllegalArgumentException("phase " + phase);
    
    period = period.asMultipleOf(timeResolution);
    
    
    Schedule s = new Schedule(atom, period.ticks, priority);
    s.scheduledTick = ticks + phase;
    
    pushSchedule(s);
  }
  
  
  
  private void advanceHead() {
    Schedule s = head;
    head = head.next;
    s.scheduledTick += s.period;
    pushSchedule(s);
  }
  
  
  private void pushSchedule(Schedule s) {
    Schedule cursor = head;
    Schedule prev = null;
    while (cursor.compareTo(s) < 0) {
      prev = cursor;
      cursor = cursor.next;
    }
    s.next = cursor;
    if (prev == null)
      head = s;
    else
      prev.next = s;
  }
  
  
  
  private static class Schedule {
    
    final Atom atom;
    final long period;
    final int priority;
    
    long scheduledTick;
    Schedule next;
    
    Schedule(Atom atom, long period, int priority) {
      this.atom = atom;
      this.period = period;
      this.priority = priority;
      
      if (atom == null)
        throw new IllegalArgumentException("null atom");
    }
    
    
    int compareTo(Schedule other) {
      int comp = Long.compare(scheduledTick, other.scheduledTick);
      return comp == 0 ? -Integer.compare(priority, other.priority) : comp;
    }
  }



  public TickTime getTimeResolution() {
    return timeResolution;
  }

}
