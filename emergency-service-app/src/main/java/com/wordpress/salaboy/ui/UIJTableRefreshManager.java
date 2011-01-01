/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;

/**
 *
 * @author salaboy
 */
public class UIJTableRefreshManager {
    
  

    
    public static void start(final JCheckBox selected, final Integer refreshTime, final Refreshable refreshable){
        Thread refreshThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(selected.isSelected()){
                    try {
                        Thread.sleep(refreshTime * 1000);
                        refreshable.refresh();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UIJTableRefreshManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        refreshThread.start();
    }
    
}
