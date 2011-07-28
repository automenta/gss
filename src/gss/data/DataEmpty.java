/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data;

import gss.Event;
import gss.data.DataPoints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Useful as a placeholder for unimplemented data sources
 * @author seh
 */
public class DataEmpty extends DataPoints {
    private static final List<Event> emptyList = Collections.unmodifiableList(new ArrayList());

    public DataEmpty(String id, String name, String category, String iconURL, String unit) {
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
