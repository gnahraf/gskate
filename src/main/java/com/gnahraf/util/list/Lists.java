/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.util.list;


import java.util.AbstractList;
import java.util.List;

/**
 *
 */
public class Lists {

  private Lists() {  }
  
  
  
  public static <T> List<T> readOnlyList(final T[] array) {
    return
        new AbstractList<T>() {
      
          @Override
          public T get(int index) {
            return array[index];
          }
          
          @Override
          public int size() {
            return array.length;
          }
        };
  }

}
