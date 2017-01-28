/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.reg;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gnahraf.gskate.gen.le.Constraints;
import com.gnahraf.gskate.gen.le.SimpleEquiTrial;
import com.gnahraf.gskate.gen.le.io.TrialStore;
import com.gnahraf.gskate.model.CraftState;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraEdge;
import com.gnahraf.gskate.model.TetraShape;
import com.gnahraf.main.Args;
import com.gnahraf.print.TablePrint;
import com.gnahraf.util.data.NormPoint;
import com.gnahraf.util.tree.DecisionTreeProcessor;
import com.gnahraf.util.tree.RegionProgression;
import com.gnahraf.util.tree.TreeNode;
import com.gnahraf.xcept.NotFoundException;

/**
 * An ensemble of {@linkplain RegularShapeTrial}s characterized with 3
 * decision points along the first orbit. (Yes, just the first orbit. I'll
 * generalize later.) These 3 points, then, divide the simulation of each trial into 4 regions:
 * <ol>
 * <li>(0,1) Steady expansion (linear in time)</li>
 * <li>(1,2) Hold shape steady</li>
 * <li>(2,3) Contract to steady-state length</li>
 * <li>(3,4) Hold shape steady</li>
 * </ol>
 * Intuitively, the reasoning behind this strategy is that we (0) expand the volume of the craft when
 * tidal forces are at a minimum (along the orbit), (1) hold the volume steady while descending into the
 * well (tidal forces increase), (2) work against the tidal forces when maximized by contracting the volume,
 * and (3) hold the craft's shape steady until the completion of the orbit.
 * <p/>
 * Now the last step might seem a waste of time, since we barely do any work against the potential well.
 * However, I can't be very sure. The shape of the craft is maintained by adaptive control of tether forces,
 * which in the event, may end up gaining or losing work. My intuition here is that this region of the
 * orbit may be used to give up rotational spin of the craft back to its environment. (We don't want too
 * much rotational energy since our tether forces are bounded.)
 * <p/>
 * In order to further
 * limit the computational space, we divide each orbital period
 * into [time] regions.
 * <p/>
 * <pre>
 * 
      tether length
       |       
       |(1)     (2)
       |  * * * *
       | *        * (3)
       |*           * * * * * *
       |          
       |_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ time
      0                        1
      
 * </pre>
 * <p/>
 * The 3 points are of the form
   <p/>
   <pre><tt>
     (t1, max), (t2, max), (t3, min),
                               with    0 < t1 < t2 < t3 <= 1,
                               and     0 < min < max <= 1
   </tt></pre>
   <p/>
     min and max are fixed for the ensemble; we vary t1, t2, and t3
   <p/>
     Let's say there are n teeth. Since they're arranged in a circle,
     there are also n regions.
   <p/>
     For the purpose of counting the # of combinations, let us renormalize
     the time axis so that
   <p/> 
   <pre><tt>
                  0 < t1 < t2 < t3 <= n  (n >= 3)
   </tt></pre>
   <p/>
     with t1, t2, and t3 taking on only integral values.
   <p/>
     This yields a count N s.t.
   <p/> 
   <pre><tt>
         2N = sigma[i: 1 -> n-2]{ i(i+1) }
   </tt></pre>
   <p/>
     So the problem space grows with the square of the # of regions (derivative)
     and is O(n^3)
 *
 * <h4>After-thoughts</h4>
 * <p/>
 * <strike>
 * On increasing the number of the regions, we discover entire classes of
 * ineligible profiles that we need not try. This, in turn, is a general problem
 * that I'll doubtless revisit under other conditions, as I vary the model. Turning
 * my attention in this direction. -- 26 November 2016
 * </strike><br/><strong>Done.</strong>
 */
public class RegularShapeTrialEnsemble {
  
  private final static double SCALED_MAX = 0.98;
  

  
  private final Constraints constraints;
  private final double scaledMin;
  private final int regions;
  
  private final List<RegularShapeTrial> trials;
  
