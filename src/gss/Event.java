/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import java.util.Date;

/**
 *
 * @author seh
 */
public interface Event {
    
    public String getLabel();
    public String getDescription();
    
    /** when the event started, null for always existing */
    public Date getStart();
    
    /** when the event stopped, null for ongoing */
    public Date getStop();
        
    public Position getCenter();
    
    /** a scalar measurement, in the units specified by the providing Data source */
    public double getMeasurement();
    
    public class RadialEvent implements Event {
        private double radius;
        private String label;
        private String description;
        private Date start;
        private Date stop;
        private Position center;
        private double measurement;
        

        private static final double defaultAltitude = 1.0;
        
        public RadialEvent(final String label, final Date when, double latitude, double longitude, double radius, double measurement) {
            this(label, when, 
                   new Position(new LatLon(Angle.fromDegreesLatitude(latitude), Angle.fromDegreesLongitude(longitude)), defaultAltitude),
                   radius, measurement);
        }
        
        public RadialEvent(final String label, final Date when, Position center, double radius, double measurement) {
            this.label = label;
            this.description = "";
            this.start = this.stop = when;
            this.center = center;
            this.radius = radius;                       
            this.measurement = measurement;
        }        
        
        /** radius around which it directly affects, may be zero */
        public double getRadius() {
            return radius;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Date getStart() {
            return start;
        }

        @Override
        public Date getStop() {
            return stop;
        }

        @Override
        public Position getCenter() {
            return center;
        }

        public double getMeasurement() {
            return measurement;
        }
                
    }
    
    /*
    class CountryEvent {
       String country
    }
    class CityEvent {
        String city
    }
    class PolyEvent {
        LatLng[] coords
        boolean isInside(LatLng p)
        boolean distanceFromCenter(LatLng p)
    }
    */
}
