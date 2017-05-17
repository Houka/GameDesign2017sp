package edu.cornell.gdiac.util.sidebar;

import edu.cornell.gdiac.util.SoundController;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;

/** This class represents the sidebar menu to change in-game variables **/
public class Sidebar {

    /**The frame**/
    private static JFrame frame;
    /**The main panel contained within the frame**/
    private static JPanel panel;
    
    /**Current height of the frame**/
    private static int currHeight;
    /**Hashmap of all the values**/
    private static HashMap<String,Float> theMap;
    
    /**Whether or not the Sidebar has been set up**/
    private static boolean created = false;
    
    /**Button's value**/
    public static int value =0;

    /**Create and show sidebar window**/
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
        Sidebar.addSlider("Gravity",-30f,0f,-29.0f);
        Sidebar.addSlider("Jump Height",1f,70f, 11.4f);
        Sidebar.addSlider("Player Speed",1f,20f,7.65f);
        Sidebar.addSlider("Knockback Force",0f,30f, 8f);
        Sidebar.addSlider("Knockback Friction",0f,.1f, .06f);
        Sidebar.addSlider("Knockback Duration",0f,80f, 3f);
        Sidebar.addSlider("Knockback Stun Duration",0f,60f, 30f);
        Sidebar.addSlider("Paintball Height",.1f,.5f,.46f);
        Sidebar.addSlider("Paintball Width",.5f,6.5f,3.5f);
        Sidebar.addSlider("Paintball Speed",1f,7.5f,5.225f);
        Sidebar.addSlider("Paintball-paintball Stick Time",0f,10f,8f);
        Sidebar.addSlider("Paintball-Wall Stick Time",0f,10f,2f);
        Sidebar.addSlider("Camera Speed",0f,.3f,.1f);
        Sidebar.addSlider("Rumble Intensity",0f,100f,0f);
        Sidebar.addSlider("Rumble Interval",0f,10f,3f);
    }

    public static void initDefaultSettings(){
        if (theMap == null)
            theMap = new HashMap<String, Float>();
        Sidebar.setValue("Gravity",-30.0f);
        Sidebar.setValue("Jump Height",14.25f);
        Sidebar.setValue("Player Speed",7.65f);
        Sidebar.setValue("Knockback Force",12f);
        Sidebar.setValue("Knockback Friction",.06f);
        Sidebar.setValue("Knockback Duration",3f);
        Sidebar.setValue("Knockback Stun Duration",20f);
        Sidebar.setValue("Paintball Height",.46f);
        Sidebar.setValue("Paintball Width",3.5f);
        Sidebar.setValue("Paintball Speed",5.225f);
        Sidebar.setValue("Paintball-paintball Stick Time",8f);
        Sidebar.setValue("Paintball-Wall Stick Time",2f);
        Sidebar.setValue("Camera Speed",.1f);
        Sidebar.setValue("Rumble Intensity",0f);
        Sidebar.setValue("Rumble Interval",3f);
    }

    /**Initialize sidebar*/
    public static void bootUp() {
        if (theMap == null)
            theMap = new HashMap<String,Float>();
        currHeight = 50;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
    *Create a slider which represents a variable
    *@param myReference     String to call new variable by
    *@param from            Min value of the variable
    *@param to              Max value of the variable
    *@param begin           Default value of the variable
    **/
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

    /**
    *Add a button
    *@param buttonName      The button's name
    **/
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

    /**
     * Basic getter for hashmap values
     *@param key        Variable name to get value of
     *@return           Returns key value if exists, else 0.0 
     **/
    public static float getValue(String key) {
        if(theMap == null || !theMap.containsKey(key))
            return 0.0f;
        return theMap.get(key);
    }

    /**
     * Puts values into the hashmap with
     *
     * @param key   Variable name to get value of
     * @param value The value to set the variable name to
     *
     * @returns whether or not we have successfully set the value
     */
    public static boolean setValue(String key, float value){
        if (theMap==null)
            return false;
        theMap.put(key, value);
        return true;
    }
}
