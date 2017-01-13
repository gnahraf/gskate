/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.File;
import java.io.FilenameFilter;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HashedFilepath extends HashedFilename {
  
  private final File dir;
  private final FilenameFilter filter;

  
  /**
   * Creates a new instance. If <tt>dir</tt> does not exist, the constructor
   * creates the directory.
   */
  public HashedFilepath(File dir, String prefix, String extension) {
    this(dir, prefix, extension, true);
  }
  
  /**
   * Creates a new instance in the specified parent directory.
   * 
   * @param ensureDirectory  if <tt>true</tt>, then <tt>dir</tt> is guaranteed to exist
   *                         on return
   */
  public HashedFilepath(File dir, String prefix, String extension, boolean ensureDirectory) {
    super(prefix, extension);
    this.dir = dir;
    this.filter = super.getFilenameFilter();
    if (dir == null)
      throw new IllegalArgumentException("dir " + dir);
    if (ensureDirectory && !dir.isDirectory()) {
      if (dir.exists())
        throw new IllegalArgumentException("not a directory: " + dir);
      if (!dir.mkdirs() && !dir.isDirectory())
        throw new IllegalArgumentException("failed to create directory " + dir);
    }
  }
  
  
  
  
  
  
  public File toFilepath(byte[] hash) {
    return new File(dir, toFilename(hash));
  }
  
  
  public File toFilepath(String hash) {
    return new File(dir, toFilename(hash));
  }
  
  
  
  @Override
  public FilenameFilter getFilenameFilter() {
    return filter;
  }
  
  
  
  public final File getDirectory() {
    return dir;
  }
  
  
  
  public List<String> listHashes() {
    
    final String[] filenames = dir.list(filter);
    if (filenames == null || filenames.length == 0)
      return Collections.emptyList();
    
    return
        new AbstractList<String>() {
      
          @Override
          public String get(int index) {
            String filename = filenames[index];
            int start = getPrefix().length();
            int end = filename.length() - getExtension().length();
            return filename.substring(start, end);
          }
    
          @Override
          public int size() {
            return filenames.length;
          }
        };
  }

}
