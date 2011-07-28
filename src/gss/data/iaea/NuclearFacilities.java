/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.iaea;

import gss.Event;
import gss.Event.RadialEvent;
import gss.data.DataPointsList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * IAEA Nuclear Facilities
 * @author seh
 */
public class NuclearFacilities extends DataPointsList { 
    private static final Logger logger = Logger.getLogger(NuclearFacilities.class.toString());

    //http://www.google.com/fusiontables/DataSource?dsrcid=579353&search=nuclear&cd=5
    //Seems to be last updated on March 15 2011
    
    final Date dateCollected = new GregorianCalendar(2011, 3, 15).getTime();
      
    public NuclearFacilities(String csvPath) {
        super("Nuclear Facilities", "Pollution", "nuclear.png", "Number of Reactors");
        
        setDrawIcons(true);
        setDrawLabels(true);
        
        if (csvPath == null) {
            //TODO load from web and store at csvPath            
            throw new UnsupportedOperationException(this.toString() + " Fetching from web");
        }

        File f = new File(csvPath);
        Date lastModified = new Date(f.lastModified());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String header = reader.readLine();
            String[] headerTokens = header.split(",");

            int lines = 0;
            while (reader.ready()) {
                String line = reader.readLine().trim();
                String[] tokens = line.split(",");

                if (tokens.length < 5) {
                    break;
                }

                String country = tokens[0].trim().replace("\"", "");
                String name = tokens[1].trim().replace("\"", "");
                double lat = Double.parseDouble(tokens[2].trim().replace("\"", ""));
                double lng = Double.parseDouble(tokens[3].trim().replace("\"", ""));
                
                int totalReactors = Integer.parseInt(tokens[4].trim().replace("\"", ""));
                
                double radius = 1000;
                Event e = new RadialEvent( name, dateCollected, lat, lng, radius, totalReactors );
                add(e);

                lines++;
            }
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        
    }

}
