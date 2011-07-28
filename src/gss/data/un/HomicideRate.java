/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.un;

import gss.data.DataPointsList;
import gss.Event;
import gss.Event.RadialEvent;
import gss.geo.LocationsCache;
import gss.geo.Located;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class HomicideRate extends DataPointsList {

    private static final Logger logger = Logger.getLogger(HomicideRate.class.toString());
    //http://data.un.org/Data.aspx?d=UNODC&f=tableCode%3a1
    //  Rate is in Murders per 100,000 people
    //  CSV: http://data.un.org/Handlers/DownloadHandler.ashx?DataFilter=tableCode:1&DataMartId=UNODC&Format=csv&c=2,3,5,7,9,11,12&s=countryName:asc,yr:desc
    Map<String, Integer> countryYear = new HashMap();
    Map<String, Double> countryRate = new HashMap();

    public HomicideRate(LocationsCache geo, String csvPath) {
        super("UN Murder Rate", "Crime", "gun.png", "Murders per 100,000 People");

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

                if (tokens.length < 3) {
                    break;
                }

                String country = tokens[0].trim().replace("\"", "");
                int year = Integer.parseInt(tokens[1].trim().replace("\"", ""));
                double rate = Double.parseDouble(tokens[2].trim().replace("\"", ""));

                Integer previousYear = countryYear.get(country);
                if (previousYear == null) {
                    countryYear.put(country, year);
                    countryRate.put(country, rate);
                } else if (previousYear < year) {
                    countryYear.put(country, year);
                    countryRate.put(country, rate);
                } else if (previousYear == year) {
                    double previousRate = countryRate.get(country);

                    //use the highest value
                    if (previousRate < rate) {
                        countryRate.put(country, rate);
                    }
                }

                lines++;
            }

            for (String c : countryYear.keySet()) {
                int year = countryYear.get(c);
                double rate = countryRate.get(c);

                final Located g = geo.get(c);
                if (g == null) {
                    logger.severe("Unable to geolocate: " + c);
                    continue;
                }

                //logger.info(c + " " + year + " " + rate);
                
                double radius = 1000;
                Event e = new RadialEvent(c, new GregorianCalendar(year, 1, 1).getTime(), g.latitude, g.longitude, radius, rate );
                add(e);
            }
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

//            if (!country.endsWith("\""))
//                country = country + "\"";
//            
//            logger.info(country + " (" + g.latitude + ", " + g.longitude + "): " + le + " years");
//            fw.write("addLifeExpectancy(" + country + ", " + g.latitude + ", " + g.longitude + ", " + le + ");\n");
//            
//            
//            lines++;
//            
//        }
//
//        fw.write("setLifeExpectancyData(\'" + lastModified.toString() + "\', " + year + ");\n");
//        
//        logger.info("# of lines: " + lines);
//        
    
}