  private final TrialProcessor processor;
  
  

  /**
   * 
   */
  public RegularShapeTrialEnsemble(Constraints constraints, int regions, int minRegionGap, CraftState initState) {
    this.constraints = constraints;
    this.scaledMin = constraints.steadyStateTetherLength / constraints.maxTetherLength;
    this.regions = regions;
    if (regions < 3)
      throw new IllegalArgumentException("regions " + regions);
    this.trials = new ArrayList<RegularShapeTrial>(1024);
    this.processor = new TrialProcessor(minRegionGap, initState);
  }
  
  
  public CraftState getInitState() {
    return processor.initState;
  }
  
  public void execute() {
    processor.processTree();
  }
  

  
  public List<RegularShapeTrial> getTrials() {
    return trials;
  }
  
  
  
  

  private void print(Object msg) {
    System.out.println(msg);
  }
  
  
  
  private static void printSummary(RegularShapeTrial trial) {

    System.out.println();
    System.out.println("\tCM  energy gain: " + FORMAT.format(trial.getCmEnergyGain()) + " J");
    System.out.println("\tRot energy gain: " + FORMAT.format(trial.getRotationalEnergyGain()) + " J");
    System.out.println("\tOrbital radius gain: " + FORMAT.format(trial.getRadiusGain()) + " m");
    System.out.println();
    

    Tetra craft = trial.getSystem().getCraft();
    TetraShape shape = craft.getShape();
    TablePrint tablePrint = new TablePrint(15, 15, 17);
    tablePrint.setIndentation(8);
    tablePrint.printRow("Edge", "Length (m)", "Force (N)");
    tablePrint.printRow(null, null, "(+/- : push/pull)");
    tablePrint.printHorizontalTableEdge('-');
    for (TetraEdge edge : TetraEdge.values())
      tablePrint.printRow(edge, shape.length(edge), craft.getTether(edge));
    System.out.println();
  }
  
  
  
  
  private final static DecimalFormat FORMAT = new DecimalFormat("#,###.##");
  
  
  /**
   * We use this construct to systematically cover the decision space. We also
   * use this to save computational work at each decision branch.
   */
  private class TrialProcessor extends DecisionTreeProcessor<RegionProgression> {
    
    
    
    private final ArrayDeque<RegularShapeTrial> trialProgress = new ArrayDeque<>();
    
    private final double normalizedRegionDuration = 1.0 / regions;
    
    private final int minRegionGap;
    
    private final CraftState initState;
    
    
    

    public TrialProcessor(int minRegionGap, CraftState initState) {
      super(new RegionProgression(regions, 3));
      this.minRegionGap = minRegionGap;
      {
        RegularShapeTrial protoTrial = new RegularShapeTrial(constraints, initState);
        trialProgress.push(protoTrial);
        this.initState = protoTrial.getInitState();
      }
    }
    
    

