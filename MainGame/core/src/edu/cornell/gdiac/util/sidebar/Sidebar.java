package edu.cornell.gdiac.util.sidebar;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;

public class Sidebar {

    private static JFrame frame;
    private static JPanel panel;
    private static int currHeight;
    public static int value =0;
    private static HashMap<String,Float> theMap;
    private static boolean created = false;

    private static void createAndShowGUI() {
        frame = new JFrame("Sidebar Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(600,currHeight));
        frame.setPreferredSize(new Dimension(600,currHeight));
        frame.getContentPane().add(panel);
        panel.add(Box.createRigidArea(new Dimension(0,10)));

        frame.pack();
        frame.setVisible(true);


    }


    /**
     * Call this to boot up the SidebarTool with default parameters
     */
    public static void defaultBootup(){
        if (created)
            return;

        Sidebar.bootUp();
        created = true;
        //Below fields are just samples
        Sidebar.addSlider("Gravity",-30f,0f,-20.0f);
        Sidebar.addSlider("Jump Height",10f,30f, 15f);
        Sidebar.addSlider("Player Speed",1f,10f,5.0f);
        Sidebar.addSlider("Knockback Force",10f,30f, 15f);
        Sidebar.addSlider("Knockback Duration",10f,80f, 45f);
        Sidebar.addSlider("Bullet Height",.1f,.5f,.25f);
        Sidebar.addSlider("Bullet Width",.5f,6.5f,3f);
        Sidebar.addSlider("Bullet Speed",1f,7.5f,3.2f);
        Sidebar.addSlider("Bullet-Bullet Stick Time",0f,10f,5f);
        Sidebar.addSlider("Bullet-Wall Stick Time",0f,10f,5f);
    }

    public static void bootUp() {
        theMap = new HashMap<String,Float>();
        currHeight = 50;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void addSlider(String myReference,float from, float to, float begin) {
        final String ref = myReference;
        final int height = currHeight;
        final float min = from;
        final float max = to;
        final float start = begin;
        currHeight+=72;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JLabel l = new JLabel(ref,SwingConstants.CENTER);
                l.setBounds(300,height,600,20);
                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                final JSlider s = new JSlider(JSlider.HORIZONTAL,0,100,100);
                s.setAlignmentX(Component.CENTER_ALIGNMENT);
                l.setBounds(300,height+20,600,50);
                s.addChangeListener(new SliderListener(ref,theMap,min,max));
                s.setMajorTickSpacing(20);
                s.setMinorTickSpacing(5);
                s.setPaintTicks(true);
                s.setPaintLabels(true);
                s.setValue((int)((start-min)/(max-min)*100));
                Enumeration e = s.getLabelTable().keys();

                while (e.hasMoreElements()) {
                    Integer i = (Integer) e.nextElement();
                    JLabel label = (JLabel) s.getLabelTable().get(i);
                    label.setPreferredSize(new Dimension(100,10));
                    label.setSize(label.getPreferredSize());
                    label.setText(String.valueOf(Math.round(((i/100f)*(max-min)+min)*100)/100f));
                }
                s.setBorder(
                        BorderFactory.createEmptyBorder(0,0,10,0));
                panel.add(l);
                panel.add(s);
                panel.add(Box.createRigidArea(new Dimension(0,10)));
                panel.setPreferredSize(new Dimension(600,currHeight));
                frame.setPreferredSize(new Dimension(600,currHeight));
                panel.updateUI();
                frame.pack();
            }
        });
    }

    public static void addButton(String buttonName) {
        final String bname = buttonName;
        final int height = currHeight;
        currHeight+=50;
         javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JButton b = new JButton(bname);
                b.setBounds(100,height,50,50);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        theMap.put(bname,theMap.get(bname)+1);
                        value++;
                    }
                });
                panel.add(b);
                panel.add(Box.createRigidArea(new Dimension(0,10)));
                panel.setPreferredSize(new Dimension(600,currHeight));
                frame.setPreferredSize(new Dimension(600,currHeight));
                panel.updateUI();
                frame.pack();
            }
        });
        theMap.put(bname,0f);
    }

    /*
     * Returns key value if exists, else 0.0
     */
    public static float getValue(String key) {
        if(!theMap.containsKey(key))
            return 0.0f;
        return theMap.get(key);
    }
}
