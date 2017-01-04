/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.File;

/**
 *
 */
public class PathnameGenerator {
  
  private final int stampCountMask;
  private final File dir;
  private final String filenamePrefix;
  private final String filenamePostfix;
  
  private int count;
  



  /**
   * 
   */
  public PathnameGenerator(String filenamePrefix) {
    this(new File("."), filenamePrefix);
  }

  /**
   * 
   */
  public PathnameGenerator(File dir, String filenamePrefix) {
    this(dir, filenamePrefix, 5, null);
  }

  /**
   * 
   */
  public PathnameGenerator(File dir, String filenamePrefix,  String filenamePostfix) {
    this(dir, filenamePrefix, 5, filenamePostfix);
  }
  

  /**
   * 
   */
  public PathnameGenerator(File dir, String filenamePrefix, int tokenDigits,  String filenamePostfix) {
    
    if (tokenDigits < 1 || tokenDigits > 9)
      throw new IllegalArgumentException("tokenDigits " + tokenDigits);
    int stampMask = 1;
    for (int i = 0; i < tokenDigits; ++i)
      stampMask *= 10;
    stampCountMask = stampMask;
    
    if (!dir.mkdirs() && !dir.isDirectory())
      throw new IllegalArgumentException(dir + " not a directory, and failed to create it");
    this.dir = dir;
    
    this.filenamePrefix = filenamePrefix == null ? "" : filenamePrefix;
    this.filenamePostfix = filenamePostfix == null ? "" : filenamePostfix;
  }
  
  
  public File forToken(int token) {
    return new File(dir, filenamePrefix + toStringToken(token) + filenamePostfix);
  }


  public File newPath() {
    File file;
    do {
      file = new File(dir, nextFilename());
    } while (file.exists());
    return file;
  }
  
  
  
  public int parseFilenameToken(File file) {
    return parseFilenameToken(file.getName());
  }
  
  
  public int parseFilenameToken(String filename) {
    return
        Integer.parseInt(
            filename.substring(
                filenamePrefix.length(),
                filename.length() - filenamePostfix.length()));
  }

  
  
  
  
  protected String nextFilename() {
    return filenamePrefix + nextFilenameToken() + filenamePostfix;
  }
  
  protected final String nextFilenameToken() {
    if (++count >= stampCountMask)
      throw new IllegalStateException("count exhausted: " + count);
    return toStringToken(count);
  }
  
  
  private String toStringToken(int token) {
    if (token >= stampCountMask)
      throw new IllegalArgumentException("token too big: " + token);
    int t = stampCountMask + token;
    return Integer.toString(t).substring(1);
  }

}
