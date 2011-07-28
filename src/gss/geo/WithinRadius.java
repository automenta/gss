/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.geo;

import com.google.common.base.Predicate;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import gss.Event;
import java.util.ArrayList;

/**
 *
 * @author seh
 */
public class WithinRadius implements Predicate<Event> {
    public final Position center;
    public final double radiusMeters;

    private final ArrayList<Position> p = new ArrayList(2);
    private final Globe globe;
    
    public WithinRadius(Globe globe, Position center, double radiusMeters) {        
        super();
        this.globe = globe;
        this.center = center;
        this.radiusMeters = radiusMeters;
        p.add(center);
    }

    
    @Override
    public boolean apply(final Event t) {
        p.set(1, t.getCenter());
        double d = new LengthMeasurer(p).getLength(globe);
        return true;
    }
    
}
