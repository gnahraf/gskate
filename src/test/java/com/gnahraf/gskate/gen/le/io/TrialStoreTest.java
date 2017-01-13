/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import static org.junit.Assert.*;
import static com.gnahraf.gskate.model.TetraTest.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraTest;
import com.gnahraf.test.IoTestCase;
import com.gnahraf.util.data.NormPoint;

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
  
  
  
  
  @Test
  public void testReadWriteCraftState() {
    TrialStore store = newStore(new Object() { });
    Tetra craft = newCraft();
    CraftState input = new CraftState(1025, craft);
    String id = store.writeCraftState(input);
    CraftState output = store.readCraftState(id);
    assertEquals(input, output);
    
    
    craft.setTetherByIndex(2, craft.getTetherByIndex(2) + .001);
    
    CraftState input2 = new CraftState(1025, craft);
    String id2 = store.writeCraftState(input2);
    assertNotEquals(id, id2);
    CraftState output2 = store.readCraftState(id2);
    assertEquals(input2, output2);
    
    assertNotEquals(output, output2);
  }
  
  

  @Test
  public void testWriteCraftIdempotence() {
    TrialStore store = newStore(new Object() { });
    Tetra craft = newCraft();
    CraftState input = new CraftState(1025, craft);
    String id = store.writeCraftState(input);
    
    assertEquals(id, store.writeCraftState(input));
    assertEquals(input, store.readCraftState(id));
  }
  
  
  
  @Test
  public void testReadWriteRegularShapeTrialCommandSet() {
    TrialStore store = newStore(new Object() { });
    ArrayList<NormPoint> commands = new ArrayList<>();
    commands.add(new NormPoint(0.1, 0.01));
    commands.add(new NormPoint(0.4, 0.51));
    commands.add(new NormPoint(0.8, 0.2));
    
    String id = store.writeRegularShapeTrialCommandSet(commands);
    
    assertEquals(commands, store.readRegularShapeTrialCommandSet(id));
  }
  
  
  
  @Test
  public void testWriteRegularShapeTrialCommandSetIdempotence() {
    TrialStore store = newStore(new Object() { });
    ArrayList<NormPoint> commands = new ArrayList<>();
    commands.add(new NormPoint(0.1, 0.01));
    commands.add(new NormPoint(0.49, 0.51));
    commands.add(new NormPoint(0.8, 0.2));
    
    String id = store.writeRegularShapeTrialCommandSet(commands);
    
    assertEquals(id, store.writeRegularShapeTrialCommandSet(commands));
    assertEquals(commands, store.readRegularShapeTrialCommandSet(id));
  }
  
  
  
  @Test
  public void testReadWriteRegularShapeTransformData() {
    TrialStore store = newStore(new Object() { });
    
    RegularShapeTransform.Builder transform = new RegularShapeTransform.Builder();
    transform.config = "c";
    transform.startState = "start";
    transform.endState = "end";
    transform.commandSet = "cmds";
    
    RegularShapeTransform txf = transform.build();
    String id = store.writeRegularShapeTransformData(txf);
    assertEquals(txf, store.readRegularShapeTransform(id));
    
    // idempotence check
    assertEquals(id, store.writeRegularShapeTransformData(txf));
    assertEquals(txf, store.readRegularShapeTransform(id));
  }
  
  
  
  
  private TrialStore newStore(Object innerMethodObject) {
    File dir = getMethodOutputFilepath(innerMethodObject, STORE_PREFIX);
    return new TrialStore(dir);
  }
  
  
  private File newFilepath(Object innerMethodObject) {
    return getMethodOutputFilepath(innerMethodObject, FILE_PREFIX);
  }

}
