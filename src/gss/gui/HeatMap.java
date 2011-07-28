/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gov.nasa.worldwind.examples.analytics.AnalyticSurface;
import gov.nasa.worldwind.examples.analytics.AnalyticSurface.GridPointAttributes;
import gov.nasa.worldwind.examples.analytics.AnalyticSurfaceAttributes;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author seh
 */
public class HeatMap {
    
    final RenderableLayer heatmapLayer = new RenderableLayer();
    private final MapPanel map;
    private final AnalyticSurfaceAttributes asa;

    int gridWidth = 20;
    int gridHeight = 20;                
    double elevation = 20000;
    double verticalScale = 100000.0; //TODO make this dependent on eyePos.getAltitude()
    double maxRadius = 2000000;

    public HeatMap(MapPanel map) {
        super();
        
        asa = new AnalyticSurfaceAttributes();
        asa.setInteriorOpacity(0.5);
        asa.setDrawShadow(false);
        asa.setDrawOutline(false);
        
        this.map = map;
        
        map.insertBeforeCompass(heatmapLayer);
        
        int updatePeriodMS = 500;
        
        new Timer(updatePeriodMS, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    updateHeatMap();
            }
            
        }).start();

    }
    
    protected void updateHeatMap() {
        int cx = map.getWwd().getWidth()/2;
        int cy = map.getWwd().getHeight()/2;

        Position eyePos = map.getWwd().getView().getCurrentEyePosition();
        Position centerPos = map.getWwd().getView().computePositionFromScreenPoint(cx, cy);
        if (centerPos == null) {
            return;
        }
        
        
        double radius = map.getWwd().getView().computePixelSizeAtDistance(eyePos.getAltitude()) * Math.min(cx, cy);
        radius = Math.min(radius, maxRadius);
        
//        Angle centerLat = centerPos.latitude;
//        Angle centerLon = centerPos.longitude;
//        
//        Angle minLat = centerLat.add(Angle.fromDegreesLatitude(-heatmapAngle));
//        Angle maxLat = centerLat.subtract(Angle.fromDegreesLatitude(heatmapAngle));
//        Angle minLon = centerLon.add(Angle.fromDegreesLongitude(-heatmapAngle));
//        Angle maxLon = centerLon.subtract(Angle.fromDegreesLongitude(heatmapAngle));
//        
//        System.out.println(minLat + ".." + maxLat + " :: " + minLon + ".." + maxLon);
        
        heatmapLayer.removeAllRenderables();

        Sector sector = Sector.boundingSector(map.getWwd().getModel().getGlobe(), centerPos, radius);
        
        //TODO cache AnalyticSurface so it doesnt need recalculated
        AnalyticSurface as = new AnalyticSurface(sector, elevation, gridWidth, gridHeight);
        as.setValues(getHeatmapValues(gridWidth * gridHeight));
        as.setVerticalScale(verticalScale);
        as.setClientLayer(heatmapLayer);
        as.setSurfaceAttributes(asa);
                
        heatmapLayer.addRenderable(as);
        
    }
    private List<GridPointAttributes> getHeatmapValues(int num) {
        List<GridPointAttributes> l = new LinkedList();
        for (int i = 0; i < num; i++)  {
            l.add(AnalyticSurface.createColorGradientAttributes(Math.random(), 0.0, 1.0, 0.2, 0.9));
            
//            float x = (float)Math.random();
//            float hue = x;
//            float sat = 0.7f;
//            float bri = 0.7f;
//            float[] v = Color.getHSBColor(hue, sat, bri).getColorComponents(null); 
//            Color c = new Color(v[0], v[1], v[2], 0.25f);                        
//            l.add(AnalyticSurface.createGridPointAttributes(x, c));
        } 
        return l;
    }
   
}
