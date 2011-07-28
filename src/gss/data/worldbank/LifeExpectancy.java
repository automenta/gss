/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.worldbank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import gss.geo.LocationsCache;
import gss.geo.Located;

/**
 *
 * @author seh
 */
public class LifeExpectancy {
    LocationsCache geo = new LocationsCache();
    private static final Logger logger = Logger.getLogger(LifeExpectancy.class.toString());

    public LifeExpectancy(String inputCSVpath, String outputPathAndPrefix) throws Exception {
        File js = new File(outputPathAndPrefix + ".js");
        FileWriter fw = new FileWriter(js);
        
        geo.load("/work/survive/cache/geocache");
        
        File f = new File(inputCSVpath);
        Date lastModified = new Date(f.lastModified());

        BufferedReader reader = new BufferedReader(new FileReader(f));
        
        String header = reader.readLine();
        String[] headerTokens = header.split(",");

        logger.info(header);
        
        int latestYearColumn = headerTokens.length-2;
        
        String year = headerTokens[latestYearColumn];
        
        int lines = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            
            String[] tokens = line.split(",");
            
            if (tokens.length < 2)
                continue;
            
            String country = tokens[0].trim();
            
            //skip line labels with parentheses, these are likely not countries but other categories
            if (country.contains("("))                continue;
            if (country.equals("\"European Union\""))     continue;
            if (country.equals("\"High income\""))     continue;
            if (country.equals("\"Low income\""))     continue;
            if (country.equals("\"North America\""))     continue;
            if (country.equals("\"South Asia\""))     continue;
            
            if (!(tokens.length > latestYearColumn))
                continue;
            
            String le = tokens[latestYearColumn].trim();
            
            if (le.length() == 0) {
                continue;
            }
            
            
            
            final Located g = geo.get(country);
            if (g == null) {
                logger.severe("Unable to geolocate: " + country);
                continue;
            }
            
            if (!country.endsWith("\""))
                country = country + "\"";
            
            logger.info(country + " (" + g.latitude + ", " + g.longitude + "): " + le + " years");
            fw.write("addLifeExpectancy(" + country + ", " + g.latitude + ", " + g.longitude + ", " + le + ");\n");
            
            
            lines++;
            
        }

        fw.write("setLifeExpectancyData(\'" + lastModified.toString() + "\', " + year + ");\n");
        
        logger.info("# of lines: " + lines);
        
        geo.save("/work/survive/cache/geocache");
        
        fw.close();
        
    }
    
    public static void main(String[] args) {
        try {
            new LifeExpectancy("/work/survive/cache/WorldBank_LifeExpectancy.csv", "/work/survive/data/LifeExpectancy.csv");
        } catch (Exception ex) {
            Logger.getLogger(LifeExpectancy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
