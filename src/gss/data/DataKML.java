/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data;

import gov.nasa.worldwind.ogc.kml.KMLRoot;

/**
 *
 * @author seh
 */
public class DataKML extends DataPointsList {
    public KMLRoot root;

    public DataKML(String kmlFile, String name, String category, String iconURL, String unit) {
        super(name, category, iconURL, unit);
        
        try {           
            root = KMLRoot.createAndParse(kmlFile); 
            //System.out.println(r.getFields().getEntries());
            //System.out.println(r.getFeature().getFields().getEntries());
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public static void main(String[] args) {
        System.out.println(new DataKML("/work/survive/cache/epa_airquality.kml", "Air Quality", "Pollution", "icon.xyz", "Units"));
    }
    
}
