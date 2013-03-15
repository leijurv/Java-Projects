/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ascii;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leif
 */
public class ASCII extends JComponent implements KeyListener{

    /**
     * @param args the command line arguments
     */
    static ASCII M;
    public static void main(String[] args) {
        M=new ASCII();
        JFrame frame=new JFrame("Conway's Game of Life");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
          frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
        // TODO code application logic here
    }
    public static class durp{
        public durp(){
            
        }
    }
    public ASCII(){
        
        addKeyListener(this);
    }
int prev=0;
    public void paintComponent(Graphics g){
        g.drawString(" "+prev,20,20);
    }
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        prev=ke.getKeyCode();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
