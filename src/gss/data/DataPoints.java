/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data;

import gss.Data;
import gss.Event;
import java.util.Iterator;

/**
 * data that consists of a set of events
 */
/**
 *
 * @author seh
 */
public abstract class DataPoints extends Data {

    public DataPoints(String name, String category, String iconURL, String unit) {
        this(null, name, category, iconURL, unit);
    }

    public DataPoints(String id, String name, String category, String iconURL, String unit) {
        super(id, name, category, iconURL, unit);
    }

    public abstract Iterator<Event> iterateEvents();
    
}
