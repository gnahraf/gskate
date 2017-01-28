package com.gnahraf.gskate;

import java.util.ArrayList;

public class BigPlanet {
  
  public final static double G = 6.67408e-11;
  
  public final static double EARTH_MASS = 5.972e24;
  
  public final static double EARTH_RADIUS = 6.371e6;
  
  private final double g;

  
  private final Blobule a = new Blobule();
  private final Blobule b = new Blobule();
  
  private final Object animationLock = new Object();
  
  
  /**
   * +/- means repulsive / attractive
   */
  private volatile double tetheredAccel;
  
  private ArrayList<AnimationListener> listeners = new ArrayList<>();
  
  private final double baseEnergy;
  
  
  public BigPlanet(double kmAboveGround) {
    if (kmAboveGround < 100)
      throw new IllegalArgumentException("kmAboveGround " + kmAboveGround);
    this.g = G * EARTH_MASS;
    double r = EARTH_RADIUS + kmAboveGround*1000;
    double deltaX = 50;
    a.setPosition(r - deltaX,  0);
    b.setPosition(r + deltaX, 0);
    setInCircularOrbit(a);
    setInCircularOrbit(b);
    baseEnergy = getEnergyImpl();
  }
  
  
  public double getEnergy() {
    return getEnergyImpl() - baseEnergy;
  }
  
  public double getKineticEnergy() {
    return (a.getV2() + b.getV2()) / 2;
  }
  
  private double getEnergyImpl() {
    double pe = -g * (1/a.getR() + 1/b.getR());
    double ke = getKineticEnergy();
    return pe + ke;
  }
  
  
  public void addAnimationListener(AnimationListener listener) {
    this.listeners.add(listener);
  }

  
  
  private void setInCircularOrbit(Blobule sat) {
    double r = sat.getR();
    // counter clockwise
    double unitTx = -sat.getY() / r;
    double unitTy = sat.getX() / r;
    
    // calculate orbital speed
    //    double gravitationalAccel = g / sat.getR2();
    //    double v2 = gravitationalAccel * r;
    //    double v = Math.sqrt(v2);
    double v = Math.sqrt(g / r);
    sat.setVelocity(unitTx * v, unitTy * v);
  }
  
  
  
  public synchronized void launchAnimator() {
    if (animatorThread != null)
      throw new IllegalStateException("animatorThread " + animatorThread);
    Runnable loop = new Runnable() {
      public void run() { animationLoop(); }
    };
    animatorThread = new Thread(loop);
    animatorThread.setDaemon(true);
    animatorThread.start();
  }
  
  private Thread animatorThread;
  
  
  private void animationLoop() {
    while (true) {
      animateDeltaT(0.0000067);
    }
  }
  
  
  public final Object getAnimationLock() {
    return animationLock;
  }
  
  
  
  private void animateDeltaT(double seconds) {
    
    // calculate tethered acceleration..
    // direction of this vector is a->b; must be interpreted for each blobule a/b
    double tethAccX, tethAccY;
    {
      double abX = b.getX() - a.getX();
      double abY = b.getY() - a.getY();
      double abAbs = Math.sqrt(abX*abX + abY*abY);
      abX /= abAbs;
      abY /= abAbs;
      
      double mag = tetheredAccel;
      tethAccX = abX * mag;
      tethAccY = abY * mag;
    }
    
    // calculate G force for each blobule..
    
    // 1) Blobule a
    double aGx, aGy;
    {
      Blobule sat = a;
      
      double r2 = sat.getR2();
      double r = Math.sqrt(r2);
      
      double gX = sat.getX() / r;
      double gY = sat.getY() / r;
      
      double gAccel = -g / r2;
      
      gX *= gAccel;
      gY *= gAccel;
      
      aGx = gX;
      aGy = gY;
    }
    
    // 2) Blobule b
    double bGx, bGy;
    {
      Blobule sat = b;

      double r2 = sat.getR2();
      double r = Math.sqrt(r2);
      
      double gX = sat.getX() / r;
      double gY = sat.getY() / r;
      
      double gAccel = -g / r2;
      
      gX *= gAccel;
      gY *= gAccel;
      
      bGx = gX;
      bGy = gY;
    }
    
    // Net the forces..

    // 1) Blobule a
    double aNetX, aNetY;
    {
      aNetX = aGx - tethAccX;
      aNetY = aGy - tethAccY;
    }

    // 2) Blobule b
    double bNetX, bNetY;
    {
      bNetX = bGx + tethAccX;
      bNetY = bGy + tethAccY;
    }
    
    synchronized (animationLock) {
      a.updateImpulse(aNetX, aNetY, seconds);
      b.updateImpulse(bNetX, bNetY, seconds);
    }
    
    fireBlobulesMoved(a, b);
  }
  
  
  
  
  
  private void fireBlobulesMoved(Blobule a, Blobule b) {
    int count = listeners.size();
    for (int i = 0; i < count; ++i)
      listeners.get(i).blobulesMoved(a.getX(), a.getY(), b.getX(), b.getY());
  }



  public Blobule getA() {
    return a;
  }

  public Blobule getB() {
    return b;
  }




  public interface AnimationListener {
    void blobulesMoved(double ax, double ay, double bx, double by);
  }




  public double getTetheredAccel() {
    return tetheredAccel;
  }



  public void setTetheredAccel(double tetheredAccel) {
    this.tetheredAccel = tetheredAccel;
  }
  

}
