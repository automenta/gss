/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.geo;

import gss.geo.Located;
import gss.geo.PythonLocated;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Caches the geo-coordinates of named geographic locations: String -> Located
 * Serializable to file
 * @author seh
 */
public class LocationsCache implements Serializable {
    private static final Logger logger = Logger.getLogger(LocationsCache.class.toString());
    
    Map<String, Located> locations = new HashMap();
    
    public void load(String path)  {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            LocationsCache gc = (LocationsCache)ois.readObject();
            locations = gc.locations;
            logger.info("Loaded: " + locations.size() + " entries");
        }
        catch (Exception e) {
            //use the empty
            logger.info("Unable to load GeoCache at: " + path + " , using empty cache");
        }
        
    }
    
    public void save(String path) throws IOException {
        OutputStream os = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(this);
        oos.close();
    }
    
    public Located get(String query) {
        if (locations.containsKey(query)) {
            return locations.get(query);
        }
        try {
            Located g = new PythonLocated(query);
            locations.put(query, g);
            return g;
        }
        catch (Exception e) {
            locations.put(query, null);
            logger.severe(e.toString());
            return null;
        }                
    }
}
