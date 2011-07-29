/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gov.nasa.worldwind.examples.ApplicationTemplate.AppPanel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gss.Data;
import gss.DataInterest;
import gss.data.DataPoints;
import gss.Environment;
import gss.Intensity;
import gss.gui.DataRenderer.ShadedCircleRenderer;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class MapPanel extends AppPanel {
    private final static Logger logger = Logger.getLogger(MapPanel.class.getName());
    
    private final Environment env;

    final Map<Data, DataRenderer> dataRenderers = new HashMap();
    final Map<Data, RenderableLayer> dataLayers = new HashMap();
    final Map<Data, DataInterest> dataInterest = new HashMap();
    private final HeatMap heatMap;
    
    public MapPanel(final Environment env, final Dimension d) {
        super(d, true);                        
        this.env = env;
        
        for (Data source : env.getSources()) {
            if (source instanceof DataPoints)
                addDataRenderer(new ShadedCircleRenderer((DataPoints)source, 130.0, 0, 300));
        }
        
        heatMap = new HeatMap(this);        
    }
    
    
    public void insertBeforeCompass(Layer layer)     {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = getWwd().getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }
    
    public void addDataRenderer(DataRenderer dr) {
        //TODO
        //dataRenderers.add(dr);
        
        dr.update();
        RenderableLayer layer = dr.getLayer();
        dataRenderers.put(dr.source, dr);
        dataLayers.put(dr.source, layer);
        insertBeforeCompass(layer);      
        
        //TODO hack remove this when GUI controls are implemented
        dataInterest.put(dr.source, new DataInterest(0.5));
    }
        
    public boolean isLayerEnabled(Data d) {
        Layer l = dataLayers.get(d);
        if (l!=null) {
            return l.isEnabled();
        }
        return false;        
    }
    
    public void setLayerEnabled(Data d, boolean enabled) {
        Layer l = dataLayers.get(d);
        if (l!=null) {
            l.setEnabled(enabled);
        }
        else {
            logger.severe("Layer not found for Data " + d);
        }
    }

    void redraw() {
        getWwd().redraw();
    }

    public HeatMap getHeatMap() {
        return heatMap;
    }

    /** computes the "intensity" for a location given current equalization tunings */
    public double computeIntensity(final Position p) {
        return Intensity.getIntensity(getWwd().getModel().getGlobe(), p, dataInterest);
        
    }
    
    
}
