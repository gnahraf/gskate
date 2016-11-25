/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.math;

/**
 *
 */
public class DetMatrix extends ArrayMatrix {
  
  private boolean dirty = true;
  private double cachedDet;

  /**
   * @param array
   */
  public DetMatrix(double[] array) {
    super(array);
  }

  /**
   * @param array
   * @param squareDim
   */
  public DetMatrix(double[] array, int squareDim) {
    super(array, squareDim);
  }

  /**
   * @param copy must be a square matrix
   */
  public DetMatrix(Matrix copy) {
    super(copy);
    if (!copy.isSquare())
      throw new IllegalArgumentException("not square: " + copy);
  }

  public DetMatrix(DetMatrix copy) {
    super(copy);
  }

  public DetMatrix(int squareDim) {
    super(squareDim);
  }
  
  
  

  @Override
  public void val(double value, int column, int row)
      throws IndexOutOfBoundsException {
    super.val(value, column, row);
    dirty = true;
  }

  @Override
  public double determinant() throws UnsupportedOperationException {
    if (dirty) {
      cachedDet = super.determinant();
      dirty = false;
    }
    return cachedDet;
  }
  
  
  @Override
  public void copyFrom(ArrayMatrix copy) {
    super.copyFrom(copy);
    dirty = true;
  }
  

  public void copyFrom(DetMatrix copy) {
    super.copyFrom(copy);
    dirty = copy.dirty;
    cachedDet = copy.cachedDet;
  }
  
  

  public String toDebugString() {
    StringBuilder string = new StringBuilder(16 * columns() * rows());
    String pad = "  ";
    for (int row = 0; row < rows(); ++row) {
      for (int col = 0; col < columns(); ++col) {
        string.append(val(col, row)).append(pad);
      }
      string.append('\n');
    }
    return string.toString();
  }

}
