package com.gnahraf.gskate.gen.le;

import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.Test;

import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.Tetra;
import com.gnahraf.gskate.model.TetraEdge;

public class LonelyEarthTest {

  @Test
  public void test() {
    Constraints constraints = new Constraints();
    LonelyEarth system = new LonelyEarth(constraints);
    
    printTetherLengths(system);
    
    double r = system.getCraft().getBob(0).distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / system.getCraft().getBob(0).getV();
    long periodMillis = (long) (period * 1000);
    int reportLaps = 4;
    long lapMillis = periodMillis / reportLaps;

    System.out.println();
    System.out.println("Orbital period ~ " + (int) period + " seconds");
    printCraft(system);
    for (int i = reportLaps; i--> 0;) {
      system.animateMillis(lapMillis);
      printCraft(system);
    }
    
    printTetherLengths(system);
  }
  
  
  public static double roughPeriod(LonelyEarth system) {
    double r = system.getCraft().getBob(0).distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / system.getCraft().getBob(0).getV();
    return period;
  }
  
  public static void printTetherLengths(LonelyEarth system) {
    
    Tetra craft = system.getCraft();

    System.out.println();
    System.out.println("Tether lengths: ");
    System.out.println();
    
    for (TetraEdge edge : TetraEdge.values()) {
      double distance = craft.getBob(edge.loBob).distance(craft.getBob(edge.hiBob));
      System.out.println(edge + ": " + FORMAT.format(distance) + " m");
    }
  }
  
  
  public static void printCraft(LonelyEarth system) {
    Tetra craft = system.getCraft();
    double x, y, z = y = x = 0;
    for (int i = 0; i < 4; ++i) {
      Bob bob = craft.getBob(i);
      x += bob.getX();
      y += bob.getY();
      z += bob.getZ();
    }
    x /= 4;
    y /= 4;
    z /= 4;
    
    double r = Math.sqrt(x*x + y*y + z*z);

    System.out.println();
    System.out.println("R: " + FORMAT.format(r) + " m (" + FORMAT.format((int) (r - Constants.EARTH_RADIUS)) + " m above ground)");
    System.out.println("Position:   (" + FORMAT.format(x) + ", " + FORMAT.format(y) + ", " + FORMAT.format(z) + ")");
    System.out.println("PE:        " + SCI_FORMAT.format(craft.getPe(system.getPotential())) + " J");
    System.out.println("KE:        " + SCI_FORMAT.format(craft.getKe()) + " J");
    System.out.println("Energy     " + SCI_FORMAT.format((craft.getKe() + craft.getPe(system.getPotential()))) + " J");
    System.out.println("CM KE:     " + SCI_FORMAT.format(craft.getCmKe()) + " J");
    System.out.println("CM Energy: " + SCI_FORMAT.format(craft.getCmEnergy(system.getPotential())) + " J");
  }
  
  
  
  public static void printTetherForces(LonelyEarth system) {
    Tetra craft = system.getCraft();

    System.out.println();
    System.out.println("Tether forces (N) (+/- means push/pull)");
    for (TetraEdge edge : TetraEdge.values())
      System.out.println(edge + ": " + FORMAT.format(craft.getTetherByIndex(edge.index)));
    
  }
  
  public final static DecimalFormat FORMAT = new DecimalFormat("#,###.#");
  public final static DecimalFormat SCI_FORMAT = new DecimalFormat("0.#####E0");
  
  
  
  public static double computePeriodMillis(LonelyEarth system) {
    double r = system.getCraft().getBob(0).distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / system.getCraft().getBob(0).getV();
    return (long) (period * 1000);
  }

}
