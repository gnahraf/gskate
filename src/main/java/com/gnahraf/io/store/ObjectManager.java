/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.io.store;


import java.util.function.Function;
import java.util.stream.Stream;

import com.gnahraf.io.IoRuntimeException;
import com.gnahraf.xcept.NotFoundException;

/**
 * Base abstraction for a simple object store.
 * 
 * @param T the type of data this instance manages. At minimum, this
 *          requires that {@linkplain Object#equals(Object) Object.equals}
 *          be properly overriden with a value-based implementation
 *          (the base implementation is pointer-based). You might want to also override
 *          {@linkplain Object#hashCode() Object.hashCode} (consistent with <tt>equals()</tt>)
 *          but it's not required here for the proper functioning of an implementation.
 */
public abstract class ObjectManager<T> {


  /**
   * Writes the given <tt>object</tt> and returns its ID. This is an idempotent
   * operation: if there's already another object in the store that equals the
   * given <tt>object</tt>, then the existing object's ID is returned.
   * 
   * @throws IoRuntimeException in the event of an I/O error
   */
  public abstract String write(T object) throws IoRuntimeException;
  
  
  /**
   * Reads and returns a previously written object.
   * 
   * @param id  the object's ID as returned on write
   * 
   * @throws NotFoundException  if no known (stored) object with the given <tt>id</tt> exists
   * @throws IoRuntimeException in the event of an I/O error
   */
  public abstract T read(String id) throws NotFoundException, IoRuntimeException;
  
  
  public abstract Stream<String> streamIds();
  

  
  public Stream<T> streamObjects() {
    return streamIds().map(hash -> read(hash));
  }
  
  /**
   * Maps an instance of type <tt>U</tt> to an instance of type <tt>V</tt>.
   * This is a workaround for working with a mutable type (<tt>U</tt>) at the persistence
   * layer (typically because its easier) when we'd prefer to be working with an
   * immutable exposed type (<tt>V</tt>).
   * 
   * @param manager     the base manager
   * @param readMapper  the <tt>{@literal U -> V}</tt> converter
   * @param writeMapper the <tt>{@literal V -> U}</tt> converter
   * 
   * @param U           the type under the hood (at the persistence layer)
   * @param V           the exposed type (for sanity, do override <tt>Object.equals</tt>
   *                    for this type, also)
   */
  public static <U, V> ObjectManager<V> map(
      final ObjectManager<U> manager,
      final Function<U, V> readMapper, final Function<V, U> writeMapper) {
    
    if (manager == null)
      throw new IllegalArgumentException("null manager");
    if (readMapper == null)
      throw new IllegalArgumentException("null readMapper");
    if (writeMapper == null)
      throw new IllegalArgumentException("null writeMapper");
    
    return
        new ObjectManager<V>() {

          @Override
          public String write(V object) {
            U u = writeMapper.apply(object);
            return manager.write(u);
          }

          @Override
          public V read(String id) {
            U u = manager.read(id);
            return readMapper.apply(u);
          }
          
          @Override
          public Stream<String> streamIds() {
            return manager.streamIds();
          }
        };
  }
  
}