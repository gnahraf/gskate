/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.le.io;


import static org.junit.Assert.*;
import static com.gnahraf.gskate.tetra.model.TetraTest.*;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.Test;

import com.gnahraf.gskate.tetra.gen.le.Constraints;
import com.gnahraf.gskate.tetra.gen.le.io.RegularShapeTransform;
import com.gnahraf.gskate.tetra.gen.le.io.TrialStore;
import com.gnahraf.gskate.tetra.model.CraftState;
import com.gnahraf.gskate.tetra.model.Tetra;
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
    
    String id = store.getConstraintsManager().write(constraints);
    
    Constraints copy = store.getConstraintsManager().read(id);
    
    assertEquals(constraints, copy);
  }
  
  @Test
  public void testWriteConstraintsIdempotence() {
    TrialStore store = newStore(new Object() { });
    
    Constraints constraints = new Constraints();
    constraints.initTetherLength = 109;
    
    String id = store.getConstraintsManager().write(constraints);
    
    assertEquals(id, store.getConstraintsManager().write(constraints));
    
    Constraints copy = store.getConstraintsManager().read(id);
    
    assertEquals(constraints, copy);
  }
  
  
  
  
  @Test
  public void testReadWriteCraftState() {
    TrialStore store = newStore(new Object() { });
    Tetra craft = newCraft();
    CraftState input = new CraftState(1025, craft);
    String id = store.getStateManager().write(input);
    CraftState output = store.getStateManager().read(id);
    assertEquals(input, output);
    
    
    craft.setTetherByIndex(2, craft.getTetherByIndex(2) + .001);
    
    CraftState input2 = new CraftState(1025, craft);
    String id2 = store.getStateManager().write(input2);
    assertNotEquals(id, id2);
    CraftState output2 = store.getStateManager().read(id2);
    assertEquals(input2, output2);
    
    assertNotEquals(output, output2);
  }
  
  

  @Test
  public void testWriteCraftIdempotence() {
    TrialStore store = newStore(new Object() { });
    Tetra craft = newCraft();
    CraftState input = new CraftState(1025, craft);
    String id = store.getStateManager().write(input);
    
    assertEquals(id, store.getStateManager().write(input));
    assertEquals(input, store.getStateManager().read(id));
  }
  
  
  @Test
  public void testStreamStates() {
    TrialStore store = newStore(new Object() { });
    Tetra craft = newCraft();
    CraftState input = new CraftState(1025, craft);
    String id = store.getStateManager().write(input);
    
    
    craft.setTetherByIndex(2, craft.getTetherByIndex(2) + .001);
    
    CraftState input2 = new CraftState(1025, craft);
    String id2 = store.getStateManager().write(input2);
    
    Stream<String> idStream = store.getStateManager().streamIds();
    ArrayList<String> ids = new ArrayList<String>();
    
    idStream.forEach(ids::add);
    assertEquals(2, ids.size());
    assertTrue(ids.contains(id));
    assertTrue(ids.contains(id2));
  }
  
  
  
  @Test
  public void testReadWriteRegularShapeTrialCommandSet() {
    TrialStore store = newStore(new Object() { });
    ArrayList<NormPoint> commands = new ArrayList<>();
    commands.add(new NormPoint(0.1, 0.01));
    commands.add(new NormPoint(0.4, 0.51));
    commands.add(new NormPoint(0.8, 0.2));
    
    String id = store.getRegShapeCmdSetManager().write(commands);
    
    assertEquals(commands, store.getRegShapeCmdSetManager().read(id));
  }
  
  
  
  @Test
  public void testWriteRegularShapeTrialCommandSetIdempotence() {
    TrialStore store = newStore(new Object() { });
    ArrayList<NormPoint> commands = new ArrayList<>();
    commands.add(new NormPoint(0.1, 0.01));
    commands.add(new NormPoint(0.49, 0.51));
    commands.add(new NormPoint(0.8, 0.2));
    
    String id = store.getRegShapeCmdSetManager().write(commands);
    
    assertEquals(id, store.getRegShapeCmdSetManager().write(commands));
    assertEquals(commands, store.getRegShapeCmdSetManager().read(id));
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
    String id = store.getRegShapeTransformManager().write(txf);
    assertEquals(txf, store.getRegShapeTransformManager().read(id));
    
    // idempotence check
    assertEquals(id, store.getRegShapeTransformManager().write(txf));
    assertEquals(txf, store.getRegShapeTransformManager().read(id));
  }
  
  
  
  
  private TrialStore newStore(Object innerMethodObject) {
    File dir = getMethodOutputFilepath(innerMethodObject, STORE_PREFIX);
    return new TrialStore(dir);
  }
  
  
  private File newFilepath(Object innerMethodObject) {
    return getMethodOutputFilepath(innerMethodObject, FILE_PREFIX);
  }

}
