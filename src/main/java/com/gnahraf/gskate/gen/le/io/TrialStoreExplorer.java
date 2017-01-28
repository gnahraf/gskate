/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.io;


import static com.gnahraf.print.Semantic.plural;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.gnahraf.gskate.gen.StateComparators;
import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.gskate.model.Potential;
import com.gnahraf.gskate.model.SphericalBodyPotential;
import com.gnahraf.gskate.model.TetraEdge;
import com.gnahraf.gskate.model.TetraShape;
import com.gnahraf.io.IoRuntimeException;
import com.gnahraf.io.store.ObjectManager;
import com.gnahraf.io.store.XmlObjectManager;
import com.gnahraf.main.Args;
import com.gnahraf.print.TablePrint;
import com.gnahraf.util.data.NormPoint;

/**
 *
 */
public class TrialStoreExplorer {
  
  
  private final TrialStore store;

  
  /**
   * 
   */
  public TrialStoreExplorer(TrialStore store) {
    this.store = store;
    
    if (store == null)
      throw new IllegalArgumentException("null store");
  }
  
  

  
  
  
  
  
  
  public void printStates() {
    printStates(Integer.MAX_VALUE);
  }
  
  
  public void printStates(int maxCount) {
    Comparator<CraftState> order =
        Collections.reverseOrder(
            StateComparators.newCmEnergyComparator(
                new SphericalBodyPotential()));
    
    printStates(order, maxCount);
  }
  
  
  public void printStates(Comparator<CraftState> order, int maxCount) {
    if (maxCount < 1)
      return;
    
    List<CraftState> states = new ArrayList<>();
    store.getStateManager()
      .streamObjects()
      .forEach(states::add);
    
    if (order != null)
      Collections.sort(states, order);
    
    maxCount = Math.min(maxCount, states.size());
    for (int i = 0; i < maxCount; ++i) {
      CraftState state = states.get(i);
      System.out.println(i + 1 + ".");
      printState(state);
    }
  }
  
  
  public void printState(String hashPrefix) {
    ObjectManager<CraftState> stateMgr = store.getStateManager();
    
    if (stateMgr.containsId(hashPrefix))
      printState(stateMgr.read(hashPrefix));
    
    else
      stateMgr.streamIds().filter(hash -> hash.startsWith(hashPrefix))
        .forEach(hash -> printState(stateMgr.read(hash)));
  }
  
  
  public void printState(CraftState state) {

    Potential earth = new SphericalBodyPotential();
    
    TablePrint tablePrint = new TablePrint(17, 17, 22);
    tablePrint.setIndentation(4);
    tablePrint.printHorizontalTableEdge('=');
    tablePrint.println(store.getStateManager().getId(state));
    tablePrint.printHorizontalTableEdge('-');
    tablePrint.printRow("CM energy (J):", null, state.getCmEnergy(earth));
    tablePrint.printRow("PE (J):", null, state.getPe(earth));
    tablePrint.printRow("Rotational Energy (J):", null, state.getRotationalEnergy());
    tablePrint.printRow("Time (s):", null, state.getTime() / 1000);
    tablePrint.printHorizontalTableEdge('-');
    
    tablePrint.printRow("Edge", "Length (m)");
    tablePrint.printHorizontalTableEdge('-');
    
    TetraShape shape = state.getCraft().getShape();
    
    for (TetraEdge edge : TetraEdge.values())
      tablePrint.printRow(edge, shape.length(edge));
    
    tablePrint.printHorizontalTableEdge('=');
    tablePrint.println();
  }
  
  
  
  
  
  
  public void printConfigs() {
    printXmls(store.getConstraintsManager());
  }
  
  
  
  public void printConfig(String hashPrefix) {
    printXmlUsingPrefix(hashPrefix, store.getConstraintsManager());
  }
  
  
  public void printTransforms() {
    printXmls(store.getRegShapeTransformManager());
  }
  
  
  public void printTransform(String hashPrefix) {
    printXmlUsingPrefix(hashPrefix, store.getRegShapeTransformManager());
  }
  
  
  public void printCommandSets() {
    long count =
        store.getRegShapeCmdSetManager().streamIds()
          .peek(hash -> printCommandSetImpl(hash))
          .count();

    System.out.println(count + plural(" object", count) + " listed");
  }
  
  public void printCommandSet(String prefix) {
    ObjectManager<List<NormPoint>> objMgr = store.getRegShapeCmdSetManager();
    
    long count = 1;
    
    if (objMgr.containsId(prefix))
      printCommandSetImpl(prefix);
    else
      count = objMgr.streamIds().filter(hash -> hash.startsWith(prefix))
          .peek(this::printCommandSetImpl).count();
      
    System.out.println(count + plural(" object", count) + " selected");
  }
  
