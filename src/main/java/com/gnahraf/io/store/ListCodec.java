/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io.store;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gnahraf.io.CorruptionException;

/**
 *
 */
public class ListCodec<T> implements Codec<List<T>> {
  
  public final static int DEFAULT_MAX_LIST_SIZE = 128;
  
  
  
  private final Codec<T> itemCodec;
  private final int maxListSize;
  
  
  
  

  public ListCodec(Codec<T> itemCodec) {
    this(itemCodec, DEFAULT_MAX_LIST_SIZE);
  }
  
  public ListCodec(Codec<T> itemCodec, int maxListSize) {
    this.itemCodec = itemCodec;
    this.maxListSize = maxListSize;
    
    if (itemCodec == null)
      throw new IllegalArgumentException("null itemCodec");
    if (maxListSize < 2)
      throw new IllegalArgumentException("maxListSize " + maxListSize);
  }
  
  
  
  
  
  

  @Override
  public void write(List<T> list, ByteBuffer dtn) throws BufferOverflowException {
    if (list.size() > getMaxListSize())
      throw new IllegalArgumentException(
          "list size " + list.size() + " > maxListSize " + getMaxListSize());
    dtn.putInt(list.size());
    for (T item : list)
      itemCodec.write(item, dtn);
  }
  

  @Override
  public List<T> read(ByteBuffer src) throws BufferUnderflowException {
    int count = src.getInt();
    if (count < 2) {
      if (count == 1)
        return Collections.singletonList(itemCodec.read(src));
      else if (count == 0)
        return Collections.emptyList();
      
      throw new IllegalArgumentException("count read was " + count);
    }
    
    ArrayList<T> list = new ArrayList<T>(count);
    while (count-- > 0)
      list.add(itemCodec.read(src));
    
    return list;
  }
  

  @Override
  public int maxBytes() {
    return 4 + itemCodec.maxBytes() * getMaxListSize();
  }
  
  
  public int getMaxListSize() {
    return maxListSize;
  }

}
