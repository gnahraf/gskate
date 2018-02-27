/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.math.matrix;


/**
 * A matrix. The purpose of this abstraction is to lessen the need for copying
 * stuff by supporting views (filters) onto existing matrices. This can also support
 * functional style idioms that delay computations until actually needed.
 */
public abstract class Matrix {
  
  public abstract double val(int column, int row) throws IndexOutOfBoundsException;
  
  public abstract int columns();
  
  public abstract int rows();
  
  public boolean isSquare() {
    return rows() == columns();
  }
  
  
  public Matrix getMinor(int deletedColumn, int deletedRow) {
    return new MinorMatrix(this, deletedColumn, deletedRow);
  }
  
  
  public double determinant() throws UnsupportedOperationException {
    if (!isSquare())
      throw new UnsupportedOperationException("not a square matrix (" + columns() + "x" + rows() + ")");
    
    if (rows() == 2)
      return val(0,0)*val(1,1) - val(0,1)*val(1,0);
    
    double det = 0;
    for (int c = 0, sign = 1; c < columns(); ++c, sign *= -1) {
      double factor = val(c, 0) * sign;
      if (factor != 0)
        det += factor * getMinor(c, 0).determinant();
    }
    return det;
  }
  
  
  
  public Matrix product(Matrix m) {
    return new MatrixComposition(this, m);
  }

}
