/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.le.io;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 *
 */
public class RegularShapeTransform {
  
  public final String config;
  public final String startState;
  public final String endState;
  public final String commandSet;
  
  
  private RegularShapeTransform(
      String config,
      String startState,
      String endState,
      String commandSet) {
    
    this.config = config;
    this.startState = startState;
    this.endState = endState;
    this.commandSet = commandSet;
  }
  
  
  
  
  
  
  @Override
  public final boolean equals(Object o) {
    return
        this == o ||
        o instanceof RegularShapeTransform && equals((RegularShapeTransform) o);
  }
  
  
  public final boolean equals(RegularShapeTransform other) {
    if (this == other)
      return true;
    if (other == null)
      return false;
    return
        equal(startState, other.startState) &&
        equal(endState, other.endState) &&
        equal(config, other.config) &&
        equal(commandSet, other.commandSet);
  }
  
  
  private boolean equal(String a, String b) {
    return a.equals(b);
  }
  
  
  
  
  @Override
  public int hashCode() {
    return
        hashCode(config) ^
        hashCode(startState) ^
        hashCode(endState) ^
        hashCode(commandSet);
  }
  
  
  private int hashCode(String field) {
    return field.hashCode();
  }
  
  
  
  static class Builder {
    
    final static int MAX_FIELD_LENGTH = 64;
    
    public String config;
    public String startState;
    public String endState;
    public String commandSet;
    
    public Builder() {  }
    
    public Builder(RegularShapeTransform t) {
      this.config = t.config;
      this.startState = t.startState;
      this.endState = t.endState;
      this.commandSet = t.commandSet;
    }
    
    public RegularShapeTransform build() {
      checkField(config, "config");
      checkField(startState, "startState");
      checkField(endState, "endState");
      checkField(commandSet, "commandSet");
      
      return new RegularShapeTransform(config, startState, endState, commandSet);
    }
    
    private void checkField(String value, String name) {
      if (value == null || value.isEmpty())
        throw new IllegalStateException(name + " field not set");
      if (value.length() > MAX_FIELD_LENGTH)
        throw new IllegalStateException(name + " field length too long: '" + value + "'");
    }
    

    @Override
    public final boolean equals(Object o) {
      return this == o || o instanceof Builder && equals((Builder) o);
    }
    
    
    public final boolean equals(Builder other) {
      if (this == other)
        return true;
      if (other == null)
        return false;
      
      return
          equal(startState, other.startState) &&
          equal(endState, other.endState) &&
          equal(config, other.config) &&
          equal(commandSet, other.commandSet);
    }
    
    
    private boolean equal(String a, String b) {
      return a == null ? b == null : a.equals(b);
    }
    
    
    
    
    @Override
    public int hashCode() {
      return
          hashCode(config) ^
          hashCode(startState) ^
          hashCode(endState) ^
          hashCode(commandSet);
    }
    
    
    private int hashCode(String field) {
      return field == null ? -1 : field.hashCode();
    }
    
    
    
    
  }
  
  
  
  static class Encoder implements com.gnahraf.io.store.Encoder<Builder> {
    
    private final static int MAX_BYTES = (Builder.MAX_FIELD_LENGTH * 2 + 1) * 4;

    @Override
    public void write(Builder item, ByteBuffer dtn) throws BufferOverflowException {
      writeField(item.config, dtn);
      writeField(item.startState, dtn);
      writeField(item.endState, dtn);
      writeField(item.commandSet, dtn);
    }

    @Override
    public int maxBytes() {
      return MAX_BYTES;
    }
    
    private void writeField(String field, ByteBuffer buffer) {
      for (int i = 0; i < field.length(); ++i)
        buffer.putChar(field.charAt(i));
      buffer.put((byte) 0xFF);
    }
    
  }

}
