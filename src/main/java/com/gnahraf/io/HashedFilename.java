/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.File;
import java.io.FilenameFilter;

import javax.xml.bind.DatatypeConverter;

/**
 *
 */
public class HashedFilename {
  
  private final String prefix;
  private final String extension;

  /**
   * 
   */
  public HashedFilename(String prefix, String extension) {
    this.prefix = prefix == null ? "" : prefix;
    this.extension = extension == null ? "" : extension;
  }
  
  
  
  
  
  
  public String toFilename(String hash) {
    
    if (hash == null || hash.isEmpty())
      throw new IllegalArgumentException("hash " + hash + "<");
    
    return prefix + hash + extension;
  }
  
  
  
  public String toFilename(byte[] hash) {
    
    if (hash == null || hash.length == 0)
      throw new IllegalArgumentException("hash " + hash);
    
    return prefix + DatatypeConverter.printHexBinary(hash) + extension;
  }
  
  
  
  
  

  public final String getPrefix() {
    return prefix;
  }

  public final String getExtension() {
    return extension;
  }
  
  
  
  public FilenameFilter getFilenameFilter() {
    return new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.startsWith(prefix) && name.endsWith(extension);
      }
    };
  }
  
  

}
