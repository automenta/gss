/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.data.osm;

import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author seh
 */
abstract public class OSMDocument extends DefaultHandler {

    double minLat = 19.22;
    double minLon = -99.08;
    double maxLat = 19.24;
    double maxLon = -99.10;
    public double lat, lon;


    public OSMDocument(String filePath) {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            final BZip2CompressorInputStream bz = new BZip2CompressorInputStream(new FileInputStream(filePath));

            saxParser.parse(bz, this);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
