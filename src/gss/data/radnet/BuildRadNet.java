/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.radnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import gss.geo.LocationsCache;
import gss.geo.Located;

/**
 *
 * @author seh
 */
public class BuildRadNet {
    private final Date lastModified;
    private final File f;
    private static final Logger logger = Logger.getLogger(BuildRadNet.class.toString());
    
    public class Sample {
        String state;
        String location;
        Date datePosted;
        Date dateCollected;
        String sampleType;
        String unit;
        
        //isotope -> concentration
        Map<String, Double> concentration = new HashMap();         

        Located geo;
    }
    
    List<Sample> samples = new LinkedList();
    LocationsCache geo = new LocationsCache();
    
    //parses date in mm/dd/yyyy format
    private Date parseDate(String d) throws ParseException {
        return new SimpleDateFormat("M/d/y").parse(d);
    }
    
    public BuildRadNet(String inputCSVpath, String outputPathAndPrefix) throws Exception {
        super();
        
        File js = new File(outputPathAndPrefix + ".js");
        FileWriter fw = new FileWriter(js);
        
        geo.load("/work/survive/cache/geocache");
        
        f = new File(inputCSVpath);
        lastModified = new Date(f.lastModified());
        
        logger.info("Date: " + lastModified);
        
        BufferedReader reader = new BufferedReader(new FileReader(f));
        
        String header = reader.readLine();
        String[] headerTokens = header.split(",");

        logger.info(header);
            
        int lines = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            
            String[] tokens = line.split(",");
            
            if (tokens.length < 2)
                continue;
            
            Sample s = new Sample();
            {
                s.state = tokens[0].trim();
                s.location = tokens[1].trim();
                s.datePosted = parseDate(tokens[2].trim());
                s.dateCollected = parseDate(tokens[3].trim());
                s.sampleType = tokens[4].trim();
                s.unit = tokens[5].trim();
                for (int t = 6; t < tokens.length; t++) {
                    String isotope = headerTokens[t];
                    String concentration = tokens[t].trim();
                    
                    try {   
                        double cv = Double.valueOf(concentration);
                        s.concentration.put(isotope, Double.valueOf(concentration));
                    }
                    catch (Exception e) { }                                        
                }
            }            
            samples.add(s);
            
            lines++;
        }
        
        for (Sample s : samples) {
            String address = s.location + ", " + s.state;
            final Located g = geo.get(address);
            if (g == null) {
                logger.severe("Unable to geolocate: " + address);
                continue;
            }
            s.geo = g;
            
            double totalConcentration = 0;
            for (Double d : s.concentration.values()) {
                totalConcentration += d;
            }
            
            String collected = new SimpleDateFormat("MM/dd/yyyy").format(s.dateCollected);
            String script = "addRadNet(\"" + collected + "\", \"" + s.sampleType + " " + s.unit + "\", \"" + s.concentration + "\", " + totalConcentration + ", " + s.geo.latitude + ", " + s.geo.longitude + ");\n";
            fw.write(script);
        }

        fw.write("setRadNetDate(\'" + lastModified.toString() + "\');\n");
        
        logger.info("# of lines: " + lines);
        
        geo.save("/work/survive/cache/geocache");
        
        fw.close();
    }
    
    
    public static void main(String[] args) {
        try {
            new BuildRadNet("/work/survive/cache/RadNet.csv", "/work/survive/data/RadNet");
        } catch (Exception ex) {
            Logger.getLogger(BuildRadNet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
