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
  
  

  /**
   * Returns a bob representing the center of mass, the group speed and the group
   * acceleration.
   */
  public Bob newCmBob() {
    Bob bob = new Bob();
    for (int i = 0; i < 4; ++i)
      bob.add(bobs[i]);
    
    double i, j, k;
    
    i = bob.getX() / 4;
    j = bob.getY() / 4;
    k = bob.getZ() / 4;
    bob.setPosition(i, j, k);

    i = bob.getVx() / 4;
    j = bob.getVy() / 4;
    k = bob.getVz() / 4;
    bob.setVelocity(i, j, k);
    
    i = bob.getAx() / 4;
    j = bob.getAy() / 4;
    k = bob.getAz() / 4;
    bob.setAcceleration(i, j, k);
    
    return bob;
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
  
  
  

  public double getEnergy(Potential potential) {
    return getPe(potential) + getKe();
  }

  
  
  
  public int getTetherIndex(int bobA, int bobB) {
    return TetraEdge.forBobs(bobA, bobB).index;
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
  
  
  /**
   * Updates the acceleration vectors of the 4 bobs using the given <tt>potential</tt>,
   * taking into account the craft's tether forces.
   */
  public void updateForces(Potential potential) {
    
    // clear and set to gravitational forces
    for (int i = 0; i < 4; ++i) {
      Bob bob = bobs[i];
      bob.clearAcceleration();
      potential.force(bob);
    }

    // add the tether forces
    for (int tether = 0; tether < 6; ++tether)
      addTetherForces(tether);
  }
  
  
  
  public int getLoBob(Potential potential) {
    int index = 0;
    double pe = potential.pe(bobs[index]);
    for (int i = 1; i < 4; ++i) {
      double nextPe = potential.pe(bobs[i]);
      if (nextPe < pe) {
        pe = nextPe;
        index = i;
      }
    }
    return index;
  }
  
  
  
  public TetraShape getShape() {
    double[] lengths = new double[6];
    for (int index = 0; index < 6; ++index) {
      TetraEdge edge = TetraEdge.forIndex(index);
      lengths[index] = bobs[edge.loBob].distance( bobs[edge.hiBob] );
    }
    TetraShape shape = new TetraShape();
    shape.setLengths(lengths);
    return shape;
  }
  
  
  
  /**
   * Clears the acceleration vectors of the 4 bobs. Note, you can do as you please with
   * the acceleration vectors. They are really temporary vectors; they don't encapsulate
   * state.
   */
  public void clearForces() {
    for (int i = 0; i < 4; ++i)
      bobs[i].clearAcceleration();
  }
  
  
  
  public void animate(Potential potential, double seconds, double timeFineness) {
    if (seconds < 0 || timeFineness <= 0)
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
      
      TetraEdge edge = TetraEdge.forIndex(tetherIndex);
      
      a = bobs[edge.loBob];
      b = bobs[edge.hiBob];
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
    
    // add the equal and opposite forces
    if (attractive) {
      a.addAcceleration(abx, aby, abz);
      b.addAcceleration(-abx, -aby, -abz);
    } else {
      // repulsive
      b.addAcceleration(abx, aby, abz);
      a.addAcceleration(-abx, -aby, -abz);
    }
  }




  public void copyFrom(Tetra other) {
    for (int i = 0; i < 4; ++i)
      bobs[i].copyFrom(other.bobs[i]);
    for (int i = 0; i < 6; ++i)
      tethers[i] = other.tethers[i];
  }

}
