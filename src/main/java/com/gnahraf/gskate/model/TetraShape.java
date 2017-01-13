/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;


import static com.gnahraf.util.list.DoubleArrayList.asList;

import com.gnahraf.math.DetMatrix;

/**
 * A tetrahedron specified by the lengths of its edges. So this description
 * is independent of the coordinate system, and does not specify location
 * or orientation.
 */
public class TetraShape {

  
  /**
   * Lengths of the 6 edges of the tetra.
   */
  private final double[] lengths = new double[6];
  
  private final DetMatrix cayleyMenger;

  /**
   * 
   */
  public TetraShape() {
    for (int i = 6; i-- > 0; )
      lengths[i] = 1;
    double[] array =
      {
        0, 1, 1, 1, 1,
        1, 0, 1, 1, 1,
        1, 1, 0, 1, 1,
        1, 1, 1, 0, 1,
        1, 1, 1, 1, 0,
      };
    this.cayleyMenger = new DetMatrix(array);
  }
  
  
  
  private void updateCaleyMenger(double[] edges) {

//    {
//      0, 1,    1,    1,    1,
//      1, 0, d01s, d02s, d03s,
//      1, .,    0, d12s, d13s,
//      1, ., ., .,    0, d23s,
//      1, ., ., .,    .,    0,
//    }
    
    double dSq = length(edges, 0, 1);
    dSq *= dSq;
    cayleyMenger.val(dSq, 2, 1);
    cayleyMenger.val(dSq, 1, 2);
    
    dSq = length(edges, 0, 2);
    dSq *= dSq;
    cayleyMenger.val(dSq, 3, 1);
    cayleyMenger.val(dSq, 1, 3);
    
    dSq = length(edges, 0, 3);
    dSq *= dSq;
    cayleyMenger.val(dSq, 4, 1);
    cayleyMenger.val(dSq, 1, 4);
    
    dSq = length(edges, 1, 2);
    dSq *= dSq;
    cayleyMenger.val(dSq, 3, 2);
    cayleyMenger.val(dSq, 2, 3);
    
    dSq = length(edges, 1, 3);
    dSq *= dSq;
    cayleyMenger.val(dSq, 4, 2);
    cayleyMenger.val(dSq, 2, 4);
    
    dSq = length(edges, 2, 3);
    dSq *= dSq;
    cayleyMenger.val(dSq, 4, 3);
    cayleyMenger.val(dSq, 3, 4);
  }
  
  private double length(double[] edges, int bobA, int bobB) {
    return edges[TetraEdge.forBobs(bobA, bobB).index];
  }
  
  public double length(int bobA, int bobB) {
    return this.lengths[TetraEdge.forBobs(bobA, bobB).index];
  }
  
  public double length(int index) {
    return lengths[index];
  }
  
  
  public double length(TetraEdge edge) {
    return lengths[edge.index];
  }
  
  
  /**
   * Sets the lengths of the edges after validation.
   * 
   * @param edges array of at least 6 elements from which values
   *        are copied
   */
  public void setLengths(double[] edges) {
    validateLengths(edges, 0);
    for (int index = 6; index-- > 0; )
      lengths[index] = edges[index];
  }
  
  
  public void addLengths(double[] deltas) {
    double[] newLengths = new double[6];
    for (int index = 6; index-- > 0; )
      newLengths[index] = lengths[index] + deltas[index];
    setLengths(newLengths);
  }
  
  
  
  public void setLengths(double equiLength) {
    if (equiLength <= 0)
      throw new IllegalArgumentException("equiLength " + equiLength);
    for (int index = 6; index-- > 0; )
      lengths[index] = equiLength;
    updateCaleyMenger(lengths);
  }
  
  
  
