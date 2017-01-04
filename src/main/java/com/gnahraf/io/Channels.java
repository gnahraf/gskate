/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.gnahraf.xcept.NotFoundException;


/**
 *
 */
public class Channels {
  
  private final static int MAX_CONSEC_FAILS = 1024;
  
  private Channels() { }
  
  
  public static void writeRemaining(FileChannel file, ByteBuffer buffer) throws IOException {
    int fails = 0;
    while (fails < MAX_CONSEC_FAILS && buffer.hasRemaining()) {
      int bytes = file.write(buffer);
      if (bytes == 0)
        ++fails;
      else
        fails = 0;
    }
    if (buffer.hasRemaining())
      throw new IoRuntimeException("failed (" + fails + ") to write remaining " + buffer.remaining() + " bytes");
  }
  
  
  
  public static void readFully(File file, ByteBuffer buffer) {
    if (!file.isFile())
      throw new NotFoundException(file.toString());
    
    try (ClosingStack resources = new ClosingStack()) {
      FileChannel stream = new FileInputStream(file).getChannel();
      resources.push(stream);
      
      readFully(stream, buffer);
      
    } catch (IOException iox) {
      throw new IoRuntimeException(iox);
    }
  }
  
  
  public static void readFully(FileChannel file, ByteBuffer buffer) throws IOException {
    if (file.size() - file.position() > buffer.remaining())
      throw new IllegalArgumentException(
          "insufficient remaining bytes in buffer: " + 
          (file.size() - file.position()) + " > " + buffer.remaining());
    
    int fails = 0;
    while (file.position() < file.size()) {
      int bytes = file.read(buffer);

      if (bytes == 0) {
        ++fails;
        if (fails >= MAX_CONSEC_FAILS)
          throw new IoRuntimeException("failed (" + fails + ") to read fullly");
      } else
        fails = 0;
    }
  }

}
