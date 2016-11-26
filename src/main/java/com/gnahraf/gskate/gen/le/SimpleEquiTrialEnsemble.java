/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gnahraf.gskate.model.Tetra;

/**
 * An ensemble of {@linkplain SimpleEquiTrial}s with 3 point
 * {@linkplain SimpleEquiTrial.Profile profiles}. In order to
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
 * <h4>After-thoughts</h4>
 * <p/>
 * On increasing the number of the regions, we discover entire classes of
 * ineligible profiles that we need not try. This, in turn, is a general problem
 * that I'll doubtless revisit under other conditions, as I vary the model. Turning
 * my attention in this direction. -- 26 November 2016
 */
public class SimpleEquiTrialEnsemble {
  
  private final static double SCALED_MAX = 0.98;
  

  
  private final LonelyEarth.Constraints constraints;
  private final double scaledMin;
  private final int regions;
  
  private final List<SimpleEquiTrial> trials;
  

  /**
   * 
   */
  public SimpleEquiTrialEnsemble(LonelyEarth.Constraints constraints, int regions) {
    this.constraints = constraints;
    this.scaledMin = constraints.steadyStateTetherLength / constraints.maxTetherLength;
    this.regions = regions;
    if (regions < 3)
      throw new IllegalArgumentException("regions " + regions);
    this.trials = new ArrayList<SimpleEquiTrial>(countRegionCombinations(regions));
  }


  public void execute() {
    for (SimpleEquiTrial.Profile profile : generateProfiles()) {
      SimpleEquiTrial trial = new SimpleEquiTrial(constraints, profile);
      trials.add(trial);
      System.out.print("Running orbit control profile " + profile.getPoints() + "\t..");
      trial.runOneOrbit();
      if (trial.failed()) {
        System.out.println(" FAIL - " + trial.getException());
      } else {
        System.out.println(" DONE");
      }
    }
  }
  



  public List<SimpleEquiTrial> getTrials() {
    return trials;
  }
  
  
  
  
  
  
  
  
  
  protected List<SimpleEquiTrial.Profile> generateProfiles() {
    
    // each simple profile is characterized by 3 points.
    // Even with this simplified setup, the decision space is large.
    
    // We could take a naive approach and test the space over a 3 level decision
    // tree.
    // int childCount = (int) Math.round(Math.pow(count, 1.0 / 3));
    //
    // maybe not.. (would skew samples toward corner cases.)
    
    // Forget counting the trials for the moment..
    //
    // Instead, picture partitioning the orbit into regions -- like teeth on a wheel.
    // We wish to cover all combinations of 3 point profiles over these teeth.
    
    // Every profile under consideration returns to the original state (scaledMin).

   /*
     
      tether length
       |       
       |(1)     (2)
       |  * * * *
       | *        * (3)
       |*           * * * * * *
       |          
       |_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ time
      0                        1
      
    */
    
    // The 3 points are of the form
    //
    // (t1, max), (t2, max), (t3, min),
    //                           with    0 < t1 < t2 < t3 <= 1,
    //                           and     0 < min < max <= 1
    //
    // min and max are fixed for the ensemble; we vary t1, t2, and t3
    
    // Let's say there are n teeth. Since they're arranged in a circle,
    // there are also n regions.
    //
    // For the purpose of counting the # of combinations, let us renormalize
    // the time axis so that
    //
    //              0 < 0 < t1 < t2 < t3 <= n  (n >= 3)
    //
    // with t1, t2, and t3 taking on only integral values.
    
    // This yields a count N s.t.
    //
    //     2N = sigma[i: 1 -> n-2]{ i(i+1) }
    
    // So the problem space grows with the square of the # of regions (derivative)
    // and is O(n^3)
    
    List<SimpleEquiTrial.Profile> profiles =
        new ArrayList<SimpleEquiTrial.Profile>(countRegionCombinations(regions));
    
    double regionDuration = 1.0 / regions;
    for (int t1 = 1; t1 <= regions - 2; ++t1)
      for (int t2 = t1 + 1; t2 <= regions - 1; ++t2)
        for (int t3 = t2 + 1; t3 <= regions; ++t3) {
          SimpleEquiTrial.Profile profile = new SimpleEquiTrial.Profile();
          profile.addNextPoint(t1 * regionDuration, SCALED_MAX);
          profile.addNextPoint(t2 * regionDuration, SCALED_MAX);
          profile.addNextPoint(t3 * regionDuration, scaledMin);
          profiles.add(profile);
        }
    return profiles;
  }
  
  
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
   * @param args
   */
  public static void main(String[] args) {
    int regions = 4;
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
    SimpleEquiTrialEnsemble instance = new SimpleEquiTrialEnsemble(constraints, regions);
    
    System.out.println("Executing over " + regions + " regions");
    System.out.println(instance.countRegionCombinations(regions) + " combinations to try");
    System.out.println();
    
    instance.execute();

    System.out.println();
    System.out.println("Ranking trials..");
    System.out.println();
    
    List<SimpleEquiTrial> trials = instance.getTrials();
    Collections.sort(trials, new TrialComparator());
    
    DecimalFormat FORMAT = new DecimalFormat("#,###.#");
    int i = 0;
    
    int countDown = Math.min(10, trials.size());

    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    System.out.println("#");
    System.out.println("#\tTop " + countDown + " count down");
    System.out.println("#");
    System.out.println("# # # # # # # # # # # # # # # # # # # # # # # #");
    
    while (countDown-- > 0) {
      SimpleEquiTrial trial = trials.get(countDown);
      
      System.out.println();
      System.out.println(1 + countDown + ".\t" + trial.getProfile().getPoints());
      System.out.println("\tCM  energy gain: " + FORMAT.format(trial.getCmEnergyGain()) + " J");
      System.out.println("\tRotation energy: " + FORMAT.format(trial.getRotationalEnergy()) + " J");
      
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
    }
    
    
//    for (int r = 3; r <= 100; ++r) {
//      System.out.println(r + ":\t" + instance.countRegionCombinations(r));
//    }
  }
  
  
  
  
  public static class TrialComparator implements Comparator<SimpleEquiTrial> {
    
    private final double rotationalPenaltyFactor = 0.5;

    public int compare(SimpleEquiTrial a, SimpleEquiTrial b) {
      
      if (a.failed()) {
        return b.failed() ? 0 : -1;
      } else if (b.failed())
        return 1;
      
      double scoreA = score(a);
      double scoreB = score(b);
      if (scoreA > scoreB)
        return 1;
      else if (scoreA == scoreB)
        return 0;
      else
        return -1;
    }
    
    
    private double score(SimpleEquiTrial trial) {
      double rotationalEnergy = trial.getRotationalEnergy();
      return trial.getCmEnergyGain() - rotationalPenaltyFactor * rotationalEnergy * rotationalEnergy;
    }
    
  }
  

}
