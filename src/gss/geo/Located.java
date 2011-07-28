/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.geo;

import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
abstract public class Located implements Serializable {

    final static Logger logger = Logger.getLogger(Located.class.toString());
    
    public double latitude, longitude;

    
    public Located(String query) throws Exception {
        geocode(query);        
    }
    
    /**
     * Sets latitude and longitude or throws Exception
     * @param query 
     */
    abstract public void geocode(String query) throws Exception;
    
}
