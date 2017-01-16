/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.JAXB;

import com.gnahraf.gskate.gen.io.CraftStateSerializer;
import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.gen.le.reg.RegularShapeTrial;
import com.gnahraf.gskate.model.CraftState;
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
  
  
  
  
  
  
  
  
  
  
  
  private final File rootDir;
  
  
  private final ObjectManager<Constraints> constraintsManager;
  private final ObjectManager<CraftState> stateManager;
  private final ObjectManager<List<NormPoint>> regShapeCmdSetManager;
  private final ObjectManager<RegularShapeTransform> regShapeTransformManager;
  
  

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
  
  
  
  public String writeRegularShapeTransform(RegularShapeTrial trial) {
    if (trial.getTrialTime() < MIN_TRIAL_TIME)
      throw new IllegalArgumentException("insufficient trial time " + trial.getTrialTime());
    RegularShapeTransform.Builder transform = new RegularShapeTransform.Builder();
    transform.config = writeConstraints(trial.getConstraints());
    transform.startState = writeCraftState(trial.getInitState());
    transform.endState = writeCraftState(trial.newSnapshot());
    transform.commandSet = writeRegularShapeTrialCommandSet(trial.getCommandsReceived());
    
    return writeRegularShapeTransformData(transform.build());
  }
  
  
  
  String writeRegularShapeTransformData(RegularShapeTransform transform) {
    return regShapeTransformManager.write(transform);
  }
  
  
  public RegularShapeTransform readRegularShapeTransform(String id) {
    return regShapeTransformManager.read(id);
  }
  
  
  
  
  
  public Stream<String> streamRegularShapeTransformIds() {
    return regShapeTransformManager.streamIds();
  }
  
  
  
  
  
  public String writeCraftState(CraftState state) {
    return stateManager.write(state);
  }
  
  
  public CraftState readCraftState(String id) {
    return stateManager.read(id);
  }
  
  
  
  
  
  
  
  
  
  
  public String writeRegularShapeTrialCommandSet(List<NormPoint> commands) {
    return regShapeCmdSetManager.write(commands);
  }
  
  
  public List<NormPoint> readRegularShapeTrialCommandSet(String id) {
    return regShapeCmdSetManager.read(id);
  }
  
  
  
  
  
  
  
  
  
  
  public String writeConstraints(Constraints constraints) {
    return constraintsManager.write(constraints);
  }
  
  
  public Constraints readConstraints(String id) {
    return constraintsManager.read(id);
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
