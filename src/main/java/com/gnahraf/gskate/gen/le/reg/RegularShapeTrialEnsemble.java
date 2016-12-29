/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le.reg;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.gen.le.SimpleEquiTrial;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.util.data.NormPoint;
import com.gnahraf.util.tree.DecisionTreeProcessor;
import com.gnahraf.util.tree.RegionProgression;
import com.gnahraf.util.tree.TreeNode;

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
                  0 < 0 < t1 < t2 < t3 <= n  (n >= 3)
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
 * On increasing the number of the regions, we discover entire classes of
 * ineligible profiles that we need not try. This, in turn, is a general problem
 * that I'll doubtless revisit under other conditions, as I vary the model. Turning
 * my attention in this direction. -- 26 November 2016
 */
public class RegularShapeTrialEnsemble {
  
  private final static double SCALED_MAX = 0.98;
  

  
  private final LonelyEarth.Constraints constraints;
  private final double scaledMin;
  private final int regions;
  
  private final List<RegularShapeTrial> trials;
  
  private final TrialProcessor processor;
  
  

  /**
   * 
   */
  public RegularShapeTrialEnsemble(LonelyEarth.Constraints constraints, int regions) {
    this.constraints = constraints;
    this.scaledMin = constraints.steadyStateTetherLength / constraints.maxTetherLength;
    this.regions = regions;
    if (regions < 3)
      throw new IllegalArgumentException("regions " + regions);
    this.trials = new ArrayList<RegularShapeTrial>(1024);
    this.processor = new TrialProcessor();
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
  
  
  
  private void printSummary(RegularShapeTrial trial) {

    System.out.println();
    System.out.println("\tCM  energy gain: " + FORMAT.format(trial.getCmEnergyGain()) + " J");
    System.out.println("\tRot energy gain: " + FORMAT.format(trial.getRotationalEnergyGain()) + " J");
    
    double minTetherForce, maxTetherForce;
    {
      Tetra craft = trial.getSystem().getCraft();
      int index = 5;
      minTetherForce = maxTetherForce = craft.getTetherByIndex(index);
      while (index-- > 0) {
        double tetherForce = craft.getTetherByIndex(index);
        if (tetherForce < minTetherForce)
          minTetherForce = tetherForce;
        else if (tetherForce > maxTetherForce)
          maxTetherForce = tetherForce;
      }
    }
    
    System.out.println("\tTether  force range: [" + FORMAT.format(minTetherForce) + ", " + FORMAT.format(maxTetherForce) + "] N");
    System.out.println("\tOrbital radius gain: " + FORMAT.format(trial.getRadiusGain()) + " m");
    System.out.println();
  }
  
  
  
  
  private final DecimalFormat FORMAT = new DecimalFormat("#,###.##");
  
  
  
  
  
  /**
   * Count the number of 3 point profiles over a circular path partitioned into
   * <tt>n</tt> regions. Using a computational short cut here instead of doing
   * the actual sum. Programmatically, this is just being cute. But it does give
   * insight how this might be generalized to <tt>k</tt> points. 
   */
  private int countRegionCombinations(int n) {
    int count = ((n - 2) * (n - 1)) / 2;
    count += sumOfSquares(n - 2);
    return count / 2;
  }
  
  
  private int sumOfSquares(int n) {
    return (n * (n + 1) * (2*n + 1)) / 6;
  }
  
  
  
  
  
  
  
  
  
  
  
  /**
   * We use this construct to systematically cover the decision space. We also
   * use this to save computational work at each decision branch.
   */
  private class TrialProcessor extends DecisionTreeProcessor<RegionProgression> {
    
    
    
    private final ArrayDeque<RegularShapeTrial> trialProgress = new ArrayDeque<>();
    
    private final double normalizedRegionDuration = 1.0 / regions;
    

    public TrialProcessor() {
      super(new RegionProgression(regions, 3));
      trialProgress.push(new RegularShapeTrial(constraints));
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
      
      print("Executing level " + level + ", region " + node.region());
      if (! trial.runToOrbitalPoint(command) ) {
        printBackout(node, trial);
        return false;
      }
      
      if (node.isLeaf()) {
        print("Maneuver completed. Holding shape until completion of orbit");
        trial.updateSnapshot();
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
    
    
    private void printBackout(RegionProgression node, RegularShapeTrial trial) {
      print(trial.getException());
      print(".. at normalized orbit time " + (((float) trial.trialTime()) / trial.getPeriodMillis()));
      print("Backing out of level " + node.level() + ", region " + node.region() + "\n");
    }
    
  }
  
  

  
  
  
  public static void main(String[] args) {
    int regions;
    if (args.length > 0) {
      regions = Integer.parseInt(args[0]);
      if (regions < 3)
        throw new IllegalArgumentException(args[0]);
      if (regions > 10) {
        System.out.println("Go grab a cuppa coffee. Call someone, whatever. Gonna be a while before we finish..");
        System.out.println();
      }
    } else
      regions = 4;
    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    RegularShapeTrialEnsemble instance = new RegularShapeTrialEnsemble(constraints, regions);
    
    System.out.println("Executing over " + regions + " regions");
    System.out.println(instance.countRegionCombinations(regions) + " possible combinations to try");
    System.out.println();
    
    instance.execute();
    
    List<RegularShapeTrial> trials = instance.getTrials();
    
    Collections.sort(trials, new TrialComparator());
    
    int countDown = Math.min(10, trials.size());


    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    System.out.println("#");
    System.out.println("#\tTop " + countDown + " count down");
    System.out.println("#");
    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    
    int i = 0;
    while (countDown-- > 0) {
      
      RegularShapeTrial trial = trials.get(countDown);
      System.out.println();
      System.out.println(++i + ".\t" + trial.getCommandsReceived());
      instance.printSummary(trial);
    }
  }
  
  
  
  
  
  
  
  public static class TrialComparator implements Comparator<RegularShapeTrial> {
    
    private final static double EG_EQUIV_RATIO = 0.85;
    private final static double IGNORABLE_E_DELTA = 1;


    @Override
    public int compare(RegularShapeTrial a, RegularShapeTrial b) {
      int fuzzyCmComparison = compareCmEnergyGain(a, b);
      if (fuzzyCmComparison != 0)
        return fuzzyCmComparison;
      
      double aRotEnergyLoss = -a.getRotationalEnergyGain();
      double bRotEnergyLoss = -b.getRotationalEnergyGain();
      
      if (Math.abs(aRotEnergyLoss - bRotEnergyLoss) < IGNORABLE_E_DELTA)
        return 0;
      
      return aRotEnergyLoss > bRotEnergyLoss ? 1 : -1;
    }
    
    
    private int compareCmEnergyGain(RegularShapeTrial a, RegularShapeTrial b) {
      double aCmE = a.getCmEnergyGain();
      double bCmE = b.getCmEnergyGain();
      if (Math.abs(aCmE - bCmE) < IGNORABLE_E_DELTA)
        return 0;
      if (Math.abs(aCmE) > Math.abs(bCmE) && bCmE / aCmE > EG_EQUIV_RATIO)
        return 0;
      if (Math.abs(bCmE) > Math.abs(aCmE) && aCmE / bCmE > EG_EQUIV_RATIO)
        return 0;
      return aCmE > bCmE ? 1 : -1;
    }
    
    
    
    
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
