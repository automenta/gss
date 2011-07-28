/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss.gui;

import gss.Environment;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 *
 * @author seh
 */
public class MapWindow {

   public static void main(String[] args) {
        final JFrame jf = new JFrame("Global Survival System");

        final Environment env = new Environment();
        
        int mapWidth = 800;
        int controlWidth = 200;
        int height = 600;
        
        final Dimension s = new Dimension(mapWidth, height);
        final MapPanel mp = new MapPanel(env, s);
        
        {
            JSplitPane js = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                       
            ControlPanel cp = new ControlPanel(mp, env);           
            
            js.setLeftComponent(mp);
            js.setRightComponent(cp);
           
            jf.getContentPane().setLayout(new BorderLayout());                    
            jf.getContentPane().add(js, BorderLayout.CENTER);
            jf.setSize(mapWidth + controlWidth, height);

            js.setDividerLocation(mapWidth + 10);
            
            jf.getContentPane().doLayout();
        }
                    
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
                        
    }

}