    @Override
    protected boolean processNode(RegionProgression node) {
      int level = node.level();
      if (level < 1 || level > trialProgress.size())
        throw new RuntimeException("Assertion failed. level " + level + "; node " + node + "; stack " + trialProgress);
      
      while (level < trialProgress.size())
        trialProgress.pop();
      
      RegularShapeTrial trial = new RegularShapeTrial(trialProgress.peek());
      trialProgress.push(trial);
      
      NormPoint command;
      {
        double normalizedTime = normalizedRegionDuration * (1 + node.region());
        double normalizedEdgeLength;
        
        switch (level) {
        case 1:
        case 2:
          normalizedEdgeLength = SCALED_MAX;
          break;
        case 3:
          normalizedEdgeLength = scaledMin;
          break;
        default:
          throw new RuntimeException("Assertion failed. level " + level);
        }
        command = new NormPoint(normalizedTime, normalizedEdgeLength);
      }
      
      if (minRegionGap > node.region() - node.parent().region() || (3 - level) * minRegionGap > regions - node.region() - 1) {
        return false;
      }
      {
        ArrayList<Integer> regionStack = new ArrayList<>();
        for (RegionProgression n = node; !n.isRoot(); n = n.parent())
          regionStack.add(n.region());
        print("Executing level " + level + ", region " + node.region() + " - " + regionStack);
      }
      
      if (! trial.runToOrbitalPoint(command) ) {
        printBackout(node, trial);
        return false;
      }
      
      if (node.isLeaf()) {
        print("Maneuver completed.");
        
        if (trial.getCmEnergyGain() < 0) {
          print("..but CM energy gain is negative (" + FORMAT.format(trial.getCmEnergyGain()) + " J) so not pursuing..");
          printBackout(node, trial);
          return false;
        }
        
        if (tethersMaxedOut(trial)) {
          print("..but the tethers are at the break point, so not pursuing..");
          printBackout(node, trial);
          return false;
        }

        print("Holding shape until completion of orbit");
        trial.newSnapshot();
        if (! trial.runToCompleteOrbit() ) {
          printBackout(node, trial);
          return false;
        }
        
        trials.add(trial);
        print("Trial completed.");
        printSummary(trial);
      }
      
      return true;
    }
    
    
    private boolean tethersMaxedOut(RegularShapeTrial trial) {
      Tetra craft = trial.getSystem().getCraft();
      int count = 0;
      for (int t = 0; t < 6; ++t) {
        double tether = craft.getTetherByIndex(t);
        if (-tether >= constraints.maxTensileForce - MAX_TENSILE_WATERMARK)
          ++count;
      }
      return count > 2;
    }
    
    private final static double MAX_TENSILE_WATERMARK = 5;
    
    
    private void printBackout(RegionProgression node, RegularShapeTrial trial) {
      print(trial.getException());
      print(".. at normalized orbit time " + (((float) trial.getTrialTime()) / trial.getPeriodMillis()));
      print("Backing out of level " + node.level() + ", region " + node.region() + "\n");
    }
    
  }
  
  

  
  
  
  
  
  
  
  
  
  public final static String STORE = "store";
  public final static String REGIONS = "regions";
  public final static String INIT_STATE = "init_state";
  public final static String MIN_REGION_GAP = "min_region_gap";
  public final static String CONFIG = "config";
  public final static String PLAY = "play";
  
  public final static String DEFAULT_STORE_PATH =
      "data" + File.separator + "store";
  public final static String DEFAULT_CONFIG_PATH =
      "data" + File.separator + "configs" + File.separator + "constraints.xml";

  
  public static void main(String[] args) {
    
    if (Args.help(args)) {
      String[] options = { STORE, REGIONS, INIT_STATE, MIN_REGION_GAP, CONFIG, PLAY };
      System.out.println("Reminder: options are");
      for (String option : options)
        System.out.println("   " + option + "=..");
      return;
    }
    
    int regions = Args.getIntValue(args, REGIONS, 4);
    if (regions < 3)
      throw new IllegalArgumentException(REGIONS + "=" + regions + " < 3");

    int minRegionGap = Args.getIntValue(args, MIN_REGION_GAP, 1);
    
    
    
    TrialStore store;
    {
      String path = Args.getValue(args, STORE, DEFAULT_STORE_PATH);
      
      File dir = new File(path);
      
      // some chattiness..
      if (!dir.exists()) {
        System.out.println("Directory " + dir + " does not exist.");
        System.out.println("Creating TrialStore there for you.");
      } else
        System.out.println("Loading TrialStore from " + dir);
      
      store = new TrialStore(dir);
    }
    
    
    Constraints constraints;
    {
      String path = Args.getValue(args, CONFIG, DEFAULT_CONFIG_PATH);
      
      File file = new File(path);
      if (!file.exists()) {
        System.out.println("File " + file + " does not exist.");
        System.out.println("Creating default constraints there for you.");
        System.out.println("Review, edit and then relaunch.");
        
        File dir = file.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs() && !dir.isDirectory()) {
          System.out.println("Bailing on " + file);
          System.exit(1);
        }

        
        constraints = new Constraints();
        constraints.maxTetherLength = 20000;
        constraints.steadyStateTetherLength = 250;
        constraints.initTetherLength = 250;
        store.writeConstraints(constraints, file);
        
        System.exit(2);
      } else {
        System.out.println("Loading constraints from " + file);
        constraints = store.readConstraints(file);
      }
    }
    
    
    CraftState initState;
    {
      String hashPrefix = Args.getValue(args, INIT_STATE, null);
      if (hashPrefix == null)
        initState = null;
      else
        initState = store.getStateManager().readUsingPrefix(hashPrefix);
    }
    
