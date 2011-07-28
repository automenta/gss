/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.geo;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class PythonLocated extends Located {
    final static Logger logger = Logger.getLogger(PythonLocated.class.toString());

    static final int BUFFERSIZE = 256;
    static final String pythonGeocodePath = "/work/survive/gss/python/geocode.py";

    public PythonLocated(String query) throws Exception {
        super(query);
    }

    @Override
    public void geocode(String query) throws Exception {
        // Execute command
        String[] command = new String[]{"python2.6", pythonGeocodePath, "\"" + query + "\""};
        Process child = Runtime.getRuntime().exec(command);
        child.waitFor();
        // Get the input stream and read from it
        InputStream in = child.getInputStream();
        byte[] buffer = new byte[BUFFERSIZE];
        in.read(buffer);
        String output = new String(buffer).trim();
        logger.info("Geocoded: " + query + " -> " + output);
        if (output == null) {
            throw new RuntimeException("Null");
        }
        String[] o = output.split(",");
        if (o.length < 2) {
            throw new RuntimeException("Error");
        }
        latitude = Double.valueOf(o[0]);
        longitude = Double.valueOf(o[1]);
        in.close();
    }

    //Example
    public static void main(String[] args) {
        try {
            Located g = new PythonLocated("Des Moines, IA");
            System.out.println(g.latitude + " " + g.longitude);
        } catch (Exception ex) {
            Logger.getLogger(Located.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
