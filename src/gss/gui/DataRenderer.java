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
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.GeographicText;
import gov.nasa.worldwind.render.GeographicTextRenderer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.render.SurfaceIcon;
import gov.nasa.worldwind.render.UserFacingText;
import gss.Data;
import gss.data.DataPoints;
import gss.Event;
import gss.Event.RadialEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    public final RenderableLayer layer;
    private List<GeographicText> texts = new CopyOnWriteArrayList();
    ShapeAttributes normalAttributes = new BasicShapeAttributes();
    ShapeAttributes highlightAttributes = new BasicShapeAttributes(normalAttributes);
    
    public void addCylinder(Position pos, double height, double radius) {
        if (height <= 0) height = 1.0;
        
        Cylinder c = new Cylinder(pos, height, radius);
        
        c.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        c.setAttributes(normalAttributes);
        //c.setHighlightAttributes(highlightAttributes);
        c.setDetailHint(-0.5);
        layer.addRenderable(c);
    }
    
    public void addSurfaceIcon(Position pos, String pathToIcon) {
        SurfaceIcon sc = new SurfaceIcon(pathToIcon, pos);
        layer.addRenderable(sc);
    }

    public void addLabel(Position pos, String label) {
        UserFacingText t = new UserFacingText(label, pos);
        texts.add(t);        
    }
    
    public SurfaceCircle addSurfaceCircle(Position pos, double radius) {
        
        SurfaceCircle c = new SurfaceCircle(pos, radius);
        c.setAttributes(normalAttributes);
        //c.setHighlightAttributes(highlightAttributes);
        
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
        
        final GeographicTextRenderer textRenderer = new GeographicTextRenderer();
        textRenderer.setCullTextEnabled(true);;
        textRenderer.setEffect(AVKey.TEXT_EFFECT_NONE);
        
        layer = new RenderableLayer() {

            @Override
            public void render(DrawContext dc) {
                super.render(dc);
                
                if (layer.isEnabled())
                    textRenderer.render(dc, texts);                
            }
            
        };
        this.source = source;
    }

    public RenderableLayer getLayer() {
        return layer;
    }
    
    public void update() {
        layer.removeAllRenderables();
        texts.clear();
        render();
    }
    
    abstract public void render();
    
    public static class ShadedCircleRenderer extends DataRenderer<DataPoints> {
        private double scale;
        private double minScale, maxScale;
                
        public ShadedCircleRenderer(DataPoints source, double scale, double minScale, double maxScale) {
            super(source);
            
            this.scale = scale;
            this.minScale = minScale;
            this.maxScale = maxScale;            
            
        }

        public void setScale(double scale) {
            if (scale!=this.scale) {
                this.scale = scale;
                update();
            }
        }

        public double getScale() {
            return scale;
        }

        public double getMinScale() {
            return minScale;
        }

        public double getMaxScale() {
            return maxScale;
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
                    
                    float hue = ((float)source.id.hashCode())/100.0f;
                    float saturation = 0.7f + ((float)m)*0.3f;
                    float brightness = saturation;
                    
                    Color c = Color.getHSBColor(hue, saturation, brightness);
                    a.setInteriorMaterial(new Material(c));
                    sc.setAttributes(a); 
                    //re.getMeasurement()*5000.0, 
                    
                    if (source.drawIcons())
                        addSurfaceIcon(re.getCenter(), ControlPanel.getIconPath(source.iconURL));
                    if (source.drawLabels())
                        addLabel(re.getCenter(), re.getLabel());
                }
            }
        }        
        
        
    }
}
