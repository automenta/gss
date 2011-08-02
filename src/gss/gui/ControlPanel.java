/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gss.Data;
import gss.DataInterest;
import gss.Environment;
import gss.gui.DataRenderer.ShadedCircleRenderer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author seh
 */
public class ControlPanel extends JPanel {

    JPanel configPanel = new JPanel();
    Map<String, Boolean> categoryVisible = new HashMap();
    final static int categoryImageWidth = 40;
    final static int categoryImageHeight = 40;
    final static int datasourceIconWidth = 25;
    final static int datasourceIconHeight = 25;

    public static String getIconPath(String s) {
        return "./media/icons/" + s;
    }

    public static ImageIcon getIcon(String s, int w, int h) throws MalformedURLException {
        final URL u = new File(getIconPath(s)).toURL();
        ImageIcon ii = new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        return ii;
    }

    public static class JFloatSlider extends JSlider {

        public final static int MAXRESOLUTION = 1000;
        private final double maxValue;
        private final double minValue;
        private double dvalue;

        public JFloatSlider(double value, double maxValue, double minValue, int orientation) {
            super(orientation, 0, MAXRESOLUTION, 0);

            setName("X");
            this.maxValue = maxValue;
            this.minValue = minValue;

            setDoubleValue((double) value);
        }

        public int doubleToInt(double v) {
            if (v > maxValue) {
                v = maxValue;
            }
            if (v < minValue) {
                v = minValue;
            }

            return (int) ((v - minValue) / (maxValue - minValue) * ((double) MAXRESOLUTION));
        }

        public void setDoubleValue(double v) {
            this.dvalue = v;
            setValue(doubleToInt(dvalue));
        }

        public double value() {
            int v = getValue();
            return (1.0 - ((double) v) / ((double) MAXRESOLUTION)) * (maxValue - minValue) + minValue;
        }
    }
    private final MapPanel map;

    public class HeatmapPanel extends JPanel {

        public HeatmapPanel() {
            super();

            BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
            setLayout(bl);

            setAlignmentX(LEFT_ALIGNMENT);

            JPanel ep = new JPanel(new FlowLayout());

            final HeatMap heatmap = map.getHeatMap();
            
            boolean layerEnabled = heatmap.getLayer().isEnabled();

            final JToggleButton showEvents = new JToggleButton("Analysis", layerEnabled);
            ep.add(showEvents);

            final JFloatSlider js = new JFloatSlider(heatmap.getOpacity(), 0, 1.0, JSlider.HORIZONTAL);
            js.setEnabled(layerEnabled);
            js.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            heatmap.setOpacity(js.value());
                            map.redraw();
                        }                        
                    });
                }
            });
            ep.add(js);

            showEvents.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    heatmap.getLayer().setEnabled(showEvents.isSelected());
                    js.setEnabled(showEvents.isSelected());
                }
            });

            ep.setAlignmentX(LEFT_ALIGNMENT);
            add(ep);
        }
    }

    public static class DataSourcePanel extends JPanel {

        public DataSourcePanel(final MapPanel map, final Data ds) {
            super();

            BoxLayout bl = new BoxLayout(this, BoxLayout.PAGE_AXIS);
            setLayout(bl);

            setAlignmentX(LEFT_ALIGNMENT);

            JLabel l = new JLabel(ds.name);
            try {
                l.setIcon(getIcon(ds.iconURL, datasourceIconWidth, datasourceIconHeight));
            } catch (MalformedURLException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            add(l);

            DataInterest di = map.getInterest(ds);
            DataRenderer dr = map.dataRenderers.get(ds);
            if (dr instanceof ShadedCircleRenderer) {
                final ShadedCircleRenderer scr = (ShadedCircleRenderer) dr;

                JPanel ep = new JPanel(new FlowLayout());

                boolean layerEnabled = map.isLayerEnabled(ds);

                final JToggleButton showEvents = new JToggleButton("Plot", layerEnabled);
                ep.add(showEvents);

                final JFloatSlider js = new JFloatSlider(di.getScale(), scr.getMinScale(), scr.getMaxScale(), JSlider.HORIZONTAL);
                js.setEnabled(layerEnabled);
                js.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                scr.setScale(js.value());
                                map.getHeatMap().setInterestsChanged();
                                map.redraw();
                            }
                        }).start();
                    }
                });
                ep.add(js);

                showEvents.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        map.setLayerEnabled(ds, showEvents.isSelected());
                        js.setEnabled(showEvents.isSelected());
                    }
                });

                ep.setAlignmentX(LEFT_ALIGNMENT);
                add(ep);

            }
        }
    }

    public ControlPanel(MapPanel mp, Environment d) {
        super(new BorderLayout());

        this.map = mp;

        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.PAGE_AXIS));
        
        HeatmapPanel hmp = new HeatmapPanel();
        categoriesPanel.add(hmp);

        for (String s : d.categories) {
            JPanel c = new JPanel();
            c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

            final JPanel cSub = new JPanel();
            int indent = 15;
            int spacing = 5;
            cSub.setBorder(new EmptyBorder(spacing, indent, spacing, 0));
            cSub.setLayout(new BoxLayout(cSub, BoxLayout.PAGE_AXIS));
            {

                for (Data ds : d.getSources(s)) {
                    DataSourcePanel dsp = new DataSourcePanel(map, ds);
                    dsp.setBorder(new EmptyBorder(spacing, 0, 0, 0));
                    cSub.add(dsp);
                }
            }


            JLabel jb = new JLabel(s);

            try {
                ImageIcon ii = getIcon(d.categoryIcon.get(s), categoryImageWidth, categoryImageHeight);
                jb.setIcon(ii);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }


            c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            c.add(jb, BorderLayout.NORTH);
            c.add(cSub, BorderLayout.CENTER);
            c.doLayout();

            c.setBorder(BorderFactory.createLoweredBevelBorder());

            categoriesPanel.add(c);
        }

        categoriesPanel.add(Box.createVerticalBox());

        add(new JScrollPane(categoriesPanel), BorderLayout.CENTER);


        JPanel presetsPanel = new JPanel(new BorderLayout());
        {
            JComboBox jc = new JComboBox();
            jc.addItem("Immediate Survival");
            jc.addItem("Hunteger-Gatherer");
            jc.addItem("Agriculture");
            jc.addItem("Outdoor Camping");
            jc.addItem("3rd World Urban");
            jc.addItem("1st World Urban");
            presetsPanel.add(jc, BorderLayout.CENTER);
        }
        add(presetsPanel, BorderLayout.NORTH);

        JPanel renderPanel = new JPanel(new BorderLayout());
        {
            JButton buyButton = new JButton("What may I need?");
            renderPanel.add(buyButton, BorderLayout.CENTER);
        }
        add(renderPanel, BorderLayout.SOUTH);

        doLayout();
    }
}
