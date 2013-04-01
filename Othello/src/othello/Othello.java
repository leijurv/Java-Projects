/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

import java.awt.Color;
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
public class Othello extends JComponent{
static Othello M=new Othello();
static JFrame frame;
Board current;
static int state=-1;
public void paintComponent(Graphics g){
    g.setColor(Color.GREEN);
        g.fillRect(0, 0, Board.size*60-5, Board.size*60-5);
        
        for (int i = 0; i < Board.size; i++) {
            for (int j = 0; j < Board.size; j++) {
                switch (current.board[i][j]) {
                    case -1:
                        g.setColor(Color.BLACK);
                        break;
                    case 1:
                        g.setColor(Color.WHITE);
                        break;
                    default:
                        g.setColor(Color.GREEN);
                        break;
                }
                g.fillOval(i * 60, j * 60 , 50, 50);
                g.setColor(Color.BLACK);
                g.drawRect(i*60-5,j*60-5,60,60);
            }
        }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        byte[][] b=new byte[8][8];
        for (int i=0; i<b.length; i++){
            for (int j=0; j<b.length; j++){
                b[i][j]=0;
            }
        }
        b[3][3]=1;
        b[3][4]=-1;
        b[4][3]=-1;
        b[4][4]=1;
        updateScreen(new Board(b, 0, false), 0);
        frame = new JFrame("Othello");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(Board.size*60-4, Board.size*60+16);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Board B=new Board(b,0,true);
       while(true){
            B.solve();
            if (B.Best==null){
                break;
            }
            B=new Board(B.Best.result.board,0,!B.move);
            updateScreen(B,0);
            
            
        }
       System.out.println(B.eval(false));
        System.out.println("done");
    }
    public static void updateScreen(Board b, int State) {
        M.current = b;
        state = State;
        M.repaint();
    }
}