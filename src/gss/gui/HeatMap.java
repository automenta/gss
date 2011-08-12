/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.examples.analytics.AnalyticSurface;
import gov.nasa.worldwind.examples.analytics.AnalyticSurface.GridPointAttributes;
import gov.nasa.worldwind.examples.analytics.AnalyticSurfaceAttributes;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class HeatMap {
    final static int MAX_DIVISIONS = 64;
    
    final RenderableLayer heatmapLayer = new RenderableLayer();
    private final MapPanel map;
    private final AnalyticSurfaceAttributes asa;
    
    int divisions = 2;
    int maxDivisions = 32;
    int minDivisions = 5;
    
    double elevation = 100000;
    double verticalScale = 100000.0; //TODO make this dependent on eyePos.getAltitude()
    double maxRadius = 2000000;
    private double opacity = 0.5;
    private Position lastCenterPos, lastEyePos;
    private double lastOpacity;
    private int lastDivisions;
    private Sector sector;
    private Sector[] subdividedSector;
    private Position[][] gridPositions;
    private boolean interestsChanged = true;
    private List<GridPointAttributes> heatMapIntensities;
    private ArrayList<Double> intensities;
    private boolean running = true;
    int updatePeriodMS = 30;
    private boolean flat = true;

//    public static void printTimeDifference(String label, long b1, long b2) {
//        final double diff = ((double)(b2 - b1)) / 1.0e9;
//        System.out.println(label + " " + diff);
//    }
    
    public HeatMap(final MapPanel map) {
        super();
        
        asa = new AnalyticSurfaceAttributes();
        asa.setDrawShadow(true);
        asa.setDrawOutline(true);
        asa.setInteriorOpacity(opacity);                

        this.map = map;

        map.insertBeforeCompass(heatmapLayer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    update();
                    
                    map.redraw();
                    try {
                        Thread.sleep(updatePeriodMS);
                    } catch (InterruptedException ex) {
                    }
                }
            }            
        }).start();
    }

    public void setDivisions(int divisions) {
        if (divisions < 2) divisions = 2;
        if (divisions > MAX_DIVISIONS) divisions = MAX_DIVISIONS;
        this.divisions = divisions;        
    }

    /** call this to trigger a recalculation of grid intensities */
    public void setInterestsChanged() {
        interestsChanged = true;
    }
    
    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double o) {
        this.opacity = o;
        asa.setInteriorOpacity(o);        
        update();
    }

    public RenderableLayer getLayer() {
        return heatmapLayer;
    }

    protected synchronized void update() {
        if (!heatmapLayer.isEnabled()) {
            return;
        }

        int cx = map.getWwd().getWidth() / 2;
        int cy = map.getWwd().getHeight() / 2;

        Position eyePos = map.getWwd().getView().getCurrentEyePosition();
        Position centerPos = map.getWwd().getView().computePositionFromScreenPoint(cx, cy);
        if (centerPos == null) {
            return;
        }

        boolean regionChanged;
        if (lastCenterPos!=null) {
            regionChanged = (/*(!lastEyePos.equals(eyePos)) ||*/ (!lastCenterPos.equals(centerPos)));                        
        }
        else
            regionChanged = true;
        
        if (!regionChanged) {
            if (divisions < maxDivisions) {
                //scale divisions exponentially as refined
                divisions = (int)(((double)divisions) * 1.5);
            }
        }
        else {
            divisions = minDivisions;
        }
        if (divisions > maxDivisions) divisions = maxDivisions;
        
        boolean opacityChanged = (opacity!=lastOpacity);
        boolean divisionsChanged = (divisions!=lastDivisions);

        if ((regionChanged) || (divisionsChanged)) {
            double radius = map.getWwd().getView().computePixelSizeAtDistance(eyePos.getAltitude()) * Math.min(cx, cy);
            radius = Math.min(radius, maxRadius);

            sector = Sector.boundingSector(map.getWwd().getModel().getGlobe(), centerPos, radius);

            subdividedSector = sector.subdivide(divisions);

            //subdivisions is ordered through each column,then each row

            //but..

            //AnalyticSurface.GridPointAttributes Grid points are assigned attributes from this iterable starting
            //at the upper left hand corner, and proceeding in row-first order across the grid. 
            //The iterable should contain at least width * height values, where width and height are the AnalyticSurface'subdividedSector grid dimensions. If the iterable contains too few values, the unassigned grid points are given default attributes: the default scalar value is 0, and the default color is Color.BLACK. (totally opaque). 

            gridPositions = new Position[divisions][divisions];
            intensities = new ArrayList(divisions*divisions);

            int idx = 0;
            for (int row = 0; row < divisions; row++) {
                for (int col = 0; col < divisions; col++) {
                    final Sector x = subdividedSector[idx++];

                    final LatLon c = x.getCentroid();

                    gridPositions[row][col] = new Position(c, map.getWwd().getModel().getGlobe().getElevation(c.latitude, c.longitude));
                }
            }
        
            interestsChanged = true;
        }

        if (interestsChanged) {
            heatMapIntensities = getHeatmapValues(sector, divisions);
            interestsChanged = false;
        }
        
        //TODO cache AnalyticSurface so it doesnt need recalculated
        AnalyticSurface as = new AnalyticSurface(sector, elevation, divisions, divisions) {

            @Override
            public void render(DrawContext dc) {
                try {
                    super.render(dc);
                }
                catch (NullPointerException ne) {
                    //????
                }
            }
            
        };

        if (isFlat()) {
            as.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);            
        }
        else {
            as.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);           
        }
        
        as.setVerticalScale(verticalScale);
        //as.setClientLayer(heatmapLayer);

        as.setSurfaceAttributes(asa);
        
        as.setValues(heatMapIntensities);
        

        heatmapLayer.removeAllRenderables();
        heatmapLayer.addRenderable(as);

        this.lastCenterPos = centerPos;
        this.lastEyePos = eyePos;
        this.lastOpacity = opacity;
        this.lastDivisions = divisions;
    }

    public boolean isFlat() {
        return flat;
    }
    
    private List<GridPointAttributes> getHeatmapValues(final Sector sector, final int divisions) {
        
        double min=0, max=0;
        intensities.clear();
        for (int col = 0; col < divisions; col++) {
            for (int row = 0; row < divisions; row++) {
                final Position p = gridPositions[(divisions-1)-col][row];
                
                double i = map.computeIntensity(p);
                
                intensities.add(i);
                if ((row == 0) && (col == 0)) {
                    min = max = i;
                }
                else {
                    if (i < min) min = i;
                    if (i > max) max = i;
                }
            }
        }

        List<GridPointAttributes> l = new LinkedList();

        for (int i = 0; i < divisions*divisions; i++) {
            double d = intensities.get(i);
            float e = (float)((d - min) / (max-min));

            float hue = (1.0f-e)/2.5f;
            float sat = 0.75f + 0.25f * e;
            float bri = 0.7f;
            float[] v = Color.getHSBColor(hue, sat, bri).getColorComponents(null); 
            Color c = new Color(v[0], v[1], v[2], 0.75f + 0.25f * e);                        
            l.add(AnalyticSurface.createGridPointAttributes(e, c));            

            //l.add(AnalyticSurface.createColorGradientAttributes(d, min, max, 0, 0.2));
        
        }
        return l;
    }
    
    @Deprecated private List<GridPointAttributes> getRandomHeatmapValues(int num) {
        List<GridPointAttributes> l = new LinkedList();
        for (int i = 0; i < num; i++)  {
            l.add(AnalyticSurface.createColorGradientAttributes(Math.random(), 0.0, 1.0, 0.0, 0.3));
            
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
