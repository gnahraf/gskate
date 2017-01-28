/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.main;

import java.util.Arrays;

import com.gnahraf.xcept.NotFoundException;

/**
 *
 */
public class Args {
  
  private final static char EQ = '=';

  private Args() {  }
  
  

  public static String getValue(String[] args, String name) {
    return getValue(args, name, null);
  }
  
  
  public static String getValue(String[] args, String name, String defaultValue) {
    String searchString = name.toString() + EQ;
    for (int i = 0; i < args.length; ++i) {
      if (args[i].startsWith(searchString))
        return args[i].substring(searchString.length());
    }
    if (defaultValue == null)
      throw new NotFoundException(searchString + ".. in args " + Arrays.asList(args));
    
    return defaultValue;
  }

}
