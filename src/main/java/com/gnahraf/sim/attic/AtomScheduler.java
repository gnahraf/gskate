/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim.attic;

/**
 *
 */
public class AtomScheduler extends Atom {
  
  
  /**
   * A sorted linked list of scheduled atoms.
   */
  private Schedule head;
  

  private long ticks;
  
  
  
  public AtomScheduler(Atom tickAtom) {
    this(tickAtom, 0);
  }
  
  public AtomScheduler(Atom tickAtom, int priority) {
    // make the sentinel element
    head = new Schedule(Atom.NULL, Integer.MAX_VALUE, Integer.MIN_VALUE);
    head.scheduledTick = Long.MAX_VALUE;
    
    schedule(tickAtom, 1, priority);
  }
  
  
  public void schedule(Atom atom, int period, int priority) {
    schedule(atom, period, priority, period - 1);
  }
  
  public void schedule(Atom atom, int period, int priority, int phase) {
    if (phase < 0)
      throw new IllegalArgumentException("phase " + phase);
    
    Schedule s = new Schedule(atom, period, priority);
    s.scheduledTick = ticks + phase;
    
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
  
  
  @Override
  // FIXME
  public void tick(long count) {
    if (count < 0)
      throw new IllegalArgumentException("count " + count);
    
//    while (count > 0) {
//      long headTicks = Math.min(count, head.next.scheduledTick - head.scheduledTick) + 1;
//    }
    
    final long finalTicks = ticks + count;
    
    while (ticks < finalTicks) {
      
      // invariant: head.scheduledTick == ticks
      
      long freeTicks = head.next.scheduledTick - ticks;
      
      if (freeTicks == 0) {
        
        do {
//          head.atom.tick(); FIXME
          Schedule s = head;
          head = head.next;
          s.scheduledTick += s.period;
          pushSchedule(s);
          
        } while (head.scheduledTick == ticks);
        
        ++ticks;
      
      } else {
        
        freeTicks = Math.min(freeTicks, finalTicks - ticks);
        
        head.atom.tick(freeTicks);
        Schedule s = head;
        head = head.next;

        ticks += freeTicks;
        s.scheduledTick = ticks;
        pushSchedule(s);

      }
      
      
    } // while
  }


//  @Override
  public void tick() {
    tick(1);
  }
  
  
  private static class Schedule {

    final Atom atom;
    final int period;
    final int priority;
    
    long scheduledTick;
    Schedule next;
    
    Schedule(Atom atom, int period, int priority) {
      this.atom = atom;
      this.period = period;
      this.priority = priority;
      
      if (atom == null)
        throw new IllegalArgumentException("null atom");
      if (period < 1)
        throw new IllegalArgumentException("period " + period);
    }
    
    
    public int compareTo(Schedule other) {
      int comp = Long.compare(scheduledTick, other.scheduledTick);
      return comp == 0 ? -Integer.compare(priority, other.priority) : comp;
    }
  }

}