    {
      String commandSetPlay = Args.getValue(args, PLAY, null);
      if (commandSetPlay != null) {

        List<NormPoint> commands = store.getRegShapeCmdSetManager().readUsingPrefix(commandSetPlay);
        
        RegularShapeTrial trial = new RegularShapeTrial(constraints, initState);
        for (NormPoint orbitalPoint : commands) {
          System.out.println("Executing " + orbitalPoint);
          trial.runToOrbitalPoint(orbitalPoint);
        }
        System.out.println("Running to completion of orbit..");
        trial.runToCompleteOrbit();
        printSummary(trial);
        String entry = store.writeRegularShapeTransform(trial);
        System.out.println("\tDB entry " + entry);
        return;
      }
    }
    
    RegularShapeTrialEnsemble instance =
        new RegularShapeTrialEnsemble(constraints, regions, minRegionGap, initState);
    
    System.out.println(
        "Executing over " + regions + " regions with at least " + minRegionGap +
        " regions between decisions");
    System.out.println("Init state " + store.getStateManager().getId(instance.getInitState()));
    System.out.println();
    
    instance.execute();
    
    List<RegularShapeTrial> trials = instance.getTrials();
    
    Collections.sort(trials, new CmEnergyComparator());
    
    
    System.out.println(trials.size() + " trials passed.");
    System.out.println(           "--------------------");
    System.out.println();
    int i = 0;
    for (RegularShapeTrial trial : trials) {
      System.out.println();
      System.out.println(++i + ".\t" + trial.getCommandsReceived());
      printSummary(trial);
    }
    
    int countDown = Math.min(trials.size(), 5 + trials.size() / 5);


    System.out.println();
    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    System.out.println("#");
    System.out.println("#\tTop " + countDown + " count down");
    System.out.println("#");
    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    
    i = 1;
    while (countDown-- > 0) {
      RegularShapeTrial trial = trials.get(trials.size() - i);
      System.out.println();
      System.out.println(i + ".\t" + trial.getCommandsReceived());
      printSummary(trial);
      if (store != null) {
        String entry = store.writeRegularShapeTransform(trial);
        System.out.println("\tDB entry " + entry);
      }
      ++i;
    }
  }
  
  
  
  
  
  
  

  
  
  
  public static class CmEnergyComparator implements Comparator<RegularShapeTrial> {
    
    private final double SWAMP_FACTOR = 32;
    
    @Override
    public int compare(RegularShapeTrial a, RegularShapeTrial b) {
      
      double aCmE = a.getCmEnergyGain();
      double bCmE = b.getCmEnergyGain();
      
      double cmDiff = aCmE - bCmE;
      
      if (Math.abs(cmDiff) < 1 || Math.abs(aCmE / cmDiff) > SWAMP_FACTOR && Math.abs(bCmE / cmDiff) > SWAMP_FACTOR) {
        double aRotE = a.getRotationalEnergyGain();
        double bRotE = b.getRotationalEnergyGain();
        
        double rotDiff = a.getRotationalEnergy() - b.getRotationalEnergy();
        if (Math.abs(rotDiff) > 2) {
          return rotDiff > 0 ? -1 : 1;
        }
      }
      
      if (aCmE < bCmE)
        return -1;
      else if (bCmE > aCmE)
        return 1;
      else
        return 0;
    }
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
}
