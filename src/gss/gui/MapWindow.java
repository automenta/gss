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
import javax.swing.UIManager;

/**
 *
 * @author seh
 */
public class MapWindow {
    static {
        try {             
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e) {
        }
    }
    
   public static void main(String[] args) {

        final Environment env = new Environment();
        
        int mapWidth = 1000;
        int controlWidth = 350;
        int height = 850;
        
        final Dimension s = new Dimension(mapWidth, height);
        final MapPanel mp = new MapPanel(env, s);

        {
             ControlPanel cp = new ControlPanel(mp, env);           
             
             final JFrame jf = new JFrame("Control");
            
             jf.getContentPane().setLayout(new BorderLayout());                    

             jf.setSize(controlWidth, height);
             jf.getContentPane().add(cp);
             jf.getContentPane().doLayout();
             jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             jf.setVisible(true);
        }
        
        {
                       
            final JFrame jf = new JFrame("Global Survival System");
            
            jf.getContentPane().setLayout(new BorderLayout());                    

            jf.setSize(mapWidth, height);
            jf.getContentPane().add(mp);
            jf.getContentPane().doLayout();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setVisible(true);
           
        }
                    
                        
    }

   //Integrated ControlPanel into right-side of map
    public static void _main(String[] args) {
        final JFrame jf = new JFrame("Global Survival System");

        final Environment env = new Environment();
        
        int mapWidth = 1000;
        int controlWidth = 350;
        int height = 850;
        
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
