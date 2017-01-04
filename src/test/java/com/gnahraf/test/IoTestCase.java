/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.test;

import java.io.File;
import java.lang.reflect.Method;

import com.gnahraf.io.PathnameGenerator;

/**
 *
 */
public class IoTestCase {
  
  
  public final File outputDir;

  /**
   * 
   */
  public IoTestCase() {
    TestOutputFiles testDirs = new TestOutputFiles();
    this.outputDir = testDirs.getOutputPath(getClass());
    outputDir.mkdirs();
    if (!outputDir.isDirectory())
      throw new IllegalStateException("failed to create test output dir " + outputDir);
  }
  
  
  
  public File getMethodOutputDir(Object innerMethodObject) {
    Class<?> clazz = innerMethodObject.getClass();
    Method testMethod = clazz.getEnclosingMethod();
    if (testMethod == null)
      throw new IllegalArgumentException(
          "argument type not defined inside test case method: " + innerMethodObject);
    if (!testMethod.getDeclaringClass().equals(getClass()))
      throw new IllegalArgumentException(
          "argument type not defined in test case class: " + innerMethodObject);
    File dir = new File(outputDir, testMethod.getName());
    dir.mkdir();
    if (!dir.isDirectory())
      throw new IllegalStateException(
          "failed to create test method output directory " + dir);
    
    return dir;
  }
  
  
  
  public File getMethodOutputFilepath(Object innerMethodObject, String prefix) {
    return getMethodOutputFilepath(innerMethodObject, prefix, null);
  }
  
  public File getMethodOutputFilepath(Object innerMethodObject, String prefix, String postfix) {
    File dir = getMethodOutputDir(innerMethodObject);
    return new PathnameGenerator(dir, prefix, postfix).newPath();
  }

}
