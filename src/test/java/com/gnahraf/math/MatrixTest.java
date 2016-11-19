package com.gnahraf.math;


import static org.junit.Assert.*;

import org.junit.Test;

public class MatrixTest {

  @Test
  public void test1x2() {
    ArrayMatrix m = new ArrayMatrix(1, 2);
    assertEquals(1, m.columns());
    assertEquals(2, m.rows());
    assertFalse(m.isSquare());
    m.val(1, 0, 1);
    assertEquals(0, m.val(0, 0), 0);
    assertEquals(1, m.val(0, 1), 0);
    try {
      m.determinant();
      fail();
    } catch (UnsupportedOperationException expected) {
      System.out.println("expected: " + expected);
    }
    
    try {
      m.getMinor(0, 0);
      fail();
    } catch (IllegalArgumentException expected) {
      System.out.println("expected: " + expected);
      
    }
  }
  
  
  @Test
  public void test2x2() {
    ArrayMatrix m = new ArrayMatrix(2, 2);
    assertEquals(0, m.determinant(), 0);
    m.val(1, 0, 0);
    m.val(3, 1, 0);
    m.val(2, 1, 1);
    assertEquals(2, m.columns());
    assertEquals(2, m.rows());
    assertTrue(m.isSquare());
    assertEquals(1, m.val(0, 0), 0);
    assertEquals(3, m.val(1, 0), 0);
    assertEquals(0, m.val(0, 1), 0);
    assertEquals(2, m.val(1, 1), 0);
    assertEquals(2, m.determinant(), 0);
  }
  
  
  @Test
  public void test3x3() {
    ArrayMatrix m = new ArrayMatrix(3);
    int a = 3;
    int b = 1;
    int c = 5;
    m.val(a, 0, 0);
    m.val(b, 1, 1);
    m.val(c, 2, 2);
    
    assertEquals(a, m.val(0, 0), 0);
    assertEquals(b, m.val(1, 1), 0);
    assertEquals(c, m.val(2, 2), 0);
    
    assertEquals(a*b*c, m.determinant(), 1e-9);
  }
  
  
  @Test
  public void test3x4() {
    ArrayMatrix m = new ArrayMatrix(3, 4);
    int a = 3;
    int b = 1;
    int c = 5;
    m.val(a, 0, 0);
    m.val(b, 1, 1);
    m.val(c, 2, 2);
    
    assertEquals(a, m.val(0, 0), 0);
    assertEquals(b, m.val(1, 1), 0);
    assertEquals(c, m.val(2, 2), 0);
  }
  
  
  @Test
  public void test4x3() {
    ArrayMatrix m = new ArrayMatrix(3, 4);
    int a = 3;
    int b = 1;
    int c = 5;
    m.val(a, 0, 3);
    m.val(b, 2, 1);
    m.val(c, 2, 2);
    
    assertEquals(a, m.val(0, 3), 0);
    assertEquals(b, m.val(2, 1), 0);
    assertEquals(c, m.val(2, 2), 0);
  }
  
  
  @Test
  public void test4x3CopyConstructor() {
    double[] array =
      {
        -3,   0,   5,  12,
        24,   1,   7,  -9,
        -15,  0,  29,  13,
        11, -37,  -1,  17,
      };
    Matrix m = new ArrayMatrix(array, 4, 3);
    Matrix copy = new ArrayMatrix(m);
    assertEqual(m, copy, 0);
  }
  
  
  @Test
  public void test4x3CopyConstructor2() {
    double[] array =
      {
        -3,   0,   5,  12,
        24,   1,   7,  -9,
        -15,  0,  29,  13,
        11, -37,  -1,  17,
      };
    ArrayMatrix m = new ArrayMatrix(array, 4, 3);
    Matrix copy = new ArrayMatrix(m);
    assertEqual(m, copy, 0);
  }
  
  
  
  public static void assertEqual(Matrix expected, Matrix actual, double tolerance) {
    assertEquals(expected.columns(), actual.columns());
    assertEquals(expected.rows(), actual.rows());
    
    for (int i = expected.columns(); i-- > 0; )
      for (int j = expected.rows(); j-- > 0; )
        assertEquals(expected.val(i, j), actual.val(i, j), tolerance);
  }
  
  
  @Test
  public void test4x4Diagonal() {
    ArrayMatrix m = new ArrayMatrix(4);
    int a = 3;
    int b = -1;
    int c = 5;
    int d = 7;
    m.val(a, 0, 0);
    m.val(b, 1, 1);
    m.val(c, 2, 2);
    m.val(d, 3, 3);
    
    assertEquals(a, m.val(0, 0), 0);
    assertEquals(b, m.val(1, 1), 0);
    assertEquals(c, m.val(2, 2), 0);
    assertEquals(d, m.val(3, 3), 0);
    
    assertEquals(a*b*c*d, m.determinant(), 1e-9);
    m.val(0, 2, 2);
    assertEquals(0, m.determinant(), 0);
  }
  
  
  @Test
  public void test4x4() {
    double[] array =
      {
        -3,   0,   5,  12,
        24,   1,   7,  -9,
        -15,  0,  29,  13,
        11, -37,  -1,  17,
      };
    
    final double expectedDet = -287003;
    
    Matrix m = new ArrayMatrix(array, 4, 4);
    assertEquals(expectedDet, m.determinant(), 1e-9);
  }
  

}
