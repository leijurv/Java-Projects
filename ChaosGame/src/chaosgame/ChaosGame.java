package chaosgame;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class ChaosGame extends JComponent implements MouseMotionListener{
    public ChaosGame(){
        addMouseMotionListener(this);
    }
    static ChaosGame M = new ChaosGame();
    static JFrame frame;
    static double size = 500;
    static boolean done = false;
    static double[] Pos;
    static double offset = 10;
    static long seed=5021;
    public static void poopoo3() {
        double aY = Math.sqrt(size * size - (size / 2) * (size / 2)) + offset;
        double[] pos = {offset, aY, offset + size, aY, offset + (size / 2), offset};
        Pos = pos;
    }
    public static void poopoo4() {
        double[] pos = {offset, offset, offset + size, offset, offset, offset + size, offset + size, offset + size};
        Pos = pos;
    }
    public void paintComponent(Graphics g) {
double X = mouseX;
        double Y = mouseY;
        Random r=new Random(seed);
        r.nextInt(500+mouseX);
        r.nextInt(500+mouseY);
        
        int s=10000;
        
        for (int i = 0; i < s; i++) {
            int n = r.nextInt(Pos.length / 2) * 2;
            X = (X * (d - 1) + Pos[n]) / d;
            Y = (Y * (d - 1) + Pos[n + 1]) / d;
            g.fillRect((int)X, (int)Y, 1, 1);
        }
    }
    public static void main(String[] args) throws InterruptedException {
poopoo3();
        frame = new JFrame("Chaos Game");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setSize(1000, 700);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    static final int d = 2;
    static long lastTime=System.currentTimeMillis()+500;
    static int mouseX=50;
    static int mouseY=50;

    @Override
    public void mouseDragged(MouseEvent me) {
        seed=System.currentTimeMillis();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        
        if (System.currentTimeMillis()>lastTime+100){
            lastTime=System.currentTimeMillis();
            mouseX=me.getX();
            mouseY=me.getY();
            M.repaint();
        }
    }
}