/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling;

import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.SphericalBodyPotential;
import com.gnahraf.math.matrix.MatrixComposition;
import com.gnahraf.math.r3.Matrix3x3;
import com.gnahraf.math.r3.Vector;


/**
 * Utility for start configuration. Specifies the initial
 * orientation, position (center of mass), and velocity vectors of
 * the sling.
 * These are independent of other parameters, such as the initial
 * length of the sling, and the relative weights of the bobs. Keep
 * in mind, the motivation at this juncture is to roll out,
 * unfurl the sling from a small form factor in a circular orbit.
 * <p/>
 * In order to be relatable, we'll be orbiting Earth.
 * 
 * <h3>Coordinates</h3>
 * <p/>
 * By convention the launch orbit is in the x-y plane. To simplify
 * matters we'll further assume the orbit is counter clockwise. The
 * initial coordinates will be of the form <tt>(x, 0, 0)</tt>
 * where <tt>x</tt> is a positive number.
 * <p/>
 * Consequently, in order that the craft be in orbit, given its
 * location, the velocity vector will be close to <tt>(0, y, 0)</tt>
 * where again <tt>y</tt> is a positive number.
 * 
 * <h3>Orientation</h3>
 * <p/>
 * The default orientation is with bob B (the lighter bob) directly below
 * bob A in the direction of the planet. (Future note: Actually, I'll probably
 * refine this to be oriented perpendicular to the velocity vector and in
 * the plane that spans the sling's position, the planet's center, and whose
 * normal is perpendicular to the velocity vector.)
 * <p/>
 * So given the initial coordinates, the initial orientation (assuming an
 * initial circular orbit) works out to be along the x-axis.
 * 
 */
public class Launcher {
  
  private Matrix3x3 orientation;
  
  private double initSurfaceDistance = 300*1000;
  
  private double initLength = 1.0;
  
  private double a2bRatio = 2.0;
  

  /**
   * 
   */
  public Launcher() {
    // TODO Auto-generated constructor stub
  }


  
  /**
   * Tilts the sling upward relative to its trajectory. Order matters.
   * 
   * @param degrees +/- means up/down
   */
  public void pitch(double degrees) {
    if (degrees == 0)
      return;
    // we're heading in the y-direction (velocity vector)
    // so pitch here means rotating clockwise about the z-axis
    Matrix3x3 r = Matrix3x3.rotateAboutZ(-toRadians(degrees));
    updateOrientation(r);
  }
  
  
  public void yaw(double degrees) {
    if (degrees == 0)
      return;
    // we're heading in the y-direction (velocity vector)
    // so yaw here means rotating counter clockwise about the x-axis
    Matrix3x3 r = Matrix3x3.rotateAboutX(toRadians(degrees));
    updateOrientation(r);
  }
  
  
  /**
   * Rolls the sling relative to its trajectory. Order matters.
   * 
   * @param degrees  +/- means counter-clockwise/clockwise
   *                 
   */
  public void roll(double degrees) {
    if (degrees == 0)
      return;
    Matrix3x3 r = Matrix3x3.rotateAboutY(toRadians(degrees));
    updateOrientation(r);
  }
  
  
  
  private void updateOrientation(Matrix3x3 r) {
    if (orientation == null)
      orientation = r;
    else
      orientation = new Matrix3x3(new MatrixComposition(orientation, r));
  }
  
  
  /**
   * Also enforces against parameter duplication.
   */
  private double toRadians(double degrees) {
    if (Math.abs(degrees) > 180)
      throw new IllegalArgumentException("|" + degrees + "| > 180");
    return degrees * Math.PI / 180;
  }
  
  public double getInitLength() {
    return initLength;
  }


  public void setInitLength(double initLength) {
    if (initLength < 0.1)
      throw new IllegalArgumentException(initLength + " < 0.1");
    this.initLength = initLength;
  }


  public double getInitSurfaceDistance() {
    return initSurfaceDistance;
  }


  public void setInitSurfaceDistance(double initSurfaceDistance) {
    if (initSurfaceDistance < Constants.MIN_SURFACE_DISTANCE)
      throw new IllegalArgumentException(initSurfaceDistance + " < " + Constants.MIN_SURFACE_DISTANCE);
    this.initSurfaceDistance = initSurfaceDistance;
  }


  public double getA2bRatio() {
    return a2bRatio;
  }

  public void setA2bRatio(double a2bRatio) {
    if (a2bRatio < 1)
      throw new IllegalArgumentException(a2bRatio + " < 1");
    this.a2bRatio = a2bRatio;
  }
  
  
  
  public Sling launch() {
    // GM/r^2 = v^2/r
    // v = (GM/r)^(1/2)
    double radius = Constants.EARTH_RADIUS + initSurfaceDistance;
    double speed = Math.sqrt(Constants.G_EARTH / radius);
    
    Vector cm = new Vector(radius, 0, 0);
    Vector velocity = new Vector(0, speed, 0);
    
    Sling sling = Sling.new1kgSling(a2bRatio, new SphericalBodyPotential());
    
    
    // We *could do a refined center of mass calculation
    // like below (which I backed out of), but what's the point when
    //
    //  i) we intend to compare energy gain depending on orientation
    //  ii) the form factor is small to begin with
    //  iii) and if not, center of inertial mass (CM) and center of
    //       gravitational mass don't coincide anyway (the whole
    //       point of this exercise)
    //
    // m1*x1 + m2*x2 = 0
    // m1/m2 = -x2/x1
    // x1 - x2 = initLength
    // m1/m2 = (initLength - x1)/x1
    // m1/m2 + 1 = initLength/x1
    // x1 = initLength / (1 + m1/m2)
    //
    // (One refinement to consider is to set the starting energy
    // instead. blah..)
    
//    double x1 = initLength / (1 + a2bRatio);
//    double x2 = x1 - initLength;
    
    sling.getBobA().getPos().set(initLength/2 , 0, 0);
    sling.getBobB().getPos().set(-initLength/2 , 0, 0);
    
    if (orientation != null) {
      applyOrientation(sling.getBobA().getPos());
      applyOrientation(sling.getBobB().getPos());
    }
    
    sling.getBobA().getPos().add(cm);
    sling.getBobB().getPos().add(cm);
    
    sling.getBobA().getVel().set(velocity);
    sling.getBobB().getVel().set(velocity);
    
    return sling;
  }
  
  
  private void applyOrientation(Vector pos) {
    pos.set(orientation.multiply(pos));
  }
  

}
