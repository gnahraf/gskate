/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.util;


/**
 * Base for a class of objects that are all equal.
 */
public abstract class EquiClass {

  
  @Override
  public final int hashCode() {
    return getClass().getName().hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return obj != null && getClass() == obj.getClass();
  }

  
  
  

}
