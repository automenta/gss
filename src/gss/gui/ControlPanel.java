/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gss.Data;
import gss.Environment;
import java.awt.BorderLayout;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

    public static ImageIcon getIcon(String s, int w, int h) throws MalformedURLException {
        final URL u = new File("./media/icons/" + s).toURL();
        ImageIcon ii = new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        return ii;
    }
        
    public static class DataSourcePanel extends JPanel {

        public DataSourcePanel(Data ds) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            
            JLabel l = new JLabel(ds.name);
            try {
                l.setIcon(getIcon(ds.iconURL, datasourceIconWidth, datasourceIconHeight));
            } catch (MalformedURLException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            add(l);
        }
        
    }
    
    public ControlPanel(MapPanel mp, Environment d) {
        super(new BorderLayout());

        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.PAGE_AXIS));

        for (String s : d.categories) {
            JPanel c = new JPanel();
            c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

            final JPanel cSub = new JPanel();
            int indent = 15;
            int spacing = 15;
            cSub.setBorder(new EmptyBorder(0, indent, spacing, 0));
            cSub.setLayout(new BoxLayout(cSub, BoxLayout.PAGE_AXIS));
            {
                for (Data ds : d.getSources(s)) {
                    cSub.add(new DataSourcePanel(ds));                    
                }
            }
            //cSub.add(new JLabel("options"));
            
            cSub.setVisible(false);


            JButton jb = new JButton(s);

            try {
                ImageIcon ii = getIcon(d.categoryIcon.get(s), categoryImageWidth, categoryImageHeight);
                jb.setIcon(ii);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            jb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    cSub.setVisible(!cSub.isVisible());
                }
            });

            c.add(jb, BorderLayout.NORTH);
            c.add(cSub, BorderLayout.SOUTH);
            c.doLayout();

            categoriesPanel.add(c);
        }

        categoriesPanel.add(Box.createVerticalBox());

        add(categoriesPanel, BorderLayout.CENTER);

        
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
