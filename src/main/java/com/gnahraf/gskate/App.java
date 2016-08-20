package com.gnahraf.gskate;


import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Launcher
 */
public class App {
  
  public static void main( String[] args ) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createGui();
      }
    });
  }
  
  
  
  private static void createGui() {

    final BigPlanet system = new BigPlanet(1200);
    JFrame f = new JFrame("gskate");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JSlider tetherSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
    tetherSlider.setMajorTickSpacing(100);
    tetherSlider.setMinorTickSpacing(25);
    tetherSlider.setPaintTicks(true);
    tetherSlider.setPaintLabels(true);
    
    final SeparationFuzzyController fuzzControl = new SeparationFuzzyController(system);
    
    ChangeListener tsListener = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
//        if (!slider.getValueIsAdjusting())
        fuzzControl.setTargetDistance(Math.max(100, 1000 * slider.getValue()));
//          system.setTetheredAccel(slider.getValue() / 100.0);
      }
    };
    tetherSlider.addChangeListener(tsListener);
    
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(tetherSlider);
    panel.add(new AnimationPanel(system));

    f.add(panel);
//    f.add(new AnimationPanel(system));
    f.pack();
    f.setVisible(true);
    
    system.launchAnimator();
  }
}
