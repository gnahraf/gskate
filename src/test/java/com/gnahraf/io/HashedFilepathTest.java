/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.Test;

import com.gnahraf.test.IoTestCase;

/**
 *
 */
public class HashedFilepathTest extends IoTestCase {
  
  
  private int counter;
  

  @Test
  public void test() throws IOException {
    Object methodLabel = new Object() { };
    File dir = getMethodOutputFilepath(methodLabel, "hfp");
    HashedFilepath hfp = new HashedFilepath(dir, "pre-", ".ext");
    final int count = 9;
    for (int i = 1; i <= count; ++i) {
      File file = hfp.toFilepath(Integer.toString(i));
      file.createNewFile();
    }
    
    Stream<String> hashStream = hfp.streamHashes();
    this.counter = 0;
    hashStream.forEach(s -> {
      System.out.println(s);
      int hash = Integer.valueOf(s);
      assertTrue(0 < hash && hash < 10);
      ++counter;
    });
    
    assertEquals(count, counter);
  }
  
  
  

}
