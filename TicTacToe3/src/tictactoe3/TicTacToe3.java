/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe3;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leijurv
 */
public class TicTacToe3 extends JComponent{
    public void paintComponent(Graphics g){
        int[] x={0,0,1,0,0,0,0,0,0};
        Board B=new Board(x,false);
        B.render(g,10,10,frame.getHeight()-50);
    }
    static JFrame frame;
    static TicTacToe3 M=new TicTacToe3();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frame=new JFrame("Conway's Game of Life");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
        // TODO code application logic here
    }
}
