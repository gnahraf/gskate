/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.math;

/**
 *
 */
public class ArrayMatrix extends Matrix {
  
  private final double[] array;
  private final int columns;
  private final int rows;
  
  
  public ArrayMatrix(int columns, int rows) {
    int count = rows * columns;
    if (count < 2 || rows < 0)
      throw new IllegalArgumentException("columns " + columns + "; rows " + rows);
    
    this.array = new double[count];
    this.columns = columns;
    this.rows = rows;
  }
  
  
  /**
   * 
   */
  public ArrayMatrix(double[] array) {
    this(array, (int) Math.round(Math.sqrt(array.length)));
  }

  /**
   * 
   */
  public ArrayMatrix(double[] array, int squareDim) {
    this(array, squareDim, squareDim);
  }

  /**
   * 
   */
  public ArrayMatrix(double[] array, int columns, int rows) {
    this.array = array;
    this.columns = columns;
    this.rows = rows;
    
    int count = rows * columns;
    if (count < 2 || rows < 0)
      throw new IllegalArgumentException("columns " + columns + "; rows " + rows);
    
    if (array.length < count)
      throw new IllegalArgumentException(
          "expected at least " + count + " elements in array; actual was " + array.length);
  }
  
  
  public ArrayMatrix(Matrix copy) {
    int count = copy.rows() * copy.columns();
    if (count < 2 || copy.rows() < 0)
      throw new IllegalArgumentException(
          "nonsensical matrix dimensions " + copy.columns() + " x " + copy.rows());
    
    this.array = new double[count];
    this.columns = copy.columns();
    this.rows = copy.rows();
    
    int index = 0;
    for (int r = 0; r < rows; ++r)
      for (int c = 0; c < columns; ++c)
        array[index++] = copy.val(c, r);
  }
  
  
  public ArrayMatrix(ArrayMatrix copy) {
    this.array = new double[copy.array.length];
    this.columns = copy.columns;
    this.rows = copy.rows;
    for (int index = array.length; index-- > 0; )
      this.array[index] = copy.array[index];
  }

  
  /**
   * Constructs a square matrix.
   */
  public ArrayMatrix(int squareDim) {
    this(squareDim, squareDim);
  }

  @Override
  public double val(int column, int row) throws IndexOutOfBoundsException {
    return array[column + row*columns];
  }

  @Override
  public int columns() {
    return columns;
  }

  @Override
  public int rows() {
    return rows;
  }
  
  
  public void val(double value, int column, int row) throws IndexOutOfBoundsException {
    array[column + row*columns] = value;
  }
  
  
  public void copyFrom(ArrayMatrix copy) {
    // check dimensions.. (might wanna relax this, later)
    if (copy.columns != columns || copy.rows != rows)
      throw new IllegalArgumentException("dimensions do not match");
    for (int index = columns * rows; index-- > 0; )
      array[index] = copy.array[index];
  }

}
