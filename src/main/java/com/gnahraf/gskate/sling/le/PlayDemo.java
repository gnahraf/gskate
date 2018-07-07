/*
 * Copyright 2018 Babak Farhang
 */
package com.gnahraf.gskate.sling.le;

import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.sling.Craft;
import com.gnahraf.gskate.sling.Sling;
import com.gnahraf.gskate.sling.TetherLengthControl;
import com.gnahraf.math.r3.Vector;
import com.gnahraf.print.TablePrint;
import com.gnahraf.sim.Checkpoint;
import com.gnahraf.sim.SimEngine;

/**
 *
 */
public class PlayDemo {
  
  final LowEarth system;
  final SimEngine simulation;
  
  final long startTime = System.currentTimeMillis();

  

  private PlayDemo(Sling sling) {
    this(sling, 100*1000);
  }
  

  private PlayDemo(Sling sling, long ticksPerSecond) {
    this.system = new LowEarth( new Craft(sling) );
    this.simulation = new SimEngine(system, ticksPerSecond);
  }
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    long ticksPerSecond = 200*1000;
    double controlPeriod = 0.01;
    Launcher launcher = new Launcher();
    
    double roll = 45;
    
    launcher.roll(roll);
    
    Sling sling = launcher.launch();
    PlayDemo i = new PlayDemo(sling, ticksPerSecond);
    
    double r = sling.getCm().magnitude();
    double v = sling.getCmVel().magnitude();
    
    double orbitalPeriod = 2 * Math.PI * r / v;
    
    i.println("orbital period: " + orbitalPeriod);
    i.println();
//    i.printStatus();
    
    int count = 8;
    
    TetherLengthControl control =
        i.system.getCraft().getController();
    
    control.setMaxTetherStength(1000);
    control.setMaxTetherIncrement(.05);
    control.setTargetLength(500);
    control.setFuzzyTimeToTarget(10);
    
    sling.setTether(-.005);
    
    Checkpoint controlCheckpoint = new Checkpoint() {
      @Override
      public void check() {
        control.adjustTether(i.simulation.getTime());
      }
    };
    
    i.simulation.schedule(controlCheckpoint, 0, controlPeriod, 10);
    
//    final int[] c = { 0 };
    
    i.simulation.schedule(
        new Checkpoint() {
          int c = 0;
          @Override
          public void check() {
            i.println();
            i.println(++c + ".");
            i.printStatus();;
          }
        },
        0,
        orbitalPeriod / count,
        0);
    i.simulation.animate(orbitalPeriod);
//    for (int c = count; c-- > 0;) {
//      i.simulation.animate(quarterPeriod);
//      i.println();
//      i.printStatus();
//    }
//    i.simulation.animate(orbitalPeriod);
    
    
  }
  
  private void println() {
    println("");
  }
  
  private void println(Object msg) {
    System.out.println(msg);
  }
  
  
  private void printStatus() {
    TablePrint table = new TablePrint(25, 45);
    table.setIndentation(8);
    table.printHorizontalTableEdge('=');
    table.printRow("Time", simulation.getTime());
    table.printRow("  simulation", simulation.getTime());
    table.printRow("  wall time", wallTime()) ;
    
    Sling sling = system.getCraft().getSling();
    
    
    table.printHorizontalTableEdge('-');
    table.printRow("Center of Mass (CM)");
    table.printHorizontalTableEdge('-');

    Vector cm = sling.getCm();
    table.printRow("Position", cm);
    table.printRow("  above ground", cm.magnitude() - Constants.EARTH_RADIUS);
    
    Vector cmVel = sling.getCmVel();
    table.printRow("Velocity", cmVel);
    table.printRow("  speed", cmVel.magnitude());
    println();
    table.printHorizontalTableEdge('-');
    table.printRow("Energy");
    table.printHorizontalTableEdge('-');
    
    double transEnergy = cmVel.magnitudeSq() * sling.getMass() / 2;
    double ke = sling.getKe();
    double rotEnergy = ke - transEnergy;
    
    table.printRow("Kinetic", null);
    table.printRow("  translational", transEnergy);
    table.printRow("  rotational", rotEnergy);
    table.printHorizontalTableEdge('-');
    table.printRow("  Subtotal", ke);
    table.printHorizontalTableEdge('-');
    table.printRow("PE", sling.getPe());
    
    println();
    table.printHorizontalTableEdge('-');
    table.printRow("Geometry", null);
    table.printHorizontalTableEdge('-');
    table.printRow("  length", sling.getBobA().distance(sling.getBobB()));
    table.printRow("  tether", sling.getTether());
    table.printHorizontalTableEdge('=');
    println();
    
  }
  
  
  private double wallTime() {
    return ((double) (System.currentTimeMillis() - startTime)) / 1000;
  }

}
