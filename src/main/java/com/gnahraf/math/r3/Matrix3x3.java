/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.math.r3;

import com.gnahraf.math.matrix.ArrayMatrix;
import com.gnahraf.math.matrix.Matrix;
import com.gnahraf.math.matrix.MatrixComposition;

/**
 *
 */
public class Matrix3x3 extends ArrayMatrix {

  public Matrix3x3() {
    super(3);
  }

  /**
   * @param array
   */
  public Matrix3x3(double[] array) {
    super(array, 3);
  }

  /**
   * @param copy
   */
  public Matrix3x3(Matrix3x3 copy) {
    super(copy);
  }

  /**
   * @param copy
   */
  public Matrix3x3(Matrix copy) {
    super(copy);
    if (copy.columns() != 3 || copy.rows() != 3)
      throw new IllegalArgumentException("expected 3X3 matrix but was " + copy);
  }
  
  
  
  public Vector multiply(Vector vector) {
    return new Vector(new MatrixComposition(this, vector));
  }
  
  
  
  
  public static Matrix3x3 rotateAboutX(double radians) {
    double s = Math.sin(radians);
    double c = Math.cos(radians);
    return
        new Matrix3x3(
            new double[]
            {
              1,   0,   0,
              0,   c,  -s,
              0,   s,   c
            });
  }
  
  
  public static Matrix3x3 rotateAboutY(double radians) {
    double s = Math.sin(radians);
    double c = Math.cos(radians);
    return
        new Matrix3x3(
            new double[]
            {
              c,   0,   s,
              0,   1,   0,
             -s,   0,   c
            });
  }
  
  
  public static Matrix3x3 rotateAboutZ(double radians) {
    double s = Math.sin(radians);
    double c = Math.cos(radians);
    return
        new Matrix3x3(
            new double[]
            {
              c,  -s,   0,
              s,   c,   0,
              0,   0,   1
            });
  }
  
  
  
  public static Matrix3x3 rotate(double radians, Vector axis) {
    double s = Math.sin(radians);
    double c = Math.cos(radians);
    axis = new Vector(axis).toUnit();
    double x = axis.getX();
    double y = axis.getY();
    double z = axis.getZ();
    
    double _c = 1 - c;
    double xs = x * s;
    double ys = y * s;
    double zs = z * s;
    
    double xy_c = x * y * _c;
    double xz_c = x * z * _c;
    double yz_c = y * z * _c;
    
    return
        new Matrix3x3(
            new double[] {
                c + x*x*_c,   xy_c - zs,   xz_c + ys,
                yz_c + zs,    c + y*y*_c,  yz_c - xs,
                xz_c - ys,    yz_c + xs,   c + z*z*_c
            });
  }
  
  
  public static Matrix3x3 newIdentity() {
    Matrix3x3 i = new Matrix3x3();
    i.val(1.0, 0, 0).val(1.0, 1, 1).val(1.0, 2, 2);
    return i;
  }
  

}
