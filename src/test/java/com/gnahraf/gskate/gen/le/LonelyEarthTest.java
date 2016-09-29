package com.gnahraf.gskate.gen.le;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gnahraf.gskate.gen.le.LonelyEarth;
import com.gnahraf.gskate.model.Bob;
import com.gnahraf.gskate.model.Constants;
import com.gnahraf.gskate.model.Tetra;

public class LonelyEarthTest {

  @Test
  public void test() {
    LonelyEarth.Constraints constraints = new LonelyEarth.Constraints();
    constraints.initTetherValue = 0;
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
    for (int i = 0; i < 3; ++i) {
      Bob bob = craft.getBob(i);
      for (int j = i + 1; j < 4; ++j) {
        double distance = bob.distance(craft.getBob(j));
        System.out.println("(" + i + "," + j + "): " + distance + " m");
      }
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
    System.out.println("R: " + r + "  m (" + (int) (r - Constants.EARTH_RADIUS) + " m above ground)");
    System.out.println("Position:   (" + x + ", " + y + ", " + z + ")");
    System.out.println("PE:        " + craft.getPe(system.getPotential()) + " J");
    System.out.println("KE:        " + craft.getKe() + " J");
    System.out.println("Energy     " + (craft.getKe() + craft.getPe(system.getPotential())) + " J");
    System.out.println("CM KE:     " + craft.getCmKe() + " J");
    System.out.println("CM Energy: " + craft.getCmEnergy(system.getPotential()) + " J");
  }
  
  
  
  public static double computePeriodMillis(LonelyEarth system) {
    double r = system.getCraft().getBob(0).distance(0, 0, 0);
    double circum = 2 * r * Math.PI;
    double period = circum / system.getCraft().getBob(0).getV();
    return (long) (period * 1000);
  }

}
