/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.io;


import java.nio.ByteBuffer;

import com.gnahraf.gskate.model.Bob;


/**
 * Reads and writes <tt>Bob<tt>s to memory buffers. Suitable for serializing
 * and de-serializing to and from streams. Instances are stateless and therefore
 * safe under concurrent access.
 * <h4>Note</h4>
 * A <tt>Bob</tt>'s acceleration vector is not part of its state (it's just a
 * convenient scratch pad for calculating stuff), so it isn't serialized.
 */
public class BobSerializer {
  
  /**
   * Writes the given <tt>bob</tt> to the specified <tt>buffer</tt>. The position
   * of the buffer is advanced.
   */
  public void write(Bob bob, ByteBuffer buffer) {
    buffer.putDouble(bob.getX());
    buffer.putDouble(bob.getY());
    buffer.putDouble(bob.getZ());
    
    buffer.putDouble(bob.getVx());
    buffer.putDouble(bob.getVy());
    buffer.putDouble(bob.getVz());
    
//    buffer.putDouble(bob.getAx());
//    buffer.putDouble(bob.getAy());
//    buffer.putDouble(bob.getAz());
  }
  
  
  /**
   * Reads and returns the <tt>Bob</tt> encoded in the given <tt>buffer</tt>.
   */
  public Bob read(ByteBuffer buffer) {
    double x = buffer.getDouble();
    double y = buffer.getDouble();
    double z = buffer.getDouble();

    double vx = buffer.getDouble();
    double vy = buffer.getDouble();
    double vz = buffer.getDouble();
    
//    double ax = buffer.getDouble();
//    double ay = buffer.getDouble();
//    double az = buffer.getDouble();
    
    Bob bob = new Bob();
    bob.setPosition(x, y, z);
    bob.setVelocity(vx, vy, vz);
//    bob.setAcceleration(ax, ay, az);
    
    return bob;
  }

}
