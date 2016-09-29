/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate.gen.le;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class TrialTest {

  @Test
  public void test() {
    // this test hangs because the space of the randomized trial
    // is large, as is the odds of violating the constraints of the
    // trial. This led me to go after randomizing the *shape* of the
    // craft, rather than the tether forces (see ShapeFuzzyController).
//    Trial trial = new Trial(null, null, null);
//    long period = (long) LonelyEarthTest.computePeriodMillis(trial.getSystem());
//    long quarterPeriodSec = period / 4000;
//    int decisonPointsPerQuarter = 2;
//    trial.setRandomSeed(0);
//    
//    for (int countDown = 4; countDown-- > 0; ) {
//      trial.simulateRandomDecisions(quarterPeriodSec, decisonPointsPerQuarter);
//      System.out.println();
//      LonelyEarthTest.printCraft(trial.getSystem());
//      System.out.println();
//      LonelyEarthTest.printTetherLengths(trial.getSystem());
//    }
  }

}
