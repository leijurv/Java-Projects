package mandelbrot;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
public class Mandelbrot extends JComponent implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;
    static JFrame frame;
    static Mandelbrot M = new Mandelbrot();
    static ArrayList<Color> colors = new ArrayList<Color>();
    static int iterationLimit = 1000;
    static double yScale = 0.0035;
    static JComboBox<String> rightClickCombo;
    static JComboBox<String> colorSchemeCombo;
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
    static double juliaY = -0.35153125;
    static int leftClickAction = 4;
    static boolean julia = false;
    static double juliaX = -0.6724375;
    static int rightClickAction = 4;
    static JComboBox<String> leftClickCombo;
    static double orbitLimit = 4;
    static final String[] ClickOptions = {"Zooms in", "Zooms out", "Shows orbit", "Shows julia set", "Does nothing"};
    static final String[] colorSchemes = {"Hue", "Black and white", "Rotating scheme"};
    static double lastClickedX = 0;
    static JButton DecExponentButton;
    static double lastClickedY = 0;
    static JComboBox<String> orbitLimitCombo;
    static JComboBox<String> iterationLimitCombo;
    static JComboBox<String> Smooth;
    static int exponent = 2;
    static JButton IncExponentButton;
    static boolean drawThreadRunning = false;
    static boolean smooth = true;
    static BufferedImage export;
    static boolean exporting = false;
    static boolean animated = false;
    static final int[] iterationCombs = new int[] {100, 500, 1000, 5000, 10000};
    public static void main(String[] args) throws Exception {
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
    public static void setupScreen() throws Exception {
        frame = new JFrame("Leif's Mandelbrot Set Of Awesomeness");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
        Smooth = new JComboBox<String>(new String[] {"Smooth", "Jagged"});
        Smooth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                smooth = !smooth;
                (new redraw()).start();
            }
        });
        frame.add(Smooth);
        orbitLimitCombo = new JComboBox<String>(new String[] {"0", "1", "2", "3", "4", "5"});
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
        String[] iterationC = new String[iterationCombs.length];
        for (int i = 0; i < iterationCombs.length; i++) {
            iterationC[i] = Integer.toString(iterationCombs[i]);
        }
        iterationLimitCombo = new JComboBox<String>(iterationC);
        iterationLimitCombo.setSelectedIndex(4);
        iterationLimitCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                iterationLimit = iterationCombs[iterationLimitCombo.getSelectedIndex()];
                (new redraw()).start();
            }
        });
        leftClickCombo = new JComboBox<String>(ClickOptions);
        rightClickCombo = new JComboBox<String>(ClickOptions);
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
        colorSchemeCombo = new JComboBox<String>(colorSchemes);
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
        JButton j = new JButton("Export");
        j.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                new Thread() {
                    @Override
                    public void run() {
                        export();
                    }
                }.start();
            }
        });
        frame.add(j);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        Thread.sleep(500);
        xWidth = frame.getWidth() / 2;
        yWidth = frame.getHeight() / 2;
        ImageBuffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        export = new BufferedImage(frame.getWidth() * 4, frame.getHeight() * 4, BufferedImage.TYPE_INT_RGB);
        (new redraw()).start();
        if (!animated) {
            return;
        }
        long start = System.currentTimeMillis();
        julia = true && animated;
        double startX = -1.309;
        boolean radd = true;
        double endX = 0.1295;
        double startY = -0.1155;
        double endY = -0.812;
        double ccenterX = ((-1.14865) + (-1.1243)) / 2;
        double ccenterY = (-0.26515 + (-0.2159)) / 2;
        double rad = Math.sqrt((ccenterX - (-1.14865)) * (ccenterX - (-1.14865)) + (ccenterY - (-0.2159)) * (ccenterY - (-0.2159))) * 1.05;
        double total = 60 * 1000;/*
         double[] points = {1., 0., 1.00462, 0.000302492, 1.01833, 0.00241267, 1.04073,
         0.00810198, 1.07116, 0.01907, 1.1087, 0.0369098, 1.15221, 0.0630754,
         1.20032, 0.0988507, 1.25148, 0.145322, 1.30398, 0.203352, 1.35595,
         0.273561, 1.40546, 0.356307, 1.45049, 0.451674, 1.48898, 0.559462,
         1.51889, 0.679185, 1.53823, 0.810068, 1.54508, 0.951057, 1.53766,
         1.10082, 1.5143, 1.25778, 1.47357, 1.42012, 1.41421, 1.58579,
         1.33526, 1.75256, 1.23598, 1.91807, 1.11595, 2.07979, 0.975062,
         2.23514, 0.81352, 2.38146, 0.631863, 2.5161, 0.430962, 2.63644,
         0.212021, 2.73992, -0.0234337, 2.8241, -0.273561, 2.88669, -0.536226,
         2.92559, -0.809017, 2.93893, -1.08928, 2.9251, -1.37412, 2.88278,
         -1.6605, 2.81099, -1.94519, 2.70906, -2.22488, 2.57672, -2.49617,
         2.41404, -2.75568, 2.22151, -3., 2., -3.22583, 1.75076, -3.42996,
         1.47544, -3.60934, 1.17605, -3.76115, 0.854962, -3.88278, 0.514889,
         -3.97192, 0.15884, -4.02657, -0.209894, -4.04508, -0.587785,
         -4.02621, -0.971102, -3.96908, -1.35595, -3.87325, -1.73834,
         -3.73873, -2.11418, -3.56595, -2.4794, -3.35581, -2.82994, -3.10962,
         -3.16182, -2.82916, -3.4712, -2.51662, -3.75443, -2.17458, -4.00806,
         -1.80601, -4.22894, -1.41421, -4.41421, -1.00281, -4.56139,
         -0.575694, -4.66835, -0.136983, -4.7334, 0.309017, -4.75528, 0.75787,
         -4.73321, 1.20505, -4.66685, 1.64602, -4.55638, 2.07622, -4.40243,
         2.49123, -4.20613, 2.88669, -3.96908, 3.25846, -3.69334, 3.60262,
         -3.38139, 3.91552, -3.03614, 4.19383, -2.66087, 4.43458, -2.25919,
         4.63518, -1.83503, 4.79349, -1.39257, 4.9078, -0.936194, 4.97689,
         -0.470452, 5., 0., 4.97689, 0.470452, 4.9078, 0.936194, 4.79349,
         1.39257, 4.63518, 1.83503, 4.43458, 2.25919, 4.19383, 2.66087,
         3.91552, 3.03614, 3.60262, 3.38139, 3.25846, 3.69334, 2.88669,
         3.96908, 2.49123, 4.20613, 2.07622, 4.40243, 1.64602, 4.55638,
         1.20505, 4.66685, 0.75787, 4.73321, 0.309017, 4.75528, -0.136983,
         4.7334, -0.575694, 4.66835, -1.00281, 4.56139, -1.41421, 4.41421,
         -1.80601, 4.22894, -2.17458, 4.00806, -2.51662, 3.75443, -2.82916,
         3.4712, -3.10962, 3.16182, -3.35581, 2.82994, -3.56595, 2.4794,
         -3.73873, 2.11418, -3.87325, 1.73834, -3.96908, 1.35595, -4.02621,
         0.971102, -4.04508, 0.587785, -4.02657, 0.209894, -3.97192, -0.15884,
         -3.88278, -0.514889, -3.76115, -0.854962, -3.60934, -1.17605,
         -3.42996, -1.47544, -3.22583, -1.75076, -3., -2., -2.75568, -2.22151,
         -2.49617, -2.41404, -2.22488, -2.57672, -1.94519, -2.70906, -1.6605,
         -2.81099, -1.37412, -2.88278, -1.08928, -2.9251, -0.809017, -2.93893,
         -0.536226, -2.92559, -0.273561, -2.88669, -0.0234337, -2.8241,
         0.212021, -2.73992, 0.430962, -2.63644, 0.631863, -2.5161, 0.81352,
         -2.38146, 0.975062, -2.23514, 1.11595, -2.07979, 1.23598, -1.91807,
         1.33526, -1.75256, 1.41421, -1.58579, 1.47357, -1.42012, 1.5143,
         -1.25778, 1.53766, -1.10082, 1.54508, -0.951057, 1.53823, -0.810068,
         1.51889, -0.679185, 1.48898, -0.559462, 1.45049, -0.451674, 1.40546,
         -0.356307, 1.35595, -0.273561, 1.30398, -0.203352, 1.25148,
         -0.145322, 1.20032, -0.0988507, 1.15221, -0.0630754, 1.1087,
         -0.0369098, 1.07116, -0.01907, 1.04073, -0.00810198, 1.01833,
         -0.00241267, 1.00462, -0.000302492, 1., 0.};*/

        String name = radd ? "julia radius " + rad + " around " + ccenterX + "," + ccenterY : "julia_" + startX + "," + startY + "_to_" + endX + "," + endY;
        //name = "julia zack's points over 4";
        ImageOutputStream output = new FileImageOutputStream(new File(System.getProperty("user.home") + "/Documents/" + name + ".gif"));
        GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
        int percent = 0;
        while (animated) {
            //double time = System.currentTimeMillis() - start;
            //double p = time / total;
            double p = percent++;
            //p /= points.length;
            //p /= 2;
            p /= 100;
            if (p >= 1) {
                break;
            }
            if (radd) {
                juliaX = Math.cos(p * Math.PI * 2) * rad + ccenterX;
                juliaY = Math.sin(p * Math.PI * 2) * rad + ccenterY;
            } else {
                juliaX = startX + (endX - startX) * p;
                juliaY = startY + (endY - startY) * p;
            }
            //juliaX = points[percent * 2] / 4;
            //juliaY = points[percent * 2 + 1] / 4;
            //centerX = juliaX;
            //centerY = juliaY;
            System.out.println("Drawing at " + juliaX + "," + juliaY);
            doActualDraw();
            writer.writeToSequence(ImageBuffer);
        }
        writer.close();
        output.close();
        System.out.println("DONE");
        //System.exit(0);
    }
    public static void export() {
        JProgressBar dj = new JProgressBar(-xWidth * 4, xWidth * 4);
        frame.add(dj);
        frame.repaint();
        exporting = true;
        for (int x = -xWidth * 4; x < xWidth * 4; x++) {
            dj.setValue(x);
            if (x % 100 == 0) {
                System.out.println(x + "," + xWidth * 4);
            }
            frame.repaint();
            double cX = ((double) x) * xScale / 4 + centerX;
            for (int y = -yWidth * 4; y < yWidth * 4; y++) {
                double cY = ((double) y) * yScale / 4 + centerY;
                double zX = cX;
                double zY = cY;
                int its = 0;
                boolean done = false;
                while (!done) {
                    double c = zX * zX;
                    double d = zY * zY;
                    double xt = zX;
                    double yt = zY;
                    if (exponent == 2) {
                        double xx = xt * zX - yt * zY;
                        yt = xt * zY + yt * zX;
                        xt = xx;
                    } else {
                        for (int i = 0; i < exponent - 1; i++) {
                            double xx = xt * zX - yt * zY;
                            yt = xt * zY + yt * zX;
                            xt = xx;
                        }
                    }
                    xt += (julia ? juliaX : cX);
                    yt += (julia ? juliaY : cY);
                    zX = xt;
                    zY = yt;
                    its++;
                    if (!(c + d < orbitLimit && its < Mandelbrot.iterationLimit)) {
                        done = true;
                    }
                }
                if (its == iterationLimit) {
                    export.setRGB(x + xWidth * 4, y + yWidth * 4, Color.BLACK.getRGB());
                } else {
                    double R = 0;
                    if (smooth) {
                        double d = Math.sqrt(zY * zY + zX * zX);
                        R = (double) its - Math.log(Math.log(d)) / Math.log(2.0);
                    } else {
                        R = (double) its;
                    }
                    export.setRGB(x + xWidth * 4, y + yWidth * 4, colorFromIts(R).getRGB());
                }
            }
        }
        exporting = false;
        frame.remove(dj);
        try {
            String[] s = ImageIO.getWriterFormatNames();
            for (String S : s) {
                System.out.println(S);
            }
            String name = (julia ? "julia_" + juliaX + "," + juliaY : "mandelbrot");
            System.out.println(ImageIO.write(export, "jpeg", new File(System.getProperty("user.home") + "/Desktop/" + name + ".jpeg")));
        } catch (IOException ex) {
            System.out.println(ex);
        }
        frame.repaint();
    }
    public static void drawMandelbrot() {
        for (int x = -xWidth; x < xWidth && drawThreadRunning; x++) {
            M.repaint();
            final double cX = ((double) x) * xScale + centerX;
            double cY = ((double) (-yWidth)) * yScale + centerY;
            double incrementX = (julia ? juliaX : cX);
            for (int y = -yWidth; y < yWidth && drawThreadRunning; y++) {
                double zX = cX;
                double zY = cY;
                int its = 0;
                boolean done = false;
                double incrementY = (julia ? juliaY : cY);
                while (!done) {
                    double c = zX * zX;
                    double d = zY * zY;
                    double xt = zX;
                    double yt = zY;
                    if (exponent == 2) {
                        double xx = xt * zX - yt * zY;
                        yt = xt * zY + yt * zX;
                        xt = xx;
                    } else {
                        for (int i = 0; i < exponent - 1; i++) {
                            double xx = xt * zX - yt * zY;
                            yt = xt * zY + yt * zX;
                            xt = xx;
                        }
                    }
                    xt += incrementX;
                    yt += incrementY;
                    zX = xt;
                    zY = yt;
                    its++;
                    if ((c + d >= orbitLimit || its >= Mandelbrot.iterationLimit)) {
                        done = true;
                    }
                }
                if (its == iterationLimit) {
                    ImageBuffer.setRGB(x + xWidth, y + yWidth, Color.BLACK.getRGB());
                } else {
                    double R = 0;
                    if (smooth) {
                        double d = Math.sqrt(zY * zY + zX * zX);
                        R = (double) its - Math.log(Math.log(d)) / Math.log(2.0);
                    } else {
                        R = (double) its;
                    }
                    ImageBuffer.setRGB(x + xWidth, y + yWidth, colorFromIts(R).getRGB());
                }
                cY += yScale;
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
                Color x = new Color(getHue(its));
                int RGBAverage = (x.getRed() + x.getBlue() + x.getGreen()) / 3;
                c = new Color(RGBAverage, RGBAverage, RGBAverage);
                break;
            case 2:
                c = colors.get(((int) its - 1) % colors.size());
                break;
        }
        return c;
    }
    public static int getHue(double its) {
        return Color.HSBtoRGB((float) (its / 100), 1F, 1F);
    }
    @Override
    public void paintComponent(Graphics g) {
        // if (export!=null && exporting)
        //   g.drawImage(export.getScaledInstance(frame.getWidth(),frame.getHeight(),Image.SCALE_SMOOTH), 0, 0, null);
        if (ImageBuffer != null) {
            g.drawImage(ImageBuffer, 0, 0, null);
        }
        g.setColor(Color.WHITE);
        g.drawString("Real part: " + lastClickedX + ", Imaginary part: " + lastClickedY, 10, 10);
        g.drawString("Z exponent is " + exponent, IncExponentButton.getX() - 100, IncExponentButton.getY() + 17);
        g.drawString("Left click ", leftClickCombo.getX() - 55, leftClickCombo.getY() + 17);
        g.drawString("Right click ", rightClickCombo.getX() - 65, rightClickCombo.getY() + 17);
        g.drawString("Color scheme ", colorSchemeCombo.getX() - 85, colorSchemeCombo.getY() + 17);
        g.drawString("Orbit limit ", orbitLimitCombo.getX() - 65, orbitLimitCombo.getY() + 17);
        g.drawString("Iteration limit ", iterationLimitCombo.getX() - 85, iterationLimitCombo.getY() + 17);
        if (julia) {
            g.drawString("Displaying Julia Set with", 10, 85);
            g.drawString("Real part: " + juliaX + " and imaginary part: " + juliaY, 10, 100);
        }
        g.fillRect((int) ((-centerX) / xScale + xWidth - 2), (int) ((1 - centerY) / yScale + yWidth - 2), 4, 4);
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
        int n = (me.getButton() == MouseEvent.BUTTON1) ? leftClickAction : rightClickAction;
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
            doActualDraw();
        }
    }
    public static void doActualDraw() {
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
