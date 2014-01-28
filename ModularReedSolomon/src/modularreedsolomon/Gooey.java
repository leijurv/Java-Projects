/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modularreedsolomon;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 *
 * @author leijurv
 */
public class Gooey {
    JFrame frame;
    JTabbedPane t;
    EncodePane e=new EncodePane();
    DecodePane d=new DecodePane();
    public Gooey(){
        t=new JTabbedPane();
        t.add("Encode",e);
        t.add("Decode",d);
        frame=new JFrame("RS error correction!");
        frame.setContentPane(t);
          
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
    }
}
