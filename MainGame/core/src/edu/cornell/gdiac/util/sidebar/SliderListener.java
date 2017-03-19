package edu.cornell.gdiac.util.sidebar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;

/**
 * Created by Bucc on 3/18/2017.
 */
public class SliderListener implements ChangeListener{
    private String ref;
    private HashMap<String,Float> theMap;
    private float scale;
    private float offset;

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
        System.out.println(theMap.get(ref));
    }
}
