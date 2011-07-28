/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gss.gui.example;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.ApplicationTemplate;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;

import java.util.ArrayList;

/**
 * Example of {@link Polygon} usage. Sets material, opacity and other attributes. Sets rotation and other properties.
 * Adds an image for texturing. Shows a dateline-spanning Polygon.
 *
 * @author tag
 * @version $Id: Polygons.java 15656 2011-06-17 00:51:53Z pabercrombie $
 */
public class Polygons2 extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        
        RenderableLayer layer = new RenderableLayer();
        ShapeAttributes normalAttributes = new BasicShapeAttributes();
        ShapeAttributes highlightAttributes = new BasicShapeAttributes(normalAttributes);
        
        public void addCell(double lat, double lng, double width, double height) {
            final double z = 8e4;
            
            ArrayList<Position> pathPositions = new ArrayList<Position>();
            pathPositions.add(new Position(Angle.fromDegrees(lat-width/2.0), Angle.fromDegrees(lng-height/2.0), z));
            pathPositions.add(new Position(Angle.fromDegrees(lat+width/2.0), Angle.fromDegrees(lng-height/2.0), z));
            pathPositions.add(new Position(Angle.fromDegrees(lat+width/2.0), Angle.fromDegrees(lng+height/2.0), z));
            pathPositions.add(new Position(Angle.fromDegrees(lat-width/2.0), Angle.fromDegrees(lng+height/2.0), z));
            pathPositions.add(new Position(Angle.fromDegrees(lat-width/2.0), Angle.fromDegrees(lng-height/2.0), z));
            Polygon pgon = new Polygon(pathPositions);
            pgon.setValue(AVKey.DISPLAY_NAME, "A");

            pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            pgon.setAttributes(normalAttributes);
            pgon.setHighlightAttributes(highlightAttributes);
            layer.addRenderable(pgon);            
        }
        
        public AppFrame()
        {
            super(true, true, false);


            // Create and set an attribute bundle.
            normalAttributes.setInteriorMaterial(Material.YELLOW);
            normalAttributes.setOutlineOpacity(0.6);
            normalAttributes.setInteriorOpacity(0.3);
            normalAttributes.setOutlineMaterial(Material.GREEN);
            normalAttributes.setOutlineWidth(2);
            normalAttributes.setDrawOutline(true);
            normalAttributes.setDrawInterior(true);
            normalAttributes.setEnableLighting(true);

            highlightAttributes.setOutlineMaterial(Material.WHITE);
            highlightAttributes.setOutlineOpacity(1);

            double w = 2;
            double h = 2;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    addCell(i*w, j*h, w, h);
                }
            }


            // Add the layer to the model.
            insertBeforeCompass(getWwd(), layer);

            // Update layer panel
            this.getLayerPanel().update(this.getWwd());
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Extruded Polygons", AppFrame.class);
    }
}