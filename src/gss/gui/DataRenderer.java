/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Cylinder;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
import gss.Data;
import gss.data.DataPoints;
import gss.Event;
import gss.Event.RadialEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author seh
 */
public abstract class DataRenderer<D extends Data> {

    final public D source;
    //double importance
    //boolean includeInHeatmap
    //boolean preNormalize
    //boolean showIcon
    //boolean showPerimeter
    //Color fill
    //Color stroke    
    public final RenderableLayer layer = new RenderableLayer();
    ShapeAttributes normalAttributes = new BasicShapeAttributes();
    ShapeAttributes highlightAttributes = new BasicShapeAttributes(normalAttributes);
    
    public void addCylinder(Position pos, double height, double radius) {
        if (height <= 0) height = 1.0;
        
        Cylinder c = new Cylinder(pos, height, radius);
        
        c.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        c.setAttributes(normalAttributes);
        c.setHighlightAttributes(highlightAttributes);
        c.setDetailHint(-0.5);
        layer.addRenderable(c);
    }
    
    public SurfaceCircle addSurfaceCircle(Position pos, double radius) {
        
        SurfaceCircle c = new SurfaceCircle(pos, radius);
        
        c.setAttributes(normalAttributes);
        c.setHighlightAttributes(highlightAttributes);
        layer.addRenderable(c);
        return c;
    }
    
    public void addSquare(double lat, double lng, double width, double height) {
        final double z = 8e4;

        ArrayList<Position> pathPositions = new ArrayList<Position>();
        pathPositions.add(new Position(Angle.fromDegrees(lat - width / 2.0), Angle.fromDegrees(lng - height / 2.0), z));
        pathPositions.add(new Position(Angle.fromDegrees(lat + width / 2.0), Angle.fromDegrees(lng - height / 2.0), z));
        pathPositions.add(new Position(Angle.fromDegrees(lat + width / 2.0), Angle.fromDegrees(lng + height / 2.0), z));
        pathPositions.add(new Position(Angle.fromDegrees(lat - width / 2.0), Angle.fromDegrees(lng + height / 2.0), z));
        pathPositions.add(new Position(Angle.fromDegrees(lat - width / 2.0), Angle.fromDegrees(lng - height / 2.0), z));
        Polygon pgon = new Polygon(pathPositions);
        pgon.setValue(AVKey.DISPLAY_NAME, "A");

        pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        pgon.setAttributes(normalAttributes);
        pgon.setHighlightAttributes(highlightAttributes);
        layer.addRenderable(pgon);
    }

    public DataRenderer(D source) {
        super();
        this.source = source;
    }

    public RenderableLayer getLayer() {
        return layer;
    }
    
    public void update() {
        layer.removeAllRenderables();
        render();
    }
    
    abstract public void render();
    
    public static class CylinderDataRenderer extends DataRenderer<DataPoints> {
        private double scale;
                
        public CylinderDataRenderer(DataPoints source, double scale) {
            super(source);
            this.scale = scale;
            
        }

        @Override
        public void render() {
            
            Iterator<Event> ie = source.iterateEvents();
            while (ie.hasNext()) {
                Event e = ie.next();
                if (e instanceof RadialEvent) {
                    RadialEvent re = (RadialEvent)e;
                    
                    //addCylinder(re.getCenter(), re.getMeasurement()*5000.0, re.getRadius() * scale);                    
                    
                    double m = source.getNormalizedMeasurement(re);
                    SurfaceCircle sc = addSurfaceCircle(re.getCenter(), re.getRadius() * scale * (m + 0.5));
                    
                    BasicShapeAttributes a = new BasicShapeAttributes();
                    a.setDrawOutline(false);
                    a.setEnableAntialiasing(false);
                    a.setInteriorOpacity(0.8);
                    Color c = new Color(0.7f + ((float)m)*0.3f, 0.25f, 0.25f);
                    a.setInteriorMaterial(new Material(c));
                    sc.setAttributes(a); 
                    //re.getMeasurement()*5000.0, 
                }
            }
        }        
        
        
    }
}
