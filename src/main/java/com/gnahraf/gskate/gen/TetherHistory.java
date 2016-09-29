/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 */
public class TetherHistory {
  
  private final static int INIT_CAPACITY = 16;
  private final static int BYTES_PER_ROW = 8 + 1 + 8;
  private final static int IO_BUFFER_SIZE = 512 * BYTES_PER_ROW;
  private final static int MAX_ROWS = 1024 * 1024;
  
  
  /**
   * Event timestamps in millis.
   */
  private long[] timestamps;
  /**
   * The tether IDs. There are 6 of these.
   */
  private byte[] tetherIds;
  private double[] tetherValues;
  
  private int count;
  private long lastTimestamp;
  
  
  public TetherHistory() {
    this(INIT_CAPACITY);
  }
  
  
  public TetherHistory(int initCapacity) {
    timestamps = new long[initCapacity];
    tetherIds = new byte[initCapacity];
    tetherValues = new double[initCapacity];
  }
  
  
  
  /**
   * Adds a new record.
   * 
   * @param timestamp     time in millis
   * @param tetherId      the index of the tether (0 thru 5)
   * @param tetherValue   force in Newtons (+/- denotes repulsive/attractive , or push/pull)
   */
  public void add(long timestamp, int tetherId, double tetherValue) {
    if (tetherId < 0 || tetherId > 5)
      throw new IllegalArgumentException("tetherId " + tetherId);
    if (timestamp < lastTimestamp)
      throw new IllegalArgumentException("timestamp " + timestamp + "; last timestamp " + lastTimestamp);
    ensureCapacity();
    timestamps[count] = timestamp;
    tetherIds[count] = (byte) tetherId;
    tetherValues[count] = tetherValue;
    ++count;
    lastTimestamp = timestamp;
  }
  
  
  public int count() {
    return count;
  }


  public boolean isEmpty() {
    return count == 0;
  }
  
  
  
  public long getTimestamp(int index) {
    validateIndex(index);
    return timestamps[index];
  }
  
  
  public int getTetherId(int index) {
    validateIndex(index);
    return tetherIds[index];
  }
  
  
  public double getTetherValue(int index) {
    validateIndex(index);
    return tetherValues[index];
  }
  
  
  
  public void trimCount(int newCount) {
    if (newCount > count || newCount < 0)
      throw new IllegalArgumentException("newCount " + newCount + "; count " + count);
    this.count = newCount;
    lastTimestamp = this.count == 0 ? 0 : timestamps[count - 1];
  }
  
  
  
  public void write(OutputStream out) throws IOException {
    byte[] mem = new byte[IO_BUFFER_SIZE];
    ByteBuffer buff = ByteBuffer.wrap(mem);
    
    buff.putInt(count);
    out.write(mem, 0, 4);
    buff.clear();
    
    for (int row = 0; row < count; ) {
      if (!buff.hasRemaining()) {
        out.write(mem);
        buff.clear();
        continue;
      }
      
      buff.putLong(timestamps[row]);
      buff.put(tetherIds[row]);
      buff.putDouble(tetherValues[row]);
      ++row;
    }
    
    out.write(mem, 0, buff.position());
  }
  
  
  
  public static TetherHistory read(InputStream in) throws IOException {
    byte[] mem = new byte[IO_BUFFER_SIZE];
    ByteBuffer buff = ByteBuffer.wrap(mem);
    
    {
      int bytes = in.read(mem, 0, 4);
      if (bytes != 4)
        throw new EOFException("at first read: " + bytes);
    }
    
    int count = buff.getInt();
    
    if (count > MAX_ROWS || count < 0)
      throw new IllegalArgumentException("read count " + count);
    
    TetherHistory history = new TetherHistory(count);
    
    buff.clear();
    
    
    for (int bytesRemaining = count * BYTES_PER_ROW; bytesRemaining > 0; ) {
      
      if (bytesRemaining < buff.limit())
        buff.limit(bytesRemaining);
      
      int bytesRead = in.read(mem, 0, buff.limit());
      
      if (bytesRead != buff.limit())
        throw new EOFException(
            "expected " + buff.limit() + " bytes; actual was " + bytesRead +
            ". " + bytesRemaining + " bytes remaining" );
      
      while (buff.hasRemaining()) {
        long timestamp = buff.getLong();
        int tetherId = buff.get();
        double tetherValue = buff.getDouble();
        history.add(timestamp, tetherId, tetherValue);
      }
      
      buff.clear();
      bytesRemaining -= bytesRead;
    }
    
    return history;
  }
  
  
  
  
  
  private void validateIndex(int index) {
    if (index < 0 || index >= count)
      throw new IndexOutOfBoundsException("index " + index);
  }
  
  private void ensureCapacity() {
    if (count == timestamps.length) {
      int cap = count == 0 ? INIT_CAPACITY : count * 2;
      long[] stamps = new long[cap];
      byte[] ids = new byte[cap];
      double[] values = new double[cap];
      for (int i = count; i-- > 0; ) {
        stamps[i] = timestamps[i];
        ids[i] = tetherIds[i];
        values[i] = tetherValues[i];
      }
      timestamps = stamps;
      tetherIds = ids;
      tetherValues = values;
    }
  }


  public long getLastTimestamp() {
    if (count == 0)
      throw new IllegalStateException("attempt to retrieve last record time on empty instance");
    return lastTimestamp;
  }

}
