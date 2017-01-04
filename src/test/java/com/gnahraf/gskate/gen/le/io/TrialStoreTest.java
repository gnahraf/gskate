/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.io.PathnameGenerator;
import com.gnahraf.test.IoTestCase;
import com.gnahraf.test.TestOutputFiles;

/**
 *
 */
public class TrialStoreTest extends IoTestCase {
  
  public final static String STORE_PREFIX = "store-";
  public final static String FILE_PREFIX = "file-";
  
  
  
  
  
  @Test
  public void testConstructor() {
    newStore(new Object() { });
  }
  
  
  @Test
  public void testReadWriteConstraintsFile() {
    TrialStore store = newStore(new Object() { });
    File file = newFilepath(new Object() { });
    
    Constraints constraints = new Constraints();
    constraints.initTetherLength = 105;
    
    store.writeConstraints(constraints, file);
    
    Constraints copy = store.readConstraints(file);
    assertNotEquals(constraints, new Constraints());
    assertEquals(constraints, copy);
    
    // attempt to write to existing file should fail
    try {
      store.writeConstraints(constraints, file);
      fail();
    } catch (RuntimeException expected) {  }
  }
  
  

  
  
  @Test
  public void testReadWriteConstraints() {
    TrialStore store = newStore(new Object() { });
    
    Constraints constraints = new Constraints();
    constraints.initTetherLength = 109;
    
    String id = store.writeConstraints(constraints);
    
    Constraints copy = store.readConstraints(id);
    
    assertEquals(constraints, copy);
  }
  
  @Test
  public void testWriteConstraintsIdempotence() {
    TrialStore store = newStore(new Object() { });
    
    Constraints constraints = new Constraints();
    constraints.initTetherLength = 109;
    
    String id = store.writeConstraints(constraints);
    
    assertEquals(id, store.writeConstraints(constraints));
    
    Constraints copy = store.readConstraints(id);
    
    assertEquals(constraints, copy);
  }
  
  
  
  
  
  private TrialStore newStore(Object innerMethodObject) {
    File dir = getMethodOutputFilepath(innerMethodObject, STORE_PREFIX);
    return new TrialStore(dir);
  }
  
  
  private File newFilepath(Object innerMethodObject) {
    return getMethodOutputFilepath(innerMethodObject, FILE_PREFIX);
  }

}
