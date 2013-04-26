package mandelbrot;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Mandelbrot extends JComponent implements MouseListener, MouseMotionListener {
    static long UI=System.currentTimeMillis();
    private static final long serialVersionUID = 1L;
    static JFrame A;
    static Mandelbrot B = new Mandelbrot();
    static ArrayList<Color> C = new ArrayList<Color>();
    static int D = 1000;
    static double E = -0.0035;
    static JComboBox F;
    static JComboBox G;
    static int H = 500;
    static double I = 0;
    static double J = 0;
    static BufferedImage K;
    static int L = 0;
    static int M = 0;
    static double N = 0.0035;
    static int O = 600;
    static int P = 0;
    static boolean Q = false;
    static double R = 0;
    static int S = 4;
    static boolean T = false;
    static double U = 0;
    static int V = 4;
    static JComboBox W;
    static double X = 4;
    static final String[] Y = {"Zooms in", "Zooms out", "Shows orbit", "Shows julia set", "Does nothing"};
    static final String[] Z = {"Hue", "Black and white", "Rotating scheme","BAD"};
    static double AA = 0;
    static JButton AB;
    static double AC = 0;
    static JComboBox AD;
    static JComboBox AE;
    static long AF = System.currentTimeMillis();
    static final int AG = 50;
    static int AH = 2;
    static JButton AI;
    static boolean AJ = false;

    public static void main(String[] args) throws InterruptedException {
        B.addMouseListener(B);
        B.addMouseMotionListener(B);
        A();
        B();
    }

    public static void A() {
        C.add(Color.BLUE);
        C.add(Color.CYAN);
        C.add(Color.GREEN);
        C.add(Color.YELLOW);
        C.add(Color.ORANGE);
        C.add(Color.RED);
    }

    public static void B() throws InterruptedException {
        A = new JFrame("Leif's Mandelbrot Set Of Awesomeness");
        B.setFocusable(true);
        (A).setContentPane(B);
        A.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
        AD = new JComboBox(new String[]{"0", "1", "2", "3", "4", "5"});
        AD.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                double x = X;
                X = (double) AD.getSelectedIndex();
                if (X != x) {
                    (new B()).start();
                }
            }
        });
        AD.setSelectedIndex(4);
        AE = new JComboBox(new String[]{"100", "500", "1000", "5000", "10000"});
        AE.setSelectedIndex(2);
        AE.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                D = (new int[]{100, 500, 1000, 5000, 10000})[AE.getSelectedIndex()];
                (new B()).start();
            }
        });

        W = new JComboBox(Y);
        F = new JComboBox(Y);
        W.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                S = W.getSelectedIndex();
            }
        });
        F.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                V = F.getSelectedIndex();
            }
        });
        F.setSelectedIndex(V);
        W.setSelectedIndex(S);
        A.add(W);
        A.add(F);
        G = new JComboBox(Z);
        G.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                L = G.getSelectedIndex();
                (new B()).start();
            }
        });
        A.add(G);
        A.add(AD);
        A.add(AE);
        AI = new JButton("Increase Z exponent");
        AB = new JButton("Decrease Z exponent");
        AB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                AH--;
                E = -0.0035;
                N = 0.0035;
                I = 0;
                J = 0;
                (new B()).start();
            }
        });
        AI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                AH++;
                E = -0.0035;
                N = 0.0035;
                I = 0;
                J = 0;
                (new B()).start();
            }
        });
        A.add(AI);
        A.add(AB);
        A.setExtendedState(Frame.MAXIMIZED_BOTH);
        A.setVisible(true);
        A.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        O = A.getWidth() / 2;
        H = A.getHeight() / 2 - 10;
        K = new BufferedImage(A.getWidth(), A.getHeight(), BufferedImage.TYPE_INT_RGB);
        (new B()).start();
        while(true){
            if (!AJ){
                (new B()).start();
            }
            Thread.sleep(10);
        }
    }

    public static void C() {
        //D=(int) ((System.currentTimeMillis()-UI)/1000);
        if (T) {
            E();
        } else {
            F();
        }
    }

    public static void D(int x) {
        if (System.currentTimeMillis() > AF + AG) {
            B.repaint();
            AF = System.currentTimeMillis();
        }
    }

    public static void E() {
        for (int x = -O; x < O && AJ; x++) {
            D(x);
            for (int y = -H; y < H && AJ; y++) {
                double a = ((double) x) * N + I;
                double b = ((double) y) * E + J;
                double e = a;
                double f = b;
                int g = 0;
                boolean h = false;
                while (!h) {
                    double c = e * e;
                    double d = f * f;
                    double xt = e;
                    double yt = f;
                    for (int i = 0; i < AH - 1; i++) {
                        double xx = xt * e - yt * f;
                        yt = xt * f + yt * e;
                        xt = xx;
                    }
                    xt += U;
                    yt += R;
                    e = xt;
                    f = yt;
                    g++;
                    if (!(c + d < X && g < Mandelbrot.D)) {
                        h = true;
                    }
                }
                if (g == D) {
                    K.setRGB(x + O, y + H, Color.BLACK.getRGB());
                } else {
                    K.setRGB(x + O, y + H, G(g).getRGB());
                }
            }
        }
    }

    public static void F() {
        for (int x = -O; x < O && AJ; x++) {
            D(x);
            for (int y = -H; y < H && AJ; y++) {
                double a = ((double) x) * N + I;
                double b = ((double) y) * E + J;
                double e = a;
                double f = b;
                int its = 0;
                boolean done = false;
                while (!done) {
                    double c = e * e;
                    double d = f * f;
                    double xt = e;
                    double yt = f;
                    for (int i = 0; i < AH - 1; i++) {
                        double xx = xt * e - yt * f;
                        yt = xt * f + yt * e;
                        xt = xx;
                    }
                    xt += a;
                    yt += b;
                    e = xt;
                    f = yt;
                    its++;
                    if (!(c + d < X && its < Mandelbrot.D)) {
                        done = true;
                    }
                }
                if (its == D) {
                    K.setRGB(x + O, y + H, Color.BLACK.getRGB());
                } else {
                    K.setRGB(x + O, y + H, G(its).getRGB());
                }
            }
        }
    }

    public static Color G(int its) {
        Color c = Color.black;
        switch (L) {
            case 0:
                c = new Color(H(its));
                break;
            case 1:
                
                
                Color x=new Color(H(its));
                int xx=(x.getRed()+x.getBlue()+x.getGreen())/3;
                c=new Color(xx,xx,xx);
                break;
            case 2:
                c = C.get((its - 1) % C.size());
                break;
            case 3:
                int color = Math.round((float) its * 256F / (100F));
                color = color > 255 ? 0 : color;
                c = new Color(color, color, color);
                break;
        }
        return c;
    }

    public static int H(int its) {
        return Color.HSBtoRGB((float) its / (float) 100, 1F, 1F);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(K, 0, 0, null);
        g.setColor(Color.BLACK);
        //g.fillRect(0,0,frame.getWidth(),80);
        g.setColor(Color.WHITE);
        g.drawString("Real part: " + AA + ", Imaginary part: " + AC, 10, 10);
        g.drawString("Z exponent is " + AH, AI.getX() - 100, AI.getY() + 17);
        g.drawString("Left click ", W.getX() - 55, W.getY() + 17);
        g.drawString("Right click ", F.getX() - 65, F.getY() + 17);
        g.drawString("Color scheme ", G.getX() - 85, G.getY() + 17);
        g.drawString("Orbit limit ", AD.getX() - 65, AD.getY() + 17);
        g.drawString("Iteration limit ", AE.getX() - 85, AE.getY() + 17);

        if (T) {
            g.drawString("Displaying Julia Set with", 10, 70);
            g.drawString("Real part: " + U + " and imaginary part: " + R, 10, 85);
        }
        g.fillRect((int) (-I / N + O - 2), (int) (-J / E + H - 2), 4, 4);
        if (Q) {
            double a = ((double) M - O) * N + I;
            double b = ((double) P - H) * E + J;
            double e = a;
            double f = b;
            g.fillRect((int) ((e - I) / N) + O - 2, (int) ((f - J) / E) + H - 2, 4, 4);
            int its = 0;
            boolean done = false;
            while (!done) {
                double xt = e;
                double yt = f;
                double c = xt * xt;
                double d = yt * yt;
                for (int i = 0; i < AH - 1; i++) {
                    double xx = xt * e - yt * f;
                    yt = xt * f + yt * e;
                    xt = xx;
                }
                xt += (!T ? a : U);
                yt += (!T ? b : R);
                e = xt;
                f = yt;
                g.fillRect((int) ((e - I) / N) + O - 2, (int) ((f - J) / E) + H - 2, 4, 4);
                its++;
                if (!(c + d < X && its < Mandelbrot.D)) {
                    done = true;
                }
            }
        }
    }

    public Mandelbrot() {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    public static void I(boolean ZoomIn, MouseEvent me) {
        double xPos = (me.getX() - O) * N + I;
        double yPos = (me.getY() - H) * E + J;
        I = xPos;
        J = yPos;
        if (ZoomIn) {
            N *= 4;
            E *= 4;
        }
        N *= 0.5;
        E *= 0.5;
        (new B()).start();
    }

    public static void J(MouseEvent me) {
        T = !T;
        U = (me.getX() - O) * N + I;
        R = (me.getY() - H) * E + J;
        E = -0.0035;
        N = 0.0035;
        I = 0;
        J = 0;
        (new B()).start();
    }

    public static void K(MouseEvent me) {
        Q = true;
        M = me.getX();
        P = me.getY();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        double xPos = (me.getX() - O) * N + I;
        double yPos = (me.getY() - H) * E + J;
        AA = xPos;
        AC = yPos;
        if (me.getButton() == MouseEvent.BUTTON1) {
            switch (S) {
                case 0:
                    I(false, me);
                    break;
                case 1:
                    I(true, me);
                    break;
                case 2:
                    K(me);
                    B.repaint();
                    break;
                case 3:
                    J(me);
                    break;
                default:
                    break;
            }

        }
        if (me.getButton() == MouseEvent.BUTTON3) {
            switch (V) {
                case 0:
                    I(false, me);
                    break;
                case 1:
                    I(true, me);
                    break;
                case 2:
                    K(me);
                    B.repaint();
                    break;
                case 3:
                    J(me);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        double xPos = (me.getX() - O) * N + I;
        double yPos = (me.getY() - H) * E + J;
        AA = xPos;
        AC = yPos;
        if ((me.getButton() == MouseEvent.BUTTON3 && V == 2) || (me.getButton() == MouseEvent.BUTTON1 && S == 2)) {
            K(me);
        }
        B.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    public static class B extends Thread {

        @Override
        public void run() {
            if (AJ) {
                AJ = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }

            }
            AJ = true;
            C();
            AJ = false;
            B.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Q = false;
        B.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}