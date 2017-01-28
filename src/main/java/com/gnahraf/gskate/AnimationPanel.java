/*
 * Copyright 2016 Babak Farhang
 */
package com.gnahraf.gskate;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.gnahraf.gskate.BigPlanet.AnimationListener;

/**
 *
 */
@SuppressWarnings("serial")
public class AnimationPanel extends JPanel implements BigPlanet.AnimationListener {
  
  private final BigPlanet system;
  
  
  private double metersPerPixel;
  private int originX, originY;
  
  /**
   * Coordinates of blobules a and b when last painted.
   */
  private double ax, ay, bx, by;
  
  private final ZenithTracker zenithTracker = new ZenithTracker();
  
  public AnimationPanel(BigPlanet system) {
    this.system = system;
    if (system == null)
      throw new IllegalArgumentException("null env");
    
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
    system.addAnimationListener(zenithTracker);
    system.addAnimationListener(this);
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        metersPerPixel = 0;
      }
    });
    float[] dashDef = { 2.0f };
    circleStroke = new BasicStroke(
        1.0f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER,
        5.0f, dashDef, 0.0f);
  }
  
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1000, 1000);
  }
  
  
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    ensureMetersPerPixelSet();
    int earthPixelRadius = (int) (BigPlanet.EARTH_RADIUS / metersPerPixel);
    int earthPixelDiameter = earthPixelRadius * 2;
    ax = system.getA().getX();
    ay = system.getA().getY();
    bx = system.getB().getX();
    by = system.getB().getY();
    
    int akx, aky, bkx, bky;
    akx = (int) (ax / 1000);
    aky = (int) (ay / 1000);
    bkx = (int) (bx / 1000);
    bky = (int) (by / 1000);
    g.drawString("gskate - " + metersPerPixel + " m/pix || coordinates [km]: (" + akx + "," + aky + ") (" + bkx + "," + bky + ")", 10, 20);
    g.drawString("R1: " + fmt(system.getA().getR()) + " m", 10, 40);
    g.drawString("R2: " + fmt(system.getB().getR()) + " m", 10, 60);
    g.drawString("V1: " + fmt(system.getA().getV()) + " m/s", 10, 80);
    g.drawString("V2: " + fmt(system.getB().getV()) + " m/s", 10, 100);
    
    g.drawString("TA: " + fmt(system.getTetheredAccel()) + " m/s2", 10, 120);
    
    g.drawString("E:  " + (int) (system.getEnergy() / 1000) + " kJ/kg", 10, 150);
    
    
    g.drawOval(originX - earthPixelRadius, originY - earthPixelRadius, earthPixelDiameter, earthPixelDiameter);
    
    Graphics2D g2 = (Graphics2D) g;
    Color oldColor = g2.getColor();
    Stroke oldStroke = g2.getStroke();
    
    g2.setColor(Color.GRAY);
    g2.setStroke(circleStroke);
    
    int radius = earthPixelRadius;
    for (int i = 10; i-- > 0; ) {
      radius += 10;
      g.drawOval(originX - radius, originY - radius, radius * 2, radius * 2);
    }
    
    g2.setColor(Color.BLUE);
    double zX, zY;
    synchronized (zenithTracker) {
      zX = zenithTracker.getX();
      zY = zenithTracker.getY();
    }
    g2.drawLine(originX, originY, toPixelCoordinateX(zX), toPixelCoordinateY(zY));
    
    
    g2.setStroke(oldStroke);
    g2.setColor(oldColor);
    
    paintBlobule(ax, ay, g);
    paintBlobule(bx, by, g);
  }
  private String fmt(double value) {
    return numFormat.format(value);
  }
  private final NumberFormat numFormat = new DecimalFormat("#,###.##");
  private final Stroke circleStroke;
  
  
  
  
  private void paintBlobule(double xMeters, double yMeters, Graphics g) {
    int x = toPixelCoordinateX(xMeters);
    int y = toPixelCoordinateY(yMeters);
    g.fillOval(x-2, y-2, 4, 4);
  }
  
  
  private void ensureMetersPerPixelSet() {
    if (metersPerPixel > 1)
      return;
    int pixelWidth = Math.min(getWidth(), getHeight());
    double maxMeters = 1.15 * 2 * Math.max(system.getA().getR(), system.getB().getR());
    metersPerPixel = maxMeters / pixelWidth;
    originX = originY = pixelWidth / 2;
  }
  
  
  private int toPixelCoordinateX(double meters) {
    int pixels = (int) (meters / metersPerPixel);
    return originX + pixels;
  }
  
  
  private int toPixelCoordinateY(double meters) {
    int pixels = (int) (meters / metersPerPixel);
    return originY - pixels;
  }


  public void blobulesMoved(double ax, double ay, double bx, double by) {
    double deltaX = Math.max(Math.abs(ax - this.ax), Math.abs(bx - this.bx));
    if (deltaX >= metersPerPixel) {
      repaint();
      return;
    }
    double deltaY = Math.max(Math.abs(ay - this.ay), Math.abs(by - this.by));
    if (deltaY >= metersPerPixel)
      repaint();
  }

}

