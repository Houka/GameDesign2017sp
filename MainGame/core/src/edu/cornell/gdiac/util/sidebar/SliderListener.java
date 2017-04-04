package edu.cornell.gdiac.util.sidebar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;


public class SliderListener implements ChangeListener{
    /** Key value associated with this instance **/
    private String ref;
    /** The hashmap from which the key is associated with **/
    private HashMap<String,Float> theMap;
    /** Slider value scale **/
    private float scale;
    /** Slider value offset **/
    private float offset;

    /**
    *Constructer for SliderListener
    *@param refString       The key value associated with this instance 
    *@param outMap          The hashmap from which the key is associated with
    *@param min             Slider value scale
    *@param max             Slider value offset
    **/
    public SliderListener(String refString, HashMap<String,Float> outMap, float min, float max) {
        ref = refString;
        theMap = outMap;
        scale = (max-min)/100;
        offset = min;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider s = (JSlider)e.getSource();
        theMap.put(ref,(float)s.getValue()*scale+offset);
    }
}
