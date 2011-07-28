/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data;

import gss.Event;
import gss.data.DataPoints;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class DataPointsList extends DataPoints implements Serializable {
    private final List<Event> events = new LinkedList();
    double min = 0;
    double max = 0;

    public DataPointsList(String name, String category, String iconURL, String unit) {
        super(name, category, iconURL, unit);
    }

    public void clear() {
        events.clear();
    }

    public void add(Event e) {
        events.add(e);
        double m = e.getMeasurement();
        if ((max < m) || ((min == 0) && (max == 0))) {
            max = m;
        }
        if ((min > m) || ((min == 0) && (max == 0))) {
            min = m;
        }
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

    public void save(String filePath) {
        //TODO
    }
    
}
