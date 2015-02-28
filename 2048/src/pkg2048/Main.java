/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
/**
 *
 * @author leijurv
 */
public class Main {
    static Board b = new Board();
    static int open = 0;
    static int depth = 0;
    static int move = 0;
    static int DDDDDDDDD = 1;
    static ArrayList<Integer> scores = new ArrayList<Integer>();
    static String scl = "";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        b.placeRandomly();
        b.placeRandomly();
        JFrame frame = new JFrame("2048");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(5000,5000);
        frame.setContentPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setFont(new Font("Courier",Font.PLAIN,30));
                b.draw(g);
                g.setFont(new Font("Courier",Font.BOLD,12));
                g.drawString("Open: " + open + "   Depth: " + depth + "    Score: " + b.getScore() + "     " + (move == -1 ? "NO MOVE" : Board.moveNames[move]) + "    Scores: " + scl,10,10);
            }
        });
        frame.setVisible(true);
        double avg = test(frame);
        System.out.println("Mode: " + 0);
        System.out.println("Depth: " + DDDDDDDDD);
        System.out.println("Value: " + scores);
        System.out.println("Average: " + avg);
        scores = new ArrayList<>();
        /*
         Scanner scan = new Scanner(System.in);
         while (true) {
         System.out.print(b);
         int move = Integer.parseInt(scan.nextLine());
         b = b.move(move);
         b.placeRandomly();
         }*/
    }
    public static double test(JFrame frame) {
        while (true) {
            //System.out.println(b);
            open = Board.getNumOpenSpots(b.board);
            depth = 3;
            move = (int) b.solve(depth,1)[1];
            frame.repaint();
            if (move == -1) {

                System.out.println("GAME OVER: " + b);
                System.exit(0);
                /*
                 scores.add(b.inGameScore);
                 scl = scores.toString();
                 //System.out.println(scores);
                 double tot = 0;
                 for (int i : scores) {
                 tot += (double) i;
                 }
                 tot /= (double) scores.size();
                 //System.out.println("AVERAGE: " + tot);
                 //System.out.println("Games: " + scores.size());
                 if (scores.size() >= 100) {
                 return tot;
                 }
                 //Thread.sleep(10000);
                 b = new Board();
                 b.placeRandomly();
                 b.placeRandomly();
                 continue;*/
            }
            //Thread.sleep(5000);
            b = b.move(move);
            b.placeRandomly();
        }
    }
    public static int getDepth(int open) {
        switch (open) {
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
                return 3;
            default:
                return 4;
        }
    }
}
