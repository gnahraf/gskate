/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXB;

import com.gnahraf.gskate.gen.io.CraftStateSerializer;
import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.gen.le.reg.RegularShapeTrial;
import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.io.HashedFilepath;
import com.gnahraf.io.IoRuntimeException;
import com.gnahraf.io.Channels;
import com.gnahraf.io.PathnameGenerator;
import com.gnahraf.util.data.NormPoint;
import com.gnahraf.xcept.NotFoundException;


/**
 * A file-based scheme to save system state across simulation checkpoints.
 * Or something to that effect..
 * <h4>Implementation</h4>
 * Kinda enamored with hash pointers.
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

  
  private final static CraftStateSerializer STATE_CODEC = new CraftStateSerializer();
  
  /**
   * The minimum trial time in milliseconds.
   */
  private final static int MIN_TRIAL_TIME = 1000;
  
  
  
  
  private final HashedFilepath configs;
  private final HashedFilepath states;
  private final HashedFilepath regShapeCommands;
  private final HashedFilepath regShapeTransforms;
  
  

  /**
   * 
   */
  public TrialStore(File rootDir) {
    
    configs =
        new HashedFilepath(
            new File(rootDir, CONFIGS), CONSTRAINTS_PREFIX, ".xml");
    states =
        new HashedFilepath(
            new File(rootDir, STATES), STATE_PREFIX, null);
    regShapeCommands =
        new HashedFilepath(
            new File(rootDir, COMMANDS),  REG_SHAPE_CMD_PREFIX, null);
    regShapeTransforms =
        new HashedFilepath(
            new File(rootDir, TRANSFORMS),  REG_SHAPE_TRANSFORM_PREFIX, ".xml");
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
    
    String hash = signature(transform.getSignableContent());
    
    File file = regShapeTransforms.toFilepath(hash);
    if (!file.exists())
      JAXB.marshal(new RegularShapeTransform.Builder(transform), file);
    
    else {
      RegularShapeTransform.Builder t;
      try {
        t = JAXB.unmarshal(file, RegularShapeTransform.Builder.class);
      } catch (Exception x) {
        throw new CorruptionException(file.toString(), x);
      }
      if (!transform.equals(t.build()))
        throw new CorruptionException(file.toString());
    }
    
    return hash;
  }
  
  
  public RegularShapeTransform readRegularShapeTransform(String id) {
    File file = regShapeTransforms.toFilepath(id);
    if (!file.exists())
      throw new NotFoundException(id);
    try {
      RegularShapeTransform.Builder data = JAXB.unmarshal(file, RegularShapeTransform.Builder.class);
      return data.build();
    } catch (Exception x) {
      throw new CorruptionException(file.toString(), x);
    }
  }
  
  
  
  
  
  public String writeCraftState(CraftState state) {
    
    ByteBuffer buffer =
        ByteBuffer.allocate(CraftStateSerializer.BUFFER_ALLOC_SIZE);
    
    STATE_CODEC.write(state, buffer);
    buffer.flip();
    
    String hash = signature(buffer);
    File file = states.toFilepath(hash);
    
    if (!file.exists())
      Channels.writeToNewFile(file, buffer);
    
    else {
      // the file exists: make sure it's not corrupt

      boolean fail;
      buffer.clear();
      try {
        Channels.readFully(file, buffer);
        buffer.flip();
      
        fail = !STATE_CODEC.read(buffer).equals(state);
      } catch (IllegalArgumentException iax) {
        fail = true;
      } catch (BufferUnderflowException bux) {
        fail = true;
      }
      
      if (fail)
        throw new CorruptionException("state " + hash);
    }
      
    return hash;
  }
  
  
  
  public CraftState readCraftState(String id) {
    File file = states.toFilepath(id);
    if (!file.exists())
      throw new NotFoundException("state " + id);

    ByteBuffer buffer =
        ByteBuffer.allocate(CraftStateSerializer.BUFFER_ALLOC_SIZE);
    
    Channels.readFully(file, buffer);
    buffer.flip();
    
    return STATE_CODEC.read(buffer);
  }
  
  
  
  public String writeRegularShapeTrialCommandSet(List<NormPoint> commands) {
    int count = commands.size();
    ByteBuffer buffer = ByteBuffer.allocate(count * 16 + 8);
    buffer.putInt(count);
    for (int i = 0; i < count; ++i) {
      NormPoint cmd = commands.get(i);
      buffer.putDouble(cmd.x());
      buffer.putDouble(cmd.y());
    }
    buffer.flip();
    
    String hash = signature(buffer);
    File file = regShapeCommands.toFilepath(hash);
    
    if (!file.exists())
      Channels.writeToNewFile(file, buffer);
    
    else {
      // the file exists: make sure it's not corrupt
      
      buffer.clear();
      
      boolean fail;
      try {
        
        Channels.readFully(file, buffer);
        buffer.flip();
        boolean ok = count == buffer.getInt();

        for (int i = 0; ok && i < count; ++i) {
          NormPoint cmd = commands.get(i);
          ok &= cmd.x() == buffer.getDouble();
          ok &= cmd.y() == buffer.getDouble();
        }
        
        fail = !ok;
      } catch (IllegalArgumentException iax) {
        fail = true;
      } catch (BufferUnderflowException bux) {
        fail = true;
      }
      
      if (fail)
        throw new CorruptionException(file.toString());
    }
      
    return hash;
  }
  
  
  public List<NormPoint> readRegularShapeTrialCommandSet(String id) {
    File file = regShapeCommands.toFilepath(id);
    if (!file.exists())
      throw new NotFoundException("command-set " + id);
    
    ByteBuffer buffer = ByteBuffer.allocate((int) Math.min(1024*1024, file.length()));
    
    Throwable err = null;
    try {
      Channels.readFully(file, buffer);
      buffer.flip();
      
      int count = buffer.getInt();
      ArrayList<NormPoint> commands = new ArrayList<NormPoint>(count);
      
      for (int i = 0; i < count; ++i) {
        double x = buffer.getDouble();
        double y = buffer.getDouble();
        commands.add(new NormPoint(x, y));
      }
      
      if (buffer.hasRemaining())
        throw new CorruptionException("file size larger than expected for command set " + id);
      
      return commands;
    } catch (IllegalArgumentException iax) {
      err = iax;
    } catch (BufferUnderflowException bux) {
      err = bux;
    }
    
    throw new CorruptionException("command set " + id, err);
  }
  
  
  
  
  
  
  
  public String writeConstraints(Constraints constraints) {
    String sig = computeSignature(constraints);
    File xmlFile = configs.toFilepath(sig);
    if (xmlFile.exists()) {
      if (!readConstraints(xmlFile).equals(constraints))
        throw new CorruptionException("constraints " + sig);
    } else
      writeConstraints(constraints, xmlFile);
    return sig;
  }
  
  
  public Constraints readConstraints(String id) {
    return readConstraints(configs.toFilepath(id));
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
  
  
  
  
  
  
  
  public String computeSignature(Constraints constraints) {
    ByteBuffer buffer = ByteBuffer.allocate(128);
    buffer.putDouble(constraints.initTetherLength);
    buffer.putDouble(constraints.steadyStateTetherLength);
    buffer.putDouble(constraints.maxTetherLength);
    buffer.putDouble(constraints.minTetherLength);
    buffer.putDouble(constraints.minKmsAboveGround);
    buffer.putDouble(constraints.initKmsAboveGround);
    buffer.putDouble(constraints.maxTensileForce);
    buffer.putDouble(constraints.maxCompressiveForce);
    buffer.putDouble(constraints.timeFineness);
    buffer.flip();
    return signature(buffer);
  }
  
  
  
  
  
  
  
  private MessageDigest newDigest() {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException nsax) {
      throw new RuntimeException(nsax);
    }
  }
  
  
  /**
   * Computes and returns the signature of the given <tt>buffer</tt>.
   * Excepting its mark, the state of the <tt>buffer</tt> is not modified.
   */
  private String signature(ByteBuffer buffer) {
    MessageDigest digest = newDigest();
    buffer.mark();
    digest.update(buffer);
    buffer.reset();
    return DatatypeConverter.printHexBinary(digest.digest());
  }
  

}
