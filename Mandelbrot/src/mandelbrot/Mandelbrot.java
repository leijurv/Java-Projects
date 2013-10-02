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
    private static final long serialVersionUID = 1L;
    static JFrame frame;
    static Mandelbrot M = new Mandelbrot();
    static ArrayList<Color> colors = new ArrayList<Color>();
    static int iterationLimit = 1000;
    static double yScale = 0.0035;
    static JComboBox rightClickCombo;
    static JComboBox colorSchemeCombo;
    static int yWidth = 500;
    static double centerX = 0;
    static double centerY = 0;
    static BufferedImage ImageBuffer;
    static int colorScheme = 0;
    static int orbitX = 0;
    static double xScale = 0.0035;
    static int xWidth = 600;
    static int orbitY = 0;
    static boolean showingOrbit = false;
    static double juliaY = 0;
    static int leftClickAction = 4;
    static boolean julia = false;
    static double juliaX = 0;
    static int rightClickAction = 4;
    static JComboBox leftClickCombo;
    static double orbitLimit = 4;
    static final String[] ClickOptions = {"Zooms in", "Zooms out", "Shows orbit", "Shows julia set", "Does nothing"};
    static final String[] colorSchemes = {"Hue", "Black and white", "Rotating scheme"};
    static double lastClickedX = 0;
    static JButton DecExponentButton;
    static double lastClickedY = 0;
    static JComboBox orbitLimitCombo;
    static JComboBox iterationLimitCombo;
    static JComboBox Smooth;
    static long redrawTime = System.currentTimeMillis();
    static final int MinRedrawTimeMillis = 50;
    static int exponent = 2;
    static JButton IncExponentButton;
    static boolean drawThreadRunning = false;
    static boolean smooth = true;
        static final int[] iterationCombs=new int[]{100, 500, 1000, 5000, 10000};

    public static void main(String[] args) throws InterruptedException {
        M.addMouseListener(M);
        M.addMouseMotionListener(M);
        setupColors();
        setupScreen();
    }

    public static void setupColors() {
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
    }

    public static void setupScreen() throws InterruptedException {
        frame = new JFrame("Leif's Mandelbrot Set Of Awesomeness");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
        Smooth = new JComboBox(new String[] {"Smooth","Jagged"});
        Smooth.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                smooth=!smooth;
                (new redraw()).start();
            }
        });
        frame.add(Smooth);
        orbitLimitCombo = new JComboBox(new String[]{"0", "1", "2", "3", "4", "5"});
        orbitLimitCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                double x = orbitLimit;
                orbitLimit = (double) orbitLimitCombo.getSelectedIndex();
                if (orbitLimit != x) {
                    (new redraw()).start();
                }
            }
        });
        orbitLimitCombo.setSelectedIndex(4);
        String[] iterationC=new String[iterationCombs.length];
        for (int i=0; i<iterationCombs.length; i++){
            iterationC[i]=Integer.toString(iterationCombs[i]);
        }
        iterationLimitCombo = new JComboBox(iterationC);
        iterationLimitCombo.setSelectedIndex(2);
        iterationLimitCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                iterationLimit = iterationCombs[iterationLimitCombo.getSelectedIndex()];
                (new redraw()).start();
            }
        });
        
        leftClickCombo = new JComboBox(ClickOptions);
        rightClickCombo = new JComboBox(ClickOptions);
        leftClickCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                leftClickAction = leftClickCombo.getSelectedIndex();
            }
        });
        rightClickCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                rightClickAction = rightClickCombo.getSelectedIndex();
            }
        });
        rightClickCombo.setSelectedIndex(rightClickAction);
        leftClickCombo.setSelectedIndex(leftClickAction);
        frame.add(leftClickCombo);
        frame.add(rightClickCombo);
        colorSchemeCombo = new JComboBox(colorSchemes);
        colorSchemeCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                colorScheme = colorSchemeCombo.getSelectedIndex();
                (new redraw()).start();
            }
        });
        frame.add(colorSchemeCombo);
        frame.add(orbitLimitCombo);
        frame.add(iterationLimitCombo);
        IncExponentButton = new JButton("Increase Z exponent");
        DecExponentButton = new JButton("Decrease Z exponent");
        DecExponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                exponent--;
                yScale = 0.0035;
                xScale = 0.0035;
                centerX = 0;
                centerY = 0;
                (new redraw()).start();
            }
        });
        IncExponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                exponent++;
                yScale = 0.0035;
                xScale = 0.0035;
                centerX = 0;
                centerY = 0;
                (new redraw()).start();
            }
        });
        frame.add(IncExponentButton);
        frame.add(DecExponentButton);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        xWidth = frame.getWidth() / 2;
        yWidth = frame.getHeight() / 2 - 10;
        ImageBuffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        (new redraw()).start();
    }

    public static void showTempResult() {
        if (System.currentTimeMillis() > redrawTime + MinRedrawTimeMillis) {
            M.repaint();
            redrawTime = System.currentTimeMillis();
        }
    }

    public static void drawMandelbrot() {
        for (int x = -xWidth; x < xWidth && drawThreadRunning; x++) {
            showTempResult();
            for (int y = -yWidth; y < yWidth && drawThreadRunning; y++) {
                double cX = ((double) x) * xScale + centerX;
                double cY = ((double) y) * yScale + centerY;
                double zX = cX;
                double zY = cY;
                int its = 0;
                boolean done = false;
                while (!done) {
                    double c = zX * zX;
                    double d = zY * zY;
                    double xt = zX;
                    double yt = zY;
                    for (int i = 0; i < exponent - 1; i++) {
                        double xx = xt * zX - yt * zY;
                        yt = xt * zY + yt * zX;
                        xt = xx;
                    }
                    xt += (julia?juliaX:cX);
                    yt += (julia?juliaY:cY);
                    zX = xt;
                    zY = yt;
                    its++;
                    if (!(c + d < orbitLimit && its < Mandelbrot.iterationLimit)) {
                        done = true;
                    }
                }
                if (its == iterationLimit) {
                    ImageBuffer.setRGB(x + xWidth, y + yWidth, Color.BLACK.getRGB());
                } else {
                    double R=0;
                    if (smooth){
                        double d=Math.sqrt(zY*zY+zX*zX);
                        R=(double)its-Math.log(Math.log(d))/Math.log(2.0);
                    }else{
                        R=(double)its;
                    }
                    ImageBuffer.setRGB(x + xWidth, y + yWidth, colorFromIts(R).getRGB());
                }
            }
        }
    }

    public static Color colorFromIts(double its) {
        Color c = Color.black;
        switch (colorScheme) {
            case 0:
                c = new Color(getHue(its));
                break;
            case 1:
                
                
                Color x=new Color(getHue(its));
                int RGBAverage=(x.getRed()+x.getBlue()+x.getGreen())/3;
                c=new Color(RGBAverage,RGBAverage,RGBAverage);
                break;
            case 2:
                c = colors.get(((int)its - 1) % colors.size());
                break;
        }
        return c;
    }

    public static int getHue(double its) {
        return Color.HSBtoRGB((float)(its / 100), 1F, 1F);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(ImageBuffer, 0, 0, null);
        g.setColor(Color.WHITE);
        g.drawString("Real part: " + lastClickedX + ", Imaginary part: " + lastClickedY, 10, 10);
        g.drawString("Z exponent is " + exponent, IncExponentButton.getX() - 100, IncExponentButton.getY() + 17);
        g.drawString("Left click ", leftClickCombo.getX() - 55, leftClickCombo.getY() + 17);
        g.drawString("Right click ", rightClickCombo.getX() - 65, rightClickCombo.getY() + 17);
        g.drawString("Color scheme ", colorSchemeCombo.getX() - 85, colorSchemeCombo.getY() + 17);
        g.drawString("Orbit limit ", orbitLimitCombo.getX() - 65, orbitLimitCombo.getY() + 17);
        g.drawString("Iteration limit ", iterationLimitCombo.getX() - 85, iterationLimitCombo.getY() + 17);

        if (julia) {
            g.drawString("Displaying Julia Set with", 10, 70);
            g.drawString("Real part: " + juliaX + " and imaginary part: " + juliaY, 10, 85);
        }
        g.fillRect((int) ((-centerX) / xScale + xWidth - 2), (int) ((1-centerY) / yScale + yWidth - 2), 4, 4);
        if (showingOrbit) {
            double cX = ((double) orbitX - xWidth) * xScale + centerX;
            double cY = ((double) orbitY - yWidth) * yScale + centerY;
            double zX = cX;
            double zY = cY;
            g.fillRect((int) ((zX - centerX) / xScale) + xWidth - 2, (int) ((zY - centerY) / yScale) + yWidth - 2, 4, 4);
            int its = 0;
            boolean done = false;
            while (!done) {
                double xt = zX;
                double yt = zY;
                double c = xt * xt;
                double d = yt * yt;
                for (int i = 0; i < exponent - 1; i++) {
                    double xx = xt * zX - yt * zY;
                    yt = xt * zY + yt * zX;
                    xt = xx;
                }
                xt += (!julia ? cX : juliaX);
                yt += (!julia ? cY : juliaY);
                zX = xt;
                zY = yt;
                g.fillRect((int) ((zX - centerX) / xScale) + xWidth - 2, (int) ((zY - centerY) / yScale) + yWidth - 2, 4, 4);
                its++;
                if (!(c + d < orbitLimit && its < Mandelbrot.iterationLimit)) {
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

    public static void zoom(boolean ZoomIn, MouseEvent me) {
        double xPos = (me.getX() - xWidth) * xScale + centerX;
        double yPos = (me.getY() - yWidth) * yScale + centerY;
        centerX = xPos;
        centerY = yPos;
        if (ZoomIn) {
            xScale *= 4;
            yScale *= 4;
        }
        xScale *= 0.5;
        yScale *= 0.5;
        (new redraw()).start();
    }

    public static void setupJulia(MouseEvent me) {
        julia = !julia;
        juliaX = (me.getX() - xWidth) * xScale + centerX;
        juliaY = (me.getY() - yWidth) * yScale + centerY;
        yScale = 0.0035;
        xScale = 0.0035;
        centerX = 0;
        centerY = 0;
        (new redraw()).start();
    }

    public static void showOrbit(MouseEvent me) {
        showingOrbit = true;
        orbitX = me.getX();
        orbitY = me.getY();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        double xPos = (me.getX() - xWidth) * xScale + centerX;
        double yPos = (me.getY() - yWidth) * yScale + centerY;
        lastClickedX = xPos;
        lastClickedY = yPos;
        int n=(me.getButton() == MouseEvent.BUTTON1)?leftClickAction:rightClickAction;
            switch (n) {
                case 0:
                    zoom(false, me);
                    break;
                case 1:
                    zoom(true, me);
                    break;
                case 2:
                    showOrbit(me);
                    M.repaint();
                    break;
                case 3:
                    setupJulia(me);
                    break;
                default:
                    break;
            }

        
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        double xPos = (me.getX() - xWidth) * xScale + centerX;
        double yPos = (me.getY() - yWidth) * yScale + centerY;
        lastClickedX = xPos;
        lastClickedY = yPos;
        if ((me.getButton() == MouseEvent.BUTTON3 && rightClickAction == 2) || (me.getButton() == MouseEvent.BUTTON1 && leftClickAction == 2)) {
            showOrbit(me);
        }
        M.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    public static class redraw extends Thread {

        @Override
        public void run() {
            if (drawThreadRunning) {
                drawThreadRunning = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }

            }
            drawThreadRunning = true;
            drawMandelbrot();
            drawThreadRunning = false;
            M.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        showingOrbit = false;
        M.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}