/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.math.matrix;


/**
 *
 */
public class MatrixComposition extends Matrix {
  
  private final Matrix a;
  private final Matrix b;

  /**
   * 
   */
  public MatrixComposition(Matrix a, Matrix b) {
    this.a = a;
    this.b = b;
    
    if (a.columns() != b.rows()) {
      throw new IllegalArgumentException(
          "dimensions mismatch [" + a.columns() + "x" + a.rows() +
          "].[" + b.columns() + "x" + b.rows() + "]");
    }
  }

  @Override
  public double val(int column, int row) throws IndexOutOfBoundsException {
    double val = 0;
    for (int i = a.columns(); i-- > 0; )
      val += a.val(i, row) * b.val(column, i);
    return val;
  }

  @Override
  public int columns() {
    return b.columns();
  }

  @Override
  public int rows() {
    return a.rows();
  }

}
