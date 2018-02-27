/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.math.matrix;

/**
 *
 */
public class MinorMatrix extends Matrix {
  
  private final Matrix base;
  private final int deletedColumn;
  private final int deletedRow;

  /**
   * 
   */
  public MinorMatrix(Matrix base, int deletedColumn, int deletedRow) {
    this.base = base;
    this.deletedColumn = deletedColumn;
    this.deletedRow = deletedRow;
    
    if (deletedColumn < 0 || deletedColumn >= base.columns())
      throw new IllegalArgumentException(
          "deletedColumn " + deletedColumn + "; base.columns() " + base.columns());
    
    if (deletedRow < 0 || deletedRow >= base.rows())
      throw new IllegalArgumentException(
          "deletedRow " + deletedRow + "; base.rows() " + base.rows());
    
    if (base.rows() < 3 && base.columns() < 3)
      throw new IllegalArgumentException(
          "base matrix is " + base.columns() + "x" + base.rows());
  }

  

  
  @Override
  public double val(int column, int row) throws IndexOutOfBoundsException {
    if (column >= deletedColumn)
      ++column;
    if (row >= deletedRow)
      ++row;
    return base.val(column, row);
  }

  
  @Override
  public int columns() {
    return base.columns() - 1;
  }

  
  
  @Override
  public int rows() {
    return base.rows() - 1;
  }

}
