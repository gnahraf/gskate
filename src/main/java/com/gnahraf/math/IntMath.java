/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.math;

/**
 *
 */
public class IntMath {
  
  private IntMath() {  }
  
  
  public static long gcd(long p, long q) {
    if (p < 0 || q < 0)
      throw new IllegalArgumentException("negative arg: p " + p + "; q " + q);
    
    return gcdImpl(p, q);
  }
  
  private static long gcdImpl(long p, long q) {

    if (q == 0)
      return p;
    if (p == 0)
      return q;

    boolean pe = (p & 1) == 0;
    boolean qe = (q & 1) == 0;

    // p and q even
    if (pe && qe)
      return gcdImpl(p >> 1, q >> 1) << 1;

    // p is even, q is odd
    else if (pe)
      return gcdImpl(p >> 1, q);

    // p is odd, q is even
    else if (qe)
      return gcdImpl(p, q >> 1);

    // p and q odd, p >= q
    else if (p >= q)
      return gcdImpl((p-q) >> 1, q);

    // p and q odd, p < q
    else
      return gcdImpl(p, (q-p) >> 1);
  }

}
