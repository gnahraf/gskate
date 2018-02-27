/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * Physical constants.
 */
public class Constants {
  
  private Constants() { }
  
  /**
   * Newton's gravitational constant.
   */
  public final static double G = 6.67408e-11;
  
  public final static double EARTH_MASS = 5.972e24;
  
  public final static double G_EARTH = G * EARTH_MASS;
  
  public final static double EARTH_RADIUS = 6.371e6;

  public static final double MIN_SURFACE_DISTANCE = 100 * 1000;

}
