/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class Geocode implements Serializable {

    final static Logger logger = Logger.getLogger(Geocode.class.toString());
    final static int BUFFERSIZE = 256;
    public double latitude, longitude;

    public Geocode(String query) throws Exception {
        // Execute command
        String[] command = new String[]{"python2.6", "/work/survive/scripts/geocode.py", "\"" + query + "\""};
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

        String o[] = output.split(",");


        if (o.length < 2) {
            throw new RuntimeException("Error");
        }

        latitude = Double.valueOf(o[0]);
        longitude = Double.valueOf(o[1]);

        in.close();
    }

    public static void main(String[] args) {
        try {
            Geocode g = new Geocode("Des Moines, IA");
            System.out.println(g.latitude + " " + g.longitude);
        } catch (Exception ex) {
            Logger.getLogger(Geocode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