  public void copyFrom(TetraShape other) {
    for (int index = 6; index-- > 0; )
      lengths[index] = other.lengths[index];
    cayleyMenger.copyFrom(other.cayleyMenger);
  }
  
  
  public void stretchCorner(int bob, double factor) {
    stretchCorner(TetraCorner.forBob(bob), factor);
  }
  
  
  public void stretchCorner(TetraCorner corner, double factor) {
    double[] newLengths = new double[6];
    for (int index = 6; index-- > 0; )
      newLengths[index] = lengths[index];

    for (int oi = 3; oi-- > 0; ) {
      TetraEdge edge = corner.edge(oi);
      newLengths[edge.index] *= factor;
    }
    setLengths(newLengths);
  }
  
  
  
  public void stretchFace(TetraFace face, double factor) {
    double[] newLengths = new double[6];
    for (int index = 6; index-- > 0; )
      newLengths[index] = lengths[index];
    
    
    for (int oi = 0; oi < 3; ++oi)
      newLengths[face.edge(oi).index] *= factor;
    
    setLengths(newLengths);
  }
  
  
  
  public double getCayleyMengerDeterminant() {
    return cayleyMenger.determinant();
  }
  
  
  public double getVolume() {
    return Math.sqrt(cayleyMenger.determinant() / 288);
  }
  
  
  
  @Override
  public final boolean equals(Object obj) {
    if (obj == this)
      return true;
    return obj instanceof TetraShape && equals((TetraShape) obj);
  }
  
  
  
  public final boolean equals(TetraShape other) {
    if (other == null)
      return false;
    else if (other == this)
      return true;
    int index = 6;
    while (index-- > 0 && lengths[index] == other.lengths[index]);
    return index == -1;
  }
  
  
  
  public final int hashCode() {
    double sum = 97 * lengths[0];
    sum += 101 * lengths[1];
    sum += 103 * lengths[2];
    sum += 105 * lengths[3];
    sum += 107 * lengths[4];
    sum += 109 * lengths[5];
    return Double.hashCode(sum);
  }
  
  
  
  
  /**
   * Validates the given lengths indeed describe a possible tetrahedron. This is
   * called right before edge lengths are copied and
   * involves checking that Cayley-Menger determinant is non-negative. On return,
   * the CM matrix is already updated. If the method fails, the matrix is restored to
   * its old state.
   * 
   * @param edges array of at least 6 doubles specifying the length
   * @param minEdgeLength the minimum edge length (exclusive). Each of the
   *        elements of <tt>edges</tt> must be greater in length than this
   *        amount. Should be &ge; 0.
   */
  private void validateLengths(double[] edges, double minEdgeLength)
      throws
      IllegalArgumentException,
      IndexOutOfBoundsException {

    // validate..
    // ensure no length is too small (and not negative)
    for (int index = 6; index-- > 0; )
      if (edges[index] <= minEdgeLength)
        throw new IllegalArgumentException(
            "edges[" + index + "] " + edges[index] + " < minEdgeLength " + minEdgeLength);
    
    updateCaleyMenger(edges);
    if (cayleyMenger.determinant() < 0) {
      double det = cayleyMenger.determinant();
      updateCaleyMenger(this.lengths);
      throw new IllegalArgumentException("negative det " + det + " from " + asList(edges) + "\n" + cayleyMenger.toDebugString());
    }

    // OLD CHECK FOR TRIANGLE INEQUALITY (NECESSARY, BUT NOT SUFFICIENT)
    // we validate the following triangle relation for each face..
    // let c be the max length, and b, and c the other 2 sides
    // then,
    // c < a + b
    // 2c < a + b + c
    
    // proceed with each face, each face being defined as the
    // triangle that does not contain the bob with that face index
    // i.e. the face opposite the bob at that index
//    for (int face = 0; face < 4; ++face) {
//      int i = (face + 1) % 4;
//      int j = (face + 2) % 4;
//      int k = (face + 3) % 4;
//      
//      double a = edges[TetraEdge.forBobs(i, j).index];
//      double b = edges[TetraEdge.forBobs(j, k).index];
//      double c = edges[TetraEdge.forBobs(k, i).index];
//      
//      double max = Math.max(c, Math.max(a, b));
//      if (max * 2 >= a + b + c)
//        throw new IllegalArgumentException("face " + face);
//    }
  }

}
