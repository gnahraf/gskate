/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.model;

/**
 * A bob of mass 1 kg. Or if you prefer, this is a per kilogram calculation.
 * We're using these for animation, of course. The general contract here is
 * that the physical state of the instance is encapsulated in its positional
 * and velocity vectors, and the {@linkplain Potential potential} energy.
 * The acceleration vector encoded in this class, on the other hand, should
 * properly be understood as just a work buffer that anyone can use. When you
 * need to know it, you calculate it from scratch.
 * <p/>
 * Note this class also has some rudimentary support for related vector operations.
 * The mutator methods taking another instance as argument always affect <em>this</em>
 * instance, not the argument.
 */
public class Bob extends DynaVector {
  
  public Bob() {  }
  
  
  public Bob(Bob copy) {
    super(copy);
  }
}
