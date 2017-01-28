/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.print;

/**
 *
 */
public class Semantic {

  private Semantic() {  }
  
  
  
  public static String plural(String object, long count) {
    return count == 1 ? object : object + "s";
  }

}
