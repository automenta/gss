/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.radnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import gss.geo.LocationsCache;
import gss.geo.Located;
import gss.data.radnet.BuildUSBqM3;

/**
 *
 * @author seh
 */
public class BuildUsBqM3 {
    //http://data.un.org/Data.aspx?d=UNODC&f=tableCode%3a1
    //Rate is in Murders per 100,000 people
    
    LocationsCache geo = new LocationsCache();
    private static final Logger logger = Logger.getLogger(BuildUSBqM3.class.toString());

    class Reading {
        public double lat, lng;
        public double rate;
        public String date;
        public String city;
    }
    
    Map<String, List<Reading>> cityReading = new HashMap();
    
    public BuildUsBqM3(String inputCSVpath, String outputPathAndPrefix) throws Exception {
        
        geo.load("/work/survive/cache/geocache");
        
        File f = new File(inputCSVpath);

        BufferedReader reader = new BufferedReader(new FileReader(f));
        
        reader.readLine();
        
        String header = reader.readLine();
        String[] headerTokens = header.split(",");     
        
        logger.info(header);
                
        int lines = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            
            String[] tokens = line.split(",");
            
            if (tokens.length < 3)
                break;
            
            String city = tokens[1].replace("\"", "").trim();
            String date = tokens[2].replace("\"", "").trim();
            double rate = Double.parseDouble(tokens[0].replace("\"", "").trim());
            String isotope = tokens[3].replace("\"", "").trim();
            
            Reading r = new Reading();
            r.city = city;
            r.date = date;
            r.rate = rate;
            
            Located g = geo.get(city /*+ ", United States"*/);
            r.lat = g.latitude;
            r.lng = g.longitude;                    
            
            System.out.println(isotope + " " + city + " " + rate + " " + date + " " + r.lat + ", " + r.lng);
            
            List<Reading> lr = cityReading.get(isotope);
            if (lr == null) {
                lr =new LinkedList();
                cityReading.put(isotope, lr);
            }
            lr.add(r);
            
            lines++;
        }
        
//        for (String c : countryYear.keySet()) {
//            int year = countryYear.get(c);
//            double rate = countryRate.get(c);
//            
//            
//            
//            final Located g = geo.get(c);
//            if (g == null) {
//                logger.severe("Unable to geolocate: " + c);
//                continue;
//            }
//            
//            
//            logger.info(c + " " + year + " " + rate);
//            fw.write("addHomicide(" + g.latitude + ", " + g.longitude + ", " + year + ", " + rate + ");");
//            fw.write("\n");
//        }
            
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
        for (String s : cityReading.keySet()) {
            
            String isoName = s.replace(" ", "");
            File js = new File(outputPathAndPrefix + isoName + ".js");
            
            FileWriter fw = new FileWriter(js);
            
            fw.write("setIsotope('" + s + "');");
            for (Reading r : cityReading.get(s)) {
                String l = "addReading('" + r.city + "', " + r.lat + ", " + r.lng + ", '" + r.date + "', " + r.rate + ");";
                fw.write(l);
            }
            
            fw.close();           
            
        }

        geo.save("/work/survive/cache/geocache");
        
        
    }
    
    public static void main(String[] args) {
        try {
            new BuildUsBqM3("/work/survive/cache/bqm3date2.csv", "/work/survive/usbq/");
        } catch (Exception ex) {
            Logger.getLogger(BuildUsBqM3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
