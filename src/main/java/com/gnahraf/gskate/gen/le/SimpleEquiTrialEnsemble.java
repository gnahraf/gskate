/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import java.util.ArrayList;
import java.util.List;

/**
 *
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
    this.scaledMin = constraints.initTetherLength / constraints.maxTetherLength;
    this.regions = regions;
    if (regions < 3)
      throw new IllegalArgumentException("regions " + regions);
    this.trials = new ArrayList<SimpleEquiTrial>(regions);
  }


  public void execute() {
  }
  
  
  protected List<SimpleEquiTrial.Profile> generateProfiles() {
    List<SimpleEquiTrial.Profile> profiles = new ArrayList<SimpleEquiTrial.Profile>(regions);
    
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
    
    // This yields a count N
    //
    //     2N = sigma[i: 1 -> n-2]{ i(i+1) }
    
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
    final int trialCount = 125;
    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    SimpleEquiTrialEnsemble instance = new SimpleEquiTrialEnsemble(constraints, trialCount);
//    instance.execute();
    for (int r = 3; r <= 100; ++r) {
      System.out.println(r + ":\t" + instance.countRegionCombinations(r));
    }
  }

}