//class ZenithTracker implements AnimationListener {
//  
//  private double maxR2;
//  private double x, y;
//  
//  
//  public ZenithTracker() {
//    x = 1;
//    y = 0;
//    maxR2 = 1;
//  }
//
//  public void blobulesMoved(double ax, double ay, double bx, double by) {
//    double avgX = (ax + bx) / 2;
//    double avgY = (ay + by) / 2;
//    double r2 = avgX*avgX + avgY*avgY );
//    if (r2 > maxR2) {
//      synchronized (this) {
//        maxR2 = r2;
//        x = avgX;
//        y = avgY;
//      }
//    }
//  }
//  
//  
//  private boolean crossing(double xHatNext, double yHatNext) {
//    return false;
//  }
//
//
//
//  public double getX() {
//    return x;
//  }
//
//
//
//  public double getY() {
//    return y;
//  }
//  
//}


class ZenithTracker implements AnimationListener {
  
  private final QuadrantZenithTracker[] quadTrackers = new QuadrantZenithTracker[5];
  private QuadrantZenithTracker zenithQuad;
  int quadIndex;
  
  public ZenithTracker() {
    for (int i = quadTrackers.length; i-- > 0; )
      quadTrackers[i] = new QuadrantZenithTracker();
    quadIndex = 1;
    zenithQuad = quadTrackers[0];
  }

  public void blobulesMoved(double ax, double ay, double bx, double by) {
    QuadrantZenithTracker overTracker = quadTrackers[0];
    double avgX = (ax + bx) / 2;
    double avgY = (ay + by) / 2;
    
    int qi = quadIndex(avgX, avgY);
    synchronized (this) {
      if (qi != quadIndex) {
        quadTrackers[quadIndex].copy(overTracker);
        quadIndex = qi;
        overTracker.clear();
        overTracker.blobulesMoved(avgX, avgY);
        zenithQuad = findZenith();
      } else {
        overTracker.blobulesMoved(avgX, avgY);
        if (overTracker != zenithQuad && overTracker.maxR2() > zenithQuad.maxR2())
          zenithQuad = overTracker;
      }
    }
  }
  
  
  private QuadrantZenithTracker findZenith() {
    QuadrantZenithTracker z = quadTrackers[0];
    for (int i = 1; i < quadTrackers.length; ++i) {
      QuadrantZenithTracker q = quadTrackers[i];
      if (q.maxR2() > z.maxR2())
        z = q;
    }
    return z;
  }
  
  
  private int quadIndex(double x, double y) {
    if (x > 0 && y >= 0)
      return 1;
    else if (x <= 0 && y > 0)
      return 2;
    else if (x < 0 && y <= 0)
      return 3;
    else
      return 4;
  }
  
  
  public double getX() {
    return zenithQuad.x();
  }
  
  public double getY() {
    return zenithQuad.y();
  }
  
}


final class QuadrantZenithTracker {

  
  private double maxR2;
  private double x, y;
  
  void blobulesMoved(double x, double y) {
    double r2 = x*x + y*y;
    
    if (r2 > maxR2) {
      maxR2 = r2;
      this.x = x;
      this.y = y;
    }
  }
  
  void clear() {
    maxR2 = 0;
  }
  
  double x() {
    return x;
  }
  
  double y() {
    return y;
  }
  
  double maxR2() {
    return maxR2;
  }
  
  void copy(QuadrantZenithTracker tracker) {
    maxR2 = tracker.maxR2;
    x = tracker.x;
    y = tracker.y;
  }
}
