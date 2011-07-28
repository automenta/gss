/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data;

import gov.nasa.worldwind.geom.Position;
import gss.Data;
import gss.Event;

/**
 * data that can be calculated (as an event) for a given geolocation
 */
public abstract class DataSpace extends Data {

    public DataSpace(String name, String category, String iconURL, String unit) {
        super(name, category, iconURL, unit);
    }

    public abstract Event getEvent(Position p);
    
}
