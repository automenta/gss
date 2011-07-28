/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import gss.Event.RadialEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A collection of events provided by indicator or sensor data source for modeling the environment
 * @author seh
 */
abstract public class Data {
    
    public final String id;
    public final String name;
    //String description;

    public final String category;
    public final String iconURL;
    public final String unit;
    
    public Data(String name, String category, String iconURL, String unit) {
        this(null, name, category, iconURL, unit);
    }    

    public Data(String id, String name, String category, String iconURL, String unit) {
        super();
        
        if (id == null)
            this.id = getClass().getName();
        else
            this.id = id;
        
        this.name = name;
        this.category = category;
        this.iconURL = iconURL;
        this.unit = unit;
    }
    

    abstract public double getMinMeasurement();
    abstract public double getMaxMeasurement();
    abstract public Iterator<Event> iterateEvents();

    public double getNormalizedMeasurement(Event e) {        
        double min = getMinMeasurement();
        double max = getMaxMeasurement();
        
        if (min == max)
            return 0.5;
        
        return (e.getMeasurement()- min) / (max - min);
    }
    
    
    
    public static class EmptyData extends Data {
        private static final ArrayList<Event> emptyList = new ArrayList();

        public EmptyData(String id, String name, String category, String iconURL, String unit) {
            super(id, name, category, iconURL, unit);
        }

        @Override
        public double getMaxMeasurement() {
            return 0;
        }

        @Override
        public double getMinMeasurement() {
            return 0;
        }               
        
        @Override
        public Iterator<Event> iterateEvents() {
            return emptyList.iterator();
        }        
                
    }
    
    public static class StaticData extends Data {
        private static final List<Event> events = new LinkedList();

        double min=0, max=0;
        
        public StaticData(String name, String category, String iconURL, String unit) {
            super(name, category, iconURL, unit);
        }

        public void clear() {
            events.clear();
        }
        
        public void add(Event e) {
            events.add(e);
            
            double m = e.getMeasurement();
            if ((max < m) || ((min==0) && (max==0))) max = m;
            if ((min > m) || ((min==0) && (max==0))) min = m;
        }
        
        @Override
        public Iterator<Event> iterateEvents() {
            return events.iterator();
        }        
        
        @Override
        public double getMaxMeasurement() {
            return max;
        }

        @Override
        public double getMinMeasurement() {
            return min;
        }               
                
    }
    
}
