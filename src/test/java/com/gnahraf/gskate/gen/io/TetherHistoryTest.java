/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.io;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.gnahraf.gskate.gen.io.TetherHistory;

/**
 *
 */
public class TetherHistoryTest {

  @Test
  public void testEmpty() {
    TetherHistory history = new TetherHistory();
    assertEquals(0, history.count());
    assertIoobx(history, 0);
    assertIoobx(history, -1);
  }
  

  @Test
  public void testOne() {
    TetherHistory history = new TetherHistory();
    long time = 1;
    int id = 3;
    double value = 0.3;
    
    history.add(time, id, value);
    
    assertEquals(1, history.count());
    assertEquals(time, history.getTimestamp(0));
    assertEquals(id, history.getTetherId(0));
    assertEquals(value, history.getTetherValue(0), 0);

    assertIoobx(history, 1);
  }
  

  @Test
  public void testTwo() {
    long[] timestamps = { 0, 1 };
    int[] ids = { 3, 2 };
    double[] values = { -5.5, 73.002 };
    testAddGet(timestamps, ids, values);
  }
  

  @Test
  public void testTwoA() {
    long[] timestamps = { 0, 0 };
    int[] ids = { 3, 2 };
    double[] values = { -5.5, 73.002 };
    testAddGet(timestamps, ids, values);
  }
  
  

  

  @Test
  public void testOutOfSequence() {
    long[] timestamps = { 1, 0 };
    int[] ids = { 3, 2 };
    double[] values = { -5.5, 73.002 };
    try {
      testAddGet(timestamps, ids, values);
      fail();
    } catch (IllegalArgumentException expected) {  }
  }
  
  
  @Test
  public void testCapacityExpansion() {
    TetherHistory history = new TetherHistory(1);
    long[] timestamps = { 0, 1 };
    int[] ids = { 3, 2 };
    double[] values = { -5.5, 73.002 };
    testAddGet(history, timestamps, ids, values);
  }
  
  
  @Test
  public void testCapacityExpansionA() {
    TetherHistory history = new TetherHistory(1);
    long[] timestamps = { 0, 1, 1 };
    int[] ids = { 3, 2, 0 };
    double[] values = { -5.5, 73.002, 0.001 };
    testAddGet(history, timestamps, ids, values);
  }
  
  
  @Test
  public void testCapacityExpansionB() {
    TetherHistory history = new TetherHistory(0);
    long[] timestamps = { 0, 1 };
    int[] ids = { 3, 2 };
    double[] values = { -5.5, 73.002 };
    testAddGet(history, timestamps, ids, values);
  }
  
  
  @Test
  public void testReadWriteEmpty() throws IOException {
    testReadWrite(new TetherHistory());
  }
  
  
  @Test
  public void testReadWrite() throws IOException {
    TetherHistory history = new TetherHistory();
    long[] timestamps = { 0, 1, 1 };
    int[] ids = { 3, 2, 0 };
    double[] values = { -5.5, 73.002, 0.001 };
    testAddGet(history, timestamps, ids, values);
    testReadWrite(history);
  }
  
  
  

  
  
  private void testReadWrite(TetherHistory history) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    history.write(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    TetherHistory copy = TetherHistory.read(in);
    
    assertEquals(history.count(), copy.count());
    
    for (int index = 0; index < history.count(); ++index) {
      assertEquals(history.getTimestamp(index), copy.getTimestamp(index));
      assertEquals(history.getTetherId(index), copy.getTetherId(index));
      assertEquals(history.getTetherValue(index), copy.getTetherValue(index), 0);
    }
  }
  
  

  private void testAddGet(long[] timestamps, int[] ids, double[] values) {
    TetherHistory history = new TetherHistory();
    testAddGet(history, timestamps, ids, values);
  }
  
  
  private void testAddGet(TetherHistory history, long[] timestamps, int[] ids, double[] values) {
    
    for (int i = 0; i < ids.length; ++i)
      history.add(timestamps[i], ids[i], values[i]);
    

    assertEquals(ids.length, history.count());
    for (int i = 0; i < ids.length; ++i) {
      assertEquals(timestamps[i], history.getTimestamp(i));
      assertEquals(ids[i], history.getTetherId(i));
      assertEquals(values[i], history.getTetherValue(i), 0);
    }
  }
  
  
  private void assertIoobx(TetherHistory history, int index) {
    try {
      history.getTimestamp(index);
      fail();
    } catch (IndexOutOfBoundsException expected) {  }
    try {
      history.getTetherId(index);
      fail();
    } catch (IndexOutOfBoundsException expected) {  }
    try {
      history.getTetherValue(index);
      fail();
    } catch (IndexOutOfBoundsException expected) {  }
  }

}
