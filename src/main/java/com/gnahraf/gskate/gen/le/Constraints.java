package com.gnahraf.gskate.gen.le;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;




/**
 * Configuration. Everything in standard metric units unless otherwise
 * specified (e.g. kilometers instead of meters).
 */
public class Constraints implements Cloneable {
  
  public double initTetherLength = 100;
  public double steadyStateTetherLength = 250;
  public double maxTetherLength = 100000; // 100km
  public double minTetherLength = 50;
  
  public double minKmsAboveGround = 300;
  public double initKmsAboveGround = 1200;
  
  public double maxTensileForce = 500;
  public double maxCompressiveForce = 1;
  
  public double initTetherValue = 0;
  public double timeFineness = 6.7e-5;
  
  public Constraints clone() {
    try {
      return (Constraints) super.clone();
    } catch (CloneNotSupportedException wtf) {
      throw new RuntimeException(wtf);
    }
  }
  
  public boolean isValid() {
    return
        initTetherLength > minTetherLength &&
        initTetherLength < maxTetherLength &&

        steadyStateTetherLength > minTetherLength &&
        steadyStateTetherLength < maxTetherLength &&
        
        minTetherLength < maxTetherLength &&
        minTetherLength > 1 &&
        
        minKmsAboveGround > 50 &&
        initKmsAboveGround > minKmsAboveGround &&
        maxTensileForce > 0 &&
        maxCompressiveForce > 0 &&
        initTetherValue < maxCompressiveForce &&
        initTetherValue > -maxTensileForce &&
        timeFineness > 0 &&
        timeFineness < 0.1;
  }
  
  
  @Override
  public int hashCode() {
    int hash = Double.hashCode(initTetherValue);
    hash ^= Double.hashCode(-7 * steadyStateTetherLength);
    hash ^= Double.hashCode(maxTetherLength);
    hash ^= Double.hashCode(minTetherLength);
    hash ^= Double.hashCode(minKmsAboveGround);
    hash ^= Double.hashCode(-initKmsAboveGround);
    
    hash ^= Double.hashCode(-maxTensileForce);
    hash ^= Double.hashCode(maxCompressiveForce);
    hash ^= Double.hashCode(initTetherValue + .001);
    hash ^= Double.hashCode(timeFineness);
    return hash;
  }
  
  
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    else if (o instanceof Constraints) {
      Constraints other = (Constraints) o;
      return
          initTetherLength == other.initTetherLength &&
          steadyStateTetherLength == other.steadyStateTetherLength &&
          maxTetherLength == other.maxTetherLength &&
          minTetherLength == other.minTetherLength &&
          initKmsAboveGround == other.initKmsAboveGround &&
          maxTensileForce == other.maxTensileForce &&
          maxCompressiveForce == other.maxCompressiveForce &&
          initTetherValue == other.initTetherValue &&
          timeFineness == other.timeFineness;
    } else
      return false;
  }
  
  
  
  
  @Override
  public String toString() {
    return
        "Constraints[" +
        initTetherLength + "," +
        steadyStateTetherLength + "," +
        minTetherLength + "," +
        maxTetherLength + "," +
        minKmsAboveGround + "," +
        initKmsAboveGround + "," +
        maxTensileForce + "," +
        maxCompressiveForce + "," +
        initTetherValue + "," +
        timeFineness +
        "]";
  }
  
  
}