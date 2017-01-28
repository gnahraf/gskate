/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 */
public class HashedFilepath extends HashedFilename {
  
  private final File dir;

  
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
  
  
  
  public final File getDirectory() {
    return dir;
  }
  
  
  
  public List<String> listHashes() {
    
    final String[] filenames = dir.list(getFilenameFilter());
    if (filenames == null || filenames.length == 0)
      return Collections.emptyList();
    
    return
        new AbstractList<String>() {
      
          @Override
          public String get(int index) {
            return toHash(filenames[index]);
          }
    
          @Override
          public int size() {
            return filenames.length;
          }
        };
  }
  
  
  // FIXME: make me private
  private boolean accept(Path path) {
    String filename = path.getFileName().toString();
    boolean ok = filename.endsWith(getExtension());
    ok &= filename.startsWith(getPrefix());
    return ok;
  }
  
  
  private String toHash(String filename) {
    int start = getPrefix().length();
    int end = filename.length() - getExtension().length();
    return filename.substring(start, end);
  }
  
  
  
  public Stream<String> streamHashes() {
    try {
      return
          Files.list(dir.toPath())
            .filter(path -> accept(path))
            .map(path -> toHash(path.getFileName().toString()));
    } catch (IOException iox) {
      throw new IoRuntimeException(iox);
    }
    
  }

}
