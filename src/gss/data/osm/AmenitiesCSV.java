/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.osm;

import gss.Event;
import gss.Event.RadialEvent;
import gss.data.DataPointsList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class AmenitiesCSV extends DataPointsList {
    private static final Logger logger = Logger.getLogger(AmenitiesCSV.class.toString());

    public AmenitiesCSV(String name, String category, String iconURL, String unit, String csvPath, String whichAmenity) {
        super(name, category, iconURL, unit);
        
        File f = new File(csvPath);
        Date lastModified = new Date(f.lastModified());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            int lines = 0;
            while (reader.ready()) {
                String line = reader.readLine().trim();

                String[] tokens = line.split(",");
                
                double lat = Double.parseDouble(tokens[0]);
                double lon = Double.parseDouble(tokens[1]);
                String amenity = tokens[2];

                System.out.println(amenity + " " + lat + " " + lon);
                
                if (amenity.equalsIgnoreCase(whichAmenity)) {
                    double radius = 100;                    
                    Event e = new RadialEvent( amenity, lastModified, lat, lon, radius, 1.0 );
                    add(e);
                }
                
                
            }
        }
        catch (Exception e) {
            logger.severe(e.toString());
        }
    }
    
    
}
