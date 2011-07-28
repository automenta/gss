/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.radnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import gss.geo.LocationsCache;
import gss.geo.Located;

/**
 *
 * @author seh
 */
public class BuildUSBqM3 {
    //http://data.un.org/Data.aspx?d=UNODC&f=tableCode%3a1
    //Rate is in Murders per 100,000 people
    
    LocationsCache geo = new LocationsCache();
    private static final Logger logger = Logger.getLogger(BuildUSBqM3.class.toString());

    Map<String, Integer> countryYear = new HashMap();
    Map<String, Double> countryRate = new HashMap();
    
    public BuildUSBqM3(String inputCSVpath, String outputPathAndPrefix) throws Exception {
        
        geo.load("/work/survive/cache/geocache");
        
        File f = new File(inputCSVpath);

        BufferedReader reader = new BufferedReader(new FileReader(f));
        
        String header = reader.readLine();
        String[] headerTokens = header.split(",");

        logger.info(header);
                
        int lines = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            
            String[] tokens = line.split(",");
            
            if (tokens.length < 3)
                break;
            
            String city = tokens[1].trim();
            String date = tokens[2].trim();
            double rate = Double.parseDouble(tokens[0].trim());
            String isotope = tokens[3].trim();
            
            System.out.println(isotope + " " + city + " " + rate + " " + date);
            
//            Integer previousYear = countryYear.get(country);
//            if (previousYear==null) {
//                countryYear.put(country, year);
//                countryRate.put(country, rate);
//            }
//            else if (previousYear < year) {
//                countryYear.put(country, year);
//                countryRate.put(country, rate);                
//            }
//            else if (previousYear == year) {
//                double previousRate = countryRate.get(country);
//                
//                //use the highest value
//                if (previousRate < rate) {
//                    countryRate.put(country, rate);
//                }
//            }
            
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
        /*
        File js = new File(outputPathAndPrefix + ".js");
        FileWriter fw = new FileWriter(js);
        fw.close();
        
        */

        geo.save("/work/survive/cache/geocache");
        
        
    }
    
    public static void main(String[] args) {
        try {
            new BuildUSBqM3("/work/survive/cache/bqm3date2.csv", "/work/survive/usbq");
        } catch (Exception ex) {
            Logger.getLogger(BuildUSBqM3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
