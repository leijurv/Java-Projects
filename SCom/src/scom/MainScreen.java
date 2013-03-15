/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scom;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author leif
 */
public class MainScreen extends JPanel{
    
MainPanel M=new MainPanel();
    public MainScreen(){
	
	JFrame frame=new JFrame("Conway's Game of Life");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
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
