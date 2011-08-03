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
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gss.Data;
import gss.DataInterest;
import gss.data.DataPoints;
import gss.Environment;
import gss.Intensity;
import gss.data.DataKML;
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
            DataInterest di = getInterest(source);
            
            if (source instanceof DataKML) {
                addKMLLayer((DataKML)source);
            }
            else if (source instanceof DataPoints) {
                addDataRenderer(new ShadedCircleRenderer((DataPoints)source, di, 130.0, 0.01, 3000));
            }
        }
        
        heatMap = new HeatMap(this);        
    }
    
    protected void addKMLLayer(DataKML d) {
        RenderableLayer rl = new RenderableLayer();
        //TODO lazy-load and parse 'd' when rl becomes visible for first time
        KMLController kmlController = new KMLController(d.root);
        rl.addRenderable(kmlController);
        
        insertBeforeCompass(rl);
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

    DataInterest getInterest(Data ds) {        
        DataInterest di = dataInterest.get(ds);
        if (di == null) {
            di = new DataInterest(0.5, 130.0);
            dataInterest.put(ds, di);
        }
        return di;
    }
    
    
}
