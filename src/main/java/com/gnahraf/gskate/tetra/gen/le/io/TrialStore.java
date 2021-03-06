/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.tetra.gen.le.io;


import java.io.File;
import java.util.List;

import javax.xml.bind.JAXB;

import com.gnahraf.gskate.tetra.gen.io.CraftStateSerializer;
import com.gnahraf.gskate.tetra.gen.le.Constraints;
import com.gnahraf.gskate.tetra.gen.le.reg.RegularShapeTrial;
import com.gnahraf.gskate.tetra.model.CraftState;
import com.gnahraf.io.HashedFilepath;
import com.gnahraf.io.store.BinaryObjectManager;
import com.gnahraf.io.store.ListCodec;
import com.gnahraf.io.store.ObjectManager;
import com.gnahraf.io.store.XmlObjectManager;
import com.gnahraf.util.data.NormPoint;
import com.gnahraf.xcept.NotFoundException;


/**
 * A file-based scheme to save system state across simulation checkpoints.
 * Or something to that effect..
 * 
 * <h4>Implementation</h4>
 * Kinda enamored with hash pointers. However, I don't want users of
 * this class to interpret the hashes as anything but an ID string.
 */
public class TrialStore {
  
  
  public final static String CONFIGS = "configs";
  public final static String CONSTRAINTS_PREFIX = "c-";
  
  public final static String STATES = "state";
  public final static String STATE_PREFIX = "s-";
  
  public final static String COMMANDS = "cmd";
  public final static String REG_SHAPE_CMD_PREFIX = "c-rs-";
  
  public final static String TRANSFORMS = "transforms";
  public final static String REG_SHAPE_TRANSFORM_PREFIX = "t-rs-";
  
  /**
   * The minimum trial time in milliseconds.
   */
  private final static int MIN_TRIAL_TIME = 1000;

  
  
  /**
   * Loads an already existing store.
   */
  public static TrialStore load(String rootDir) {
    return load(new File(rootDir));
  }
  
  /**
   * Loads an already existing store.
   */
  public static TrialStore load(File rootDir) {
    if (!rootDir.isDirectory())
      throw new NotFoundException(rootDir.toString());
    return new TrialStore(rootDir);
  }

  
  
  
  /**
   * Creates a new store. <tt>rootDir</tt> must not exist.
   */
  public static TrialStore create(String rootDir) throws IllegalStateException {
    return create(new File(rootDir));
  }
  
  /**
   * Creates a new store. <tt>rootDir</tt> must not exist.
   */
  public static TrialStore create(File rootDir) throws IllegalStateException {
    if (rootDir.exists())
      throw new IllegalStateException("already exists " + rootDir);
    
    return new TrialStore(rootDir);
  }
  
  
  
  
  
  
  
  
  private final File rootDir;
  
  
  private final ObjectManager<Constraints> constraintsManager;
  private final ObjectManager<CraftState> stateManager;
  private final ObjectManager<List<NormPoint>> regShapeCmdSetManager;
  private final ObjectManager<RegularShapeTransform> regShapeTransformManager;
  
  
  public TrialStore(String rootDir) {
    this(new File(rootDir));
  }
  

  /**
   * 
   */
  public TrialStore(File rootDir) {
    
    this.rootDir = rootDir;
    
    this.constraintsManager =
        new XmlObjectManager<Constraints>(
            newHashedFilepath(CONFIGS, CONSTRAINTS_PREFIX, ".xml"),
            new ConstraintsEncoder(),
            Constraints.class);
    
    this.stateManager =
        new BinaryObjectManager<CraftState>(
            newHashedFilepath(STATES, STATE_PREFIX, null),
            new CraftStateSerializer());
    
    
    this.regShapeCmdSetManager =
        new BinaryObjectManager<List<NormPoint>>(
            newHashedFilepath(COMMANDS, REG_SHAPE_CMD_PREFIX, null),
            new ListCodec<NormPoint>(new NormPointCodec()));
    
    this.regShapeTransformManager =
        ObjectManager.map(
            
            new XmlObjectManager<RegularShapeTransform.Builder>(
                newHashedFilepath(TRANSFORMS, REG_SHAPE_TRANSFORM_PREFIX, ".xml"),
                new RegularShapeTransform.Encoder(),
                RegularShapeTransform.Builder.class),
                
            b -> b.build(),
            t -> new RegularShapeTransform.Builder(t));
    
    
  }
  
  
  
  private HashedFilepath newHashedFilepath(String subdir, String prefix, String ext) {
    return new HashedFilepath(new File(rootDir, subdir), prefix, ext);
  }
  
  
  
  
  
  
  
  public ObjectManager<Constraints> getConstraintsManager() {
    return constraintsManager;
  }



  public ObjectManager<CraftState> getStateManager() {
    return stateManager;
  }



  public ObjectManager<List<NormPoint>> getRegShapeCmdSetManager() {
    return regShapeCmdSetManager;
  }



  public ObjectManager<RegularShapeTransform> getRegShapeTransformManager() {
    return regShapeTransformManager;
  }


  
  
  
  
  
  
  

  public String writeRegularShapeTransform(RegularShapeTrial trial) {
    
    if (trial.getTrialTime() < MIN_TRIAL_TIME)
      throw new IllegalArgumentException("insufficient trial time " + trial.getTrialTime());
    
    RegularShapeTransform.Builder transform = new RegularShapeTransform.Builder();
    
    transform.config = constraintsManager.write(trial.getConstraints());
    transform.startState = stateManager.write(trial.getInitState());
    transform.endState = stateManager.write(trial.newSnapshot());
    transform.commandSet = regShapeCmdSetManager.write(trial.getCommandsReceived());
    
    return regShapeTransformManager.write(transform.build());
  }
  
  

  
  
  
  
  
  
  
  
  
  
  public void writeConstraints(Constraints constraints, File xml) {
    if (xml.exists())
      throw new IllegalArgumentException("file already exists " + xml);
    JAXB.marshal(constraints, xml);
  }
  
  
  public Constraints readConstraints(File xml) {
    if (!xml.isFile())
      throw new NotFoundException(xml.toString());
    return JAXB.unmarshal(xml, Constraints.class);
  }
  
  
  

}
