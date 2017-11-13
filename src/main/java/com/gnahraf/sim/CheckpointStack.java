/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.sim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.gnahraf.xcept.NotFoundException;

/**
 *
 */
public class CheckpointStack {
  
  
  
  private static class Schedule {
    
    private final Checkpoint checkpoint;
    private long tick;
    private long periodTicks;
    private int priority;
    /**
     * Singly linked.
     */
    private Schedule next;
    
    Schedule(Checkpoint checkpoint) {
      this.checkpoint = checkpoint;
    }
    
    boolean isPeriodic() {
      return periodTicks > 0;
    }
    
    boolean advancePeriod() {
      if (isPeriodic()) {
        tick += periodTicks;
        if (tick < 0)
          throw new IllegalStateException(tick - periodTicks + " + " + periodTicks + " = " + tick);
        return true;
      }
      return false;
    }
    
    int compareTo(Schedule other) {
      int timeComp = Long.compare(tick, other.tick);
      return timeComp == 0 ? Integer.compare(other.priority, priority) : timeComp;
    }
    
    ScheduledCheckpoint toSc() {
      return new ScheduledCheckpoint(checkpoint, tick, periodTicks, priority);
    }
  }
  
  
  
  private final HashSet<Integer> prioritySet = new HashSet<>();
  private long lastTickSeen = -1;
  
  
  private Schedule head;
  
  
  public CheckpointStack() {
    head = new Schedule(Checkpoint.NULL);
    head.tick = Long.MAX_VALUE;
  }
  
  
  
  

  
  
  public boolean isEmpty() {
    return head.next == null;
  }
  
  
  public long getHeadTick() {
    return head.tick;
  }
  
  
  public int consumeUpToTick(long tick) {
    int count = 0;
    while (getHeadTick() < tick)
      count += consumeHeadTick();
    return count;
  }
  
  
  public int consumeHeadTick() {
    if (isEmpty())
      return 0;
    
    final long headTick = head.tick;
    if (headTick == Long.MAX_VALUE)
      throw new IllegalStateException("overflow");
    
    int count = 0;
    
    do {
      Schedule s = head;
      head = head.next;
      
      s.checkpoint.check();
      ++count;
      
      if (s.advancePeriod()) {
        attach(s);
      } else {
        clearPriority(s);
      }
      
    } while (headTick == head.tick);
    
    lastTickSeen = headTick;
    
    return count;
  }
  
  
  
  
  
  
  
  
  public long getScheduledTickTime(Checkpoint checkpoint) {
    return getSchedule(checkpoint).tick;
  }
  
  public long getScheduledPeriodTicks(Checkpoint checkpoint) {
    return getSchedule(checkpoint).periodTicks;
  }
  
  public int getScheduledPriority(Checkpoint checkpoint) {
    return getSchedule(checkpoint).priority;
  }
  
  
  
  public void setScheduledTickTime(Checkpoint checkpoint, long tickTime) {
    checkTimeArg(tickTime);
    Schedule s = getSchedule(checkpoint);
    detach(s);
    s.tick = tickTime;
    attach(s);
  }
  
  public void setScheduledPeriodTicks(Checkpoint checkpoint, long periodTicks) {
    getSchedule(checkpoint).periodTicks = periodTicks;
  }
  
  
  public void setScheduledPriority(Checkpoint checkpoint, int priority) {
    Schedule s = getSchedule(checkpoint);
    if (s.priority != priority) {
      if (!prioritySet.add(priority))
        throw new IllegalArgumentException("priority collision " + priority);
      
      clearPriority(s);
      
      detach(s);
      s.priority = priority;
      attach(s);
    }
  }
  
  
  private void checkArg(Checkpoint checkpoint) {
    if (checkpoint == null || checkpoint == Checkpoint.NULL)
      throw new IllegalArgumentException("checkpoint " + checkpoint);
  }
  
  private void checkTimeArg(long tickTime) {
    if (tickTime <= lastTickSeen)
      throw new IllegalStateException(tickTime + " <= " + lastTickSeen);
    if (tickTime == Long.MAX_VALUE)
      throw new IllegalArgumentException("Don't be silly");
  }
  
  
  public void schedule(Checkpoint checkpoint, long tickTime, long periodTicks, int priority) {
    checkArg(checkpoint);
    checkTimeArg(tickTime);
    
    Schedule existing = findSchedule(checkpoint);
    if (existing != null) {
      boolean detach;
      if (priority != existing.priority) {
        if (!prioritySet.add(priority))
          throw new IllegalStateException("priority collision on update " + priority);
        detach = true;
        clearPriority(existing);
      } else
        detach = tickTime != existing.tick;
      
      if (detach) {
        detach(existing);
        existing.priority = priority;
        existing.tick = tickTime;
        existing.periodTicks = periodTicks;
        
        attach(existing);
      } else
        existing.periodTicks = periodTicks;
      
      return;
    
    } else if (!prioritySet.add(priority))
      throw new IllegalStateException("priority collision " + priority);
    
    else {
      Schedule s = new Schedule(checkpoint);
      s.priority = priority;
      s.tick = tickTime;
      s.periodTicks = periodTicks;
      attach(s);
    }
  }
  
  
  
  public List<ScheduledCheckpoint> getScheduledCheckpoints() {
    ArrayList<ScheduledCheckpoint> schedules = new ArrayList<>();
    for (Schedule s = head; s.next != null; s = s.next)
      schedules.add(s.toSc());
    return schedules;
  }
  
  
  public ScheduledCheckpoint findScheduled(Checkpoint checkpoint) {
    Schedule s = findSchedule(checkpoint);
    return s == null ? null : s.toSc();
  }
  
  
  
  
  
  
  
  
  private void clearPriority(Schedule existing) {
    boolean removed = prioritySet.remove(existing.priority);
    if (!removed)
      throw new RuntimeException("Sanity check fail on " + existing);
  }
  
  
  private void attach(Schedule s) {
    int comp = s.compareTo(head);
    if (comp < 0) {
      s.next = head;
      head = s;
      return;
    }
    
    if (comp == 0)
      throw new RuntimeException("Sanity check fail 1");
    
    Schedule prev = head;
    while ((comp = prev.next.compareTo(s)) < 0)
      prev = prev.next;
    

    if (comp == 0)
      throw new RuntimeException("Sanity check fail 2");
    
    s.next = prev.next;
    prev.next = s;
  }






  private void detach(Schedule existing) {
    
    if (existing.next == null)
      throw new RuntimeException("Sanity check 1 fail");
    
    if (existing == head) {
      head = head.next;
      
      if (head == null)
        throw new RuntimeException("Sanity check 2 fail");
      
      return;
    }
    
    Schedule prev = head;
    
    // no checks needed; blows up if broken
    while (prev.next != existing)
      prev = prev.next;
    
    prev.next = existing.next;
  }




  private Schedule getSchedule(Checkpoint checkpoint) {
    Schedule s = findSchedule(checkpoint);
    if (s == null)
      throw new NotFoundException();
    return s;
  }

  private Schedule findSchedule(Checkpoint checkpoint) {
    for (Schedule s = head; s.next != null; s = s.next) {
      if (s.checkpoint.equals(checkpoint))
        return s;
    }
    return null;
  }







  public boolean remove(Checkpoint checkpoint) {
    boolean removed = false;
    // we could do this in one pass,
    // but not worth the increase in code/bug surface..
    Schedule s = findSchedule(checkpoint);
    if (s != null) {
      detach(s);
      clearPriority(s);
      removed = true;
    }
    return removed;
  }
  

}
