/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXB;

import com.gnahraf.gskate.gen.io.CraftState;
import com.gnahraf.gskate.gen.io.CraftStateSerializer;
import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.io.HashedFilepath;
import com.gnahraf.io.IoRuntimeException;
import com.gnahraf.io.Channels;
import com.gnahraf.io.PathnameGenerator;
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

  
  private final static CraftStateSerializer STATE_CODEC = new CraftStateSerializer();
  
  
  
  
  
  private final File rootDir;
  private final HashedFilepath configs;
  private final HashedFilepath states;
  
  
//  private final PathnameGenerator configs;

  /**
   * 
   */
  public TrialStore(File rootDir) {
    this.rootDir = rootDir;
    ensureDirectory(rootDir);
    configs = new HashedFilepath(new File(rootDir, CONFIGS), CONSTRAINTS_PREFIX, ".xml");
    states = new HashedFilepath(new File(rootDir, STATES), STATE_PREFIX, null);
  }
  
  
  
  
  private void ensureDirectory(File dir) {
    if (!dir.isDirectory()) {
      if (dir.exists())
        throw new IllegalArgumentException("not a directory: " + dir);
      if (!dir.mkdirs() && !dir.isDirectory())
        throw new IllegalArgumentException("failed to create directory " + dir);
    }
  }
  
  
  
  public String writeCraftState(CraftState state) {
    
    ByteBuffer buffer =
        ByteBuffer.allocate(CraftStateSerializer.BUFFER_ALLOC_SIZE);
    
    STATE_CODEC.write(state, buffer);
    buffer.flip();
    String hash = signature(buffer);
    File file = states.toFilepath(hash);
    
    if (file.exists()) {
      buffer.clear();
      Channels.readFully(file, buffer);
      buffer.flip();
      if (!STATE_CODEC.read(buffer).equals(state))
        throw new CorruptionException("state " + hash);
    } else {
      try {
        FileChannel stream = new FileOutputStream(file).getChannel();
        Channels.writeRemaining(stream, buffer);
        stream.close();
      } catch (IOException iox) {
        throw new IoRuntimeException(iox);
      }
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
    buffer.putDouble(constraints.initTetherValue);
    buffer.putDouble(constraints.timeFineness);
    buffer.flip();
    return signature(buffer);
  }
  
  
  
  
  
//  public String writeState(
  
  
  
  
  
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
