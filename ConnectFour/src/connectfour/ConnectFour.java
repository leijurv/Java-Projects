/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfour;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class ConnectFour extends JComponent {
    
    private static final long serialVersionUID = 1L;
    Board current;
    static ConnectFour M = new ConnectFour();
    static JFrame frame;
    static int state = 0;
    static int optimalMove = -1;
    static final String[] states = {"Guaranteed Black win", "Tie or unknown in perfect play", "Guaranteed Red win"};

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(0, 80, 410, 350);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                switch (current.board[i][j]) {
                    case -1:
                        g.setColor(Color.BLACK);
                        break;
                    case 1:
                        g.setColor(Color.RED);
                        break;
                    default:
                        g.setColor(Color.WHITE);
                        break;
                }
                g.fillOval(i * 60, j * 60 + 80, 50, 50);
            }
        }
        g.setColor(Color.BLACK);
        g.drawString(states[state + 1], 0, 25);
        g.drawString("Search depth is " + Board.depth, 0, 40);
        g.drawString("Updating screen whenever depth is less than " + Board.screenUpdateDepth, 0, 55);
        if (optimalMove>-1){
            g.drawString("Best move", optimalMove * 60, 75);
        }
        if (optimalMove==-2){
            g.drawString("Any move results in other player winning",0,75);
        }
        g.drawString("Connect four simulator. By Lurf. Copyright January 4, 2013",0,10);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[][] a = new int[7][6];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                a[i][j] = 0;
            }
        }
        updateScreen(new Board(a, 0, false), 0);
        frame = new JFrame("Connect Four");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(410, 452);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        try {
            Board.depth = Integer.parseInt(JOptionPane.showInputDialog("Search depth? (How many moves it looks ahead)"));
        } catch (Exception e) {
            System.out.println("Not a number");
            System.exit(0);
        }
        M.repaint();
        try {
            Board.screenUpdateDepth = Integer.parseInt(JOptionPane.showInputDialog("Screen update interval?"));
        } catch (Exception e) {
            System.out.println("Not a number");
            System.exit(0);
        }
        M.repaint();
        while (true) {
            String s = JOptionPane.showInputDialog("What color do you want to put in? (R/B/D for done inputting)").toUpperCase();
            if (!s.equals("R") && !s.equals("B") && !s.equals("D")) {
                System.out.println("Not a color");
                System.exit(0);
            }
            if (s.equals("D")) {
                break;
            }
            try {
                int i = Integer.parseInt(JOptionPane.showInputDialog("What column? (1-7)")) - 1;
                int height = 0;
                while (height < 6 && a[i][height] == 0) {
                    height++;
                }
                height--;
                a[i][height] = s.equals("R") ? 1 : -1;
            } catch (Exception e) {
                System.out.println("BAD");
                System.exit(0);
            }
            updateScreen(new Board(a, 0, false), 0);
        }
        String s = JOptionPane.showInputDialog("Who goes first? (R/B)");
        s = s.toUpperCase();
        if (!s.equals("R") && !s.equals("B")) {
            System.out.println("Not a color");
            System.exit(0);
        }
        Board b = new Board(a, 0, s.equals("R"));
        M.current = b;
        int result = b.solve();
        updateScreen(b, result);
    }

    public static void updateScreen(Board b, int State) {
        M.current = b;
        state = State;
        M.repaint();
    }
}
