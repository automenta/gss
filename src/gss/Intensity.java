/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import gss.data.DataPoints;
import gss.data.DataPoints.ProximityFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Measures intensity at a certain point given pairs of Data,DataInterest
 * TODO return a Map<Affect, Double> instead of the preliminary scalar (threat) measurement
 * @author seh
 */
public class Intensity {
    private static final Logger logger = Logger.getLogger(Intensity.class.getName());
    
    public static double getIntensity(final Globe globe, final Position point, final Map<Data, DataInterest> dataInterest) {
        double total = 0;
        
        final ArrayList<Position> points = new ArrayList(2);
        points.add(point);
        points.add(point);
        
        final LengthMeasurer measurer = new LengthMeasurer();
        measurer.setFollowTerrain(false);
        measurer.setPositions(points);
        //measurer.setPathType(WorldWind.LINEAR);
        
        for (Entry<Data,DataInterest> en : dataInterest.entrySet()) {
            Data d = en.getKey();            
            DataInterest i = en.getValue();
            
            if (d instanceof DataPoints) {
                DataPoints dp = (DataPoints)d;
                
                Iterator<Event> v = dp.iterateEvents();
                                
                while (v.hasNext()) {
                    Event e = v.next();
                    
                    Position c = e.getCenter();
                    if (c == null) {
                        logger.severe(e + " has null getCenter()");
                        continue;
                    }
                    
                    points.set(1, c);
                    
                    try {
                        double dist = measurer.getLength(globe)/1000.0;
                        double meas = dp.getRelativeMeasurement(e.getMeasurement());

                        if (dp.getProximityFunction() == ProximityFunction.Linear)
                            total += meas / (1.0 + dist);
                        else if (dp.getProximityFunction() == ProximityFunction.InverseSquare)
                            total += meas / (1.0 + dist*dist);
                    }
                    catch (IllegalArgumentException iee) {
                        logger.severe(iee + " when measuring from " + point + " to " + c);                        
                    }
                    
                }
            }
        }
        return total;
    }
}