  private void printCommandSetImpl(String hash) {
    List<NormPoint> shapeCmds = store.getRegShapeCmdSetManager().read(hash);

    TablePrint tablePrint = new TablePrint(30, 30);
    tablePrint.setIndentation(4);
    
    tablePrint.println(hash);
    tablePrint.printHorizontalTableEdge('_');
    tablePrint.printRow("Time (period normalized)", "Edge Length (range normalized)");
    tablePrint.printHorizontalTableEdge('-');
    for (NormPoint cmd : shapeCmds)
      tablePrint.printRow(cmd.x(), cmd.y());
    tablePrint.println();
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private void printXmlUsingPrefix(String hashPrefix, ObjectManager<?> objMgr) {
    if (objMgr.containsId(hashPrefix))
      printXml(hashPrefix, objMgr);
    
    else
      printXmls(objMgr, hash -> hash.startsWith(hashPrefix));
  }
  
  
  
  private void printXmls(ObjectManager<?> objMgr) {
    printXmls(objMgr, null);
  }
  
  
  private void printXmls(ObjectManager<?> objMgr, Predicate<? super String> condition) {
    Stream<String> hashes = objMgr.streamIds();
    if (condition != null)
      hashes = hashes.filter(condition);
    
    long count = hashes.peek(hash -> printXml(hash, objMgr)).count();
    
    System.out.println(count + plural(" object", count) + (condition == null ? " listed" : " selected"));
    System.out.println();
  }
  
  
  // An exercise below.. but fuck it.
  
  // (I'll figure out closures in java later.
  // Even if the language doesn't support closures,
  // they should be easy to synthesize.)
  
//  private void printThings(ObjectManager<?> objMgr, Predicate<? super String> condition, Consumer<? super String> printOp) {
//    Stream<String> hashes = objMgr.streamIds();
//    if (condition != null)
//      hashes = hashes.filter(condition);
//    
//    long count = hashes.peek(printOp).count();
//    
//    System.out.println(count + plural(" object", count) + condition == null ? "" : " selected");
//    System.out.println();
//  }
  
  private void printXml(String hash, ObjectManager<?> objMgr) {
    try (Reader r = objMgr.getReader(hash)) {
      System.out.println(hash);
      System.out.println();
      for (int c = r.read(); c != -1; c = r.read())
        System.out.print((char) c);
    } catch (IOException iox) {
      throw new IoRuntimeException(iox);
    }
    System.out.println();
  }
  
  
  
  
  
  
  public final static String STORE = "store";
  public final static String CMD = "cmd";
  public final static String HASH = "hash";
  
  public final static String LIST_STATES_CMD = "list_states";
  public final static String PRINT_STATE_CMD = "print_state";
  public final static String LIST_CONFIFS_CMD = "list_configs";
  public final static String PRINT_CONFIG_CMD = "print_config";
  public final static String LIST_TRANSFORMS_CMD = "list_transforms";
  public final static String PRINT_TRANSFORM_CMD = "print_transform";
  public final static String LIST_COMMANDSETS_CMD = "list_reg_cmds";
  public final static String PRINT_COMMANDSET_CMD = "print_reg_cmds";
  
  
  public static void main(String[] args) {
    String cmd = Args.getValue(args, CMD);
    TrialStoreExplorer explorer =
        new TrialStoreExplorer(
            TrialStore.load(Args.getValue(args, STORE)));
    
    System.out.println();
    
    if (LIST_STATES_CMD.equals(cmd))
      explorer.printStates();
    
    else if (PRINT_STATE_CMD.equals(cmd))
      explorer.printState(Args.getValue(args, HASH));
    
    else if (LIST_CONFIFS_CMD.equals(cmd))
      explorer.printConfigs();
    
    else if (PRINT_CONFIG_CMD.equals(cmd))
      explorer.printConfig(Args.getValue(args, HASH));
    
    else if (LIST_TRANSFORMS_CMD.equals(cmd))
      explorer.printTransforms();
    
    else if (PRINT_TRANSFORM_CMD.equals(cmd))
      explorer.printTransform(Args.getValue(args, HASH));
    
    else if (LIST_COMMANDSETS_CMD.equals(cmd))
      explorer.printCommandSets();
    
    else if (PRINT_COMMANDSET_CMD.equals(cmd))
      explorer.printCommandSet(Args.getValue(args, HASH));
    
    else {
      System.err.println(
          "Unknown arg " + CMD + '=' + cmd + " - Valid combinations are " + CMD + "=" +
              Arrays.asList(
                  LIST_STATES_CMD,
                  PRINT_STATE_CMD,
                  LIST_CONFIFS_CMD,
                  PRINT_CONFIG_CMD,
                  LIST_TRANSFORMS_CMD,
                  PRINT_TRANSFORM_CMD,
                  LIST_COMMANDSETS_CMD,
                  PRINT_COMMANDSET_CMD));
      System.exit(1);
    }
  }

}









