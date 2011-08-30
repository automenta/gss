/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.osm;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author seh
 */
public class OSMXmlToAmenitiesCSV extends OSMDocument {

    public OSMXmlToAmenitiesCSV(String filePath) {
        super(filePath);
    }
    
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

//                  System.out.println(localName + " " + qName + " " + attributes.getLength());

        if (qName.equalsIgnoreCase("node")) {

            lat = Double.parseDouble(attributes.getValue("lat"));
            lon = Double.parseDouble(attributes.getValue("lon"));

            //TODO timestamp


//                        for (int i = 0; i < attributes.getLength(); i++) {
//                            String key = attributes.getQName(i);
//                            String value = attributes.getValue(i);
//                            System.out.println("  " + key + " " + value);
//                        }
        }

        //if ((lat >= minLat) && (lat <= maxLat) && (lon >= minLon) && (lon <= maxLon)) {
        if (qName.equalsIgnoreCase("tag")) {
            //System.out.println(node.keySet().toString() + " " + attributes.keySet().toString());

            if (attributes.getValue("k").equalsIgnoreCase("amenity")) {
                System.out.println(lat + ", " + lon + "," + attributes.getValue("v"));
            }
        }
        //}


        //System.out.println(bz.getBytesRead());

    }

    public static void main(String[] args) {
        new OSMXmlToAmenitiesCSV("/home/me/Downloads/mexico.osm.bz2");
    }
    
}
