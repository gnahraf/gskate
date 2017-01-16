/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io.store;


import java.io.File;
import java.nio.ByteBuffer;

import javax.xml.bind.JAXB;

import com.gnahraf.io.CorruptionException;
import com.gnahraf.io.HashedFilepath;

/**
 * Stores object state in XML format. Note the hashing scheme is independent of
 * of what's written. (So it's OK, for example, to edit one of these XML files,
 * as in adding comments, as long as you don't break the read-codepath.)
 * 
 * @param T a mutable struct type with public members suitable for <tt>JAXB</tt>. To
 *        bridge this mutable type to an immutable one, consider
 *        {@linkplain ObjectManager#map(ObjectManager, java.util.function.Function, java.util.function.Function) ObjectManager.map}
 *      ObjectManager#map(ObjectManager, java.util.function.Function, java.util.function.Function)
 */
public class XmlObjectManager<T> extends HashedObjectManager<T> {
  
  private final Class<T> clazz;

  
  /**
   * @param encoder  used to compute a hash of the object
   */
  public XmlObjectManager(HashedFilepath hashedPath, Encoder<T> encoder, Class<T> clazz) {
    super(hashedPath, encoder);
    this.clazz = clazz;
    
    if (clazz == null)
      throw new IllegalArgumentException("null clazz");
  }

  
  
  
  
  
  @Override
  protected T readObjectFile(File file) {
    return JAXB.unmarshal(file, clazz);
  }

  @Override
  protected void writeObjectFile(File file, T object, ByteBuffer buffer) {
    JAXB.marshal(object, file);
  }



  @Override
  protected void validateFile(File file, T object, ByteBuffer buffer) throws CorruptionException {
    boolean fail = !object.equals(readObjectFile(file));
    if (fail)
      throw new CorruptionException(file.toString());
  }

}
