/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe3;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class TicTacToe3 extends JComponent implements KeyListener {
    
    static int[] x = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    boolean move = false;
    boolean done = false;
    static boolean see = false;

    public void paintComponent(Graphics g) {
        Board B = new Board(x, move);
        if (see) {
            B.render(g, 0, 20, frame.getHeight() - 40);
        } else {
            B.show(g, 0, 20, frame.getHeight() - 40);
        }
        if (B.done() != 0 || B.full()) {
            done = true;
        }
        g.setColor(Color.BLACK);
        g.drawLine(frame.getHeight(), 10, frame.getHeight(), 100);
        g.drawLine(frame.getHeight() + 30, 10, frame.getHeight() + 30, 100);
        g.drawLine(frame.getHeight() - 30, 40, frame.getHeight() + 60, 40);
        g.drawLine(frame.getHeight() - 30, 70, frame.getHeight() + 60, 70);
        for (int i = 0; i < 9; i++) {
            g.drawString(Integer.toString(i + 1), i % 3 * 30 + frame.getHeight() - 20, (i - i % 3) * 10 + 30);
        }
        if (!done) {
            g.drawString("Choose where O goes by pressing a number 1-9", 10, 10);
        }
        if (B.done() != 0) {
            g.drawString((B.done() == 1 ? "X" : "O") + " wins.", 10, 10);
        } else {
            if (B.full()) {
                g.drawString("Tie.", 10, 10);
            }
        }
    }
    static JFrame frame;
    static TicTacToe3 M = new TicTacToe3();

    public static void main(String[] args) {
        String m = JOptionPane.showInputDialog("See all possible moves? (Y/N)");
        if (m.equals("Y")) {
            see = true;
        }
        String Mm = JOptionPane.showInputDialog("Computer goes first? (Y/N)");
        if (Mm.equals("Y")) {
            x[0] = 1;
        }
        frame = new JFrame("Tic Tac Toe");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(1000, 700);
        //frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public TicTacToe3() {
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        char[] n = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char N = ke.getKeyChar();
        for (int i = 0; i < n.length; i++) {
            if (n[i] == N && x[i] == 0 && !done) {
                x[i] = -1;
                Board B = new Board(x, true);
                int m = B.solve()[1];
                if (m != -1) {
                    x[m] = 1;
                }
                M.repaint();
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
