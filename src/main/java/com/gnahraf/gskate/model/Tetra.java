/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * A 4 kg craft consisting of 4 mutually tethered bobs. We assume the
 * tethers haven negligible mass.
 */
public class Tetra {
  
  private final Bob[] bobs = new Bob[4];
  
  /**
   * Lower diagonal of a 4x4 matrix (w/o diagonal elements) laid out linearly
   * (column by column)
   */
  private final double[] tethers = new double[6];
  
  
  
  
  public Tetra() {
    for (int i = 0; i < 4; ++i)
      bobs[i] = new Bob();
  }
  
  
  
  
  public Bob getBob(int index) throws IndexOutOfBoundsException {
    return bobs[index];
  }
  
  

  
  
  public double getKe() {
    double v2 = 0;
    for (int i = 0; i < 4; ++i)
      v2 += bobs[i].getV2();
    return v2 / 2;
  }
  
  
  /**
   * Returns the translational energy of the craft--excludes
   * rotational energy. 
   */
  public double getCmKe() {
    double vx, vy, vz = vy = vx = 0;
    for (int i = 0; i < 4; ++i) {
      Bob bob = bobs[i];
      vx += bob.getVx();
      vy += bob.getVy();
      vz += bob.getVz();
    }
    return (vx*vx + vy*vy + vz*vz) / 8;
//  vx /= 4;
//  vy /= 4;
//  vz /= 4;
//  
//  double v2 = (vx*vx + vy*vy + vz*vz);
//  return 2 * v2;  // 4 * v2 / 2;
  }
  
  
  
  /**
   * Returns the energy I aim to maximize. We don't care whether the
   * craft is spinning (as long as it's not too fast).
   */
  public double getCmEnergy(Potential potential) {
    return getPe(potential) + getCmKe();
  }

  
  
  
  public double getPe(Potential potential) {
    double joules = 0;
    for (int i = 0; i < 4; ++i)
      joules += potential.pe(bobs[i]);
    return joules;
  }

  
  
  
  public int getTetherIndex(int bobA, int bobB) {
    int i, j;
    {
      if (bobA < bobB) {
        i = bobA;
        j = bobB;
      } else {
        i = bobB;
        j = bobA;
      }
    }
    
    if (j - i > 3 || j == i)
      throw new IndexOutOfBoundsException(bobA + "," + bobB);
    
    switch (i) {
    case 0: return j - 1;
    case 1: return j + 1;
    case 2: return 5;
    default:
      throw new IndexOutOfBoundsException(bobA + "," + bobB);
    }
  }
  
  
  public double getTetherByIndex(int index) throws IndexOutOfBoundsException {
    return tethers[index];
  }
  
  
  
  public double getTether(int bobA, int bobB) throws IndexOutOfBoundsException {
    return tethers[ getTetherIndex(bobA, bobB) ];
  }
  
  
  /**
   * 
   * @param tether +/- means repulsive/attractive
   * @throws IndexOutOfBoundsException
   */
  public void setTether(int bobA, int bobB, double tether) throws IndexOutOfBoundsException {
    tethers[ getTetherIndex(bobA, bobB) ] = tether;
  }
  
  
  public void setTetherByIndex(int index, double tether) throws IndexOutOfBoundsException {
    tethers[index] = tether;
  }
  
  
  public void updateForces(Potential potential) {
    
    // clear accelerations..
    for (int i = 0; i < 4; ++i) {
      Bob bob = bobs[i];
      bob.clearAcceleration();
      potential.force(bob);
    }

    for (int tether = 0; tether < 6; ++tether)
      addTetherForces(tether);
  }
  
  
  
  public void animate(Potential potential, double seconds, double timeFineness) {
    if (seconds < 0 || timeFineness < 0)
      throw new IllegalArgumentException(seconds + ", " + timeFineness);
    int runs = (int) (seconds / timeFineness);
    for (int i = 0; i < runs; ++i) {
      updateForces(potential);
      animateDeltaT(timeFineness);
    }
    seconds -= (runs * timeFineness);
    if (seconds > 0) {
      updateForces(potential);
      animateDeltaT(seconds);
    }
  }
  
  
  private void animateDeltaT(double seconds) {
    for (int i = 0; i < 4; ++i)
      bobs[i].animate(seconds);
  }
  

  
  private void addTetherForces(int tetherIndex) {
    
    // find the bobs a and b at the ends of this tether
    Bob a, b;
    {
      int i, j;
      
      switch (tetherIndex) {
      case 0:
        i = 0; j = 1; break;
      case 1:
        i = 0; j = 2; break;
      case 2:
        i = 0; j = 3; break;
      case 3:
        i = 1; j = 2; break;
      case 4:
        i = 1; j = 3; break;
      case 5:
        i = 2; j = 3; break;
      default:
        throw new RuntimeException("tether " + tetherIndex);
      }
      
      a = bobs[i];
      b = bobs[j];
    }

    
    // calculate the unit vector from a -> b
    // (we'll use it to construct our acceleration vector)
    double abx, aby, abz;
    {
      double distance = a.distance(b);
      abx = (b.getX() - a.getX()) / distance;
      aby = (b.getY() - a.getY()) / distance;
      abz = (b.getZ() - a.getZ()) / distance;
    }
    
    double tetherValue = tethers[tetherIndex];
    boolean attractive;
    if (tetherValue < 0) {
      attractive = true;
      tetherValue = -tetherValue;
    } else
      attractive = false;
    
    // scale the unit vector to this tether value
    abx *= tetherValue;
    aby *= tetherValue;
    abz *= tetherValue;
    
    if (attractive) {
      a.addAcceleration(abx, aby, abz);
      b.addAcceleration(-abx, -aby, -abz);
    } else {
      // repulsive
      b.addAcceleration(abx, aby, abz);
      a.addAcceleration(-abx, -aby, -abz);
    }
  }
  
  

}
