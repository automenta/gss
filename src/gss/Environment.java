/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import gss.geo.LocationsCache;
import gss.data.DataEmpty;
import gss.data.iaea.NuclearFacilities;
import gss.data.osm.AmenitiesCSV;
import gss.data.un.HomicideRate;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class Environment {
    
    public final Set<String> categories = new HashSet();
    public final Map<String, String> categoryIcon = new HashMap();
    protected final List<Data> sources = new LinkedList();
    private final LocationsCache geo = new LocationsCache();

    public final String dataPath = "/work/survive/cache";
    
    public Environment() {
        super();
        
        geo.load(getDataFile("geocache"));
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    geo.save(getDataFile("geocache"));
                } catch (IOException ex) {
                    Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        }));
        
        addSource(new DataEmpty("radnetTotalIsotopes", "Nuclear Isotope Concentration", "Pollution", "atom.png", "Total Isotope Concentration, bQ/M^3"));
        
        addSource(new DataEmpty("earthquakesUSGS", "Earthquakes", "Natural Disasters", "quake.png", "Richter Magnitude"));

        addSource(new DataEmpty("lifeexpectancyWorldBank", "Life Expectancy", "Health", "people.png", "Years"));

        addSource(new HomicideRate(geo, getDataFile("UN_Homicide.csv") ));
        
        addSource(new NuclearFacilities(getDataFile("IAEA_Nuclear_Facilities.csv") ));
        
        addSource(new AmenitiesCSV("mexicoHospital", "Hospitals (Mexico)", "people.png", "Number", "/home/me/mexico_amenities.csv", "hospital"));
        addSource(new AmenitiesCSV("mexicoPharmacy", "Pharmacy (Mexico)", "people.png", "Number", "/home/me/mexico_amenities.csv", "pharmacy"));
        
        /*
            addSource(new DataKML("/work/survive/cache/epa_airquality.kml", "Air Quality", "Pollution", "icon.xyz", "Units"));
        */
      
    }
    
    public String getDataFile(String filename) {
        return dataPath + "/" + filename;
    }
    
    public void addSource(Data s) {
        categories.add(s.category);
        sources.add(s);
        if (categoryIcon.get(s.category) == null)
            categoryIcon.put(s.category, s.iconURL);
    }
    
    public List<Data> getSources(String category) {
        List<Data> l = new LinkedList();
        for (Data ds : sources) {
            if (ds.category.equals(category))
                l.add(ds);
        }
        return l;
    }

    public List<Data> getSources() {
        return sources;
    }
    
    
}
