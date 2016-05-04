package mandelbrot;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class Mandelbrot extends JComponent implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;
    static JFrame frame;
    static final Mandelbrot M = new Mandelbrot();
    static final ArrayList<Color> colors = new ArrayList<Color>();
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
    static boolean animated = true;
    static final int[] iterationCombs = new int[]{100, 500, 1000, 5000, 10000};
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
        Smooth = new JComboBox<String>(new String[]{"Smooth", "Jagged"});
        Smooth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                smooth = !smooth;
                (new redraw()).start();
            }
        });
        frame.add(Smooth);
        orbitLimitCombo = new JComboBox<String>(new String[]{"0", "1", "2", "3", "4", "5"});
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
        iterationLimitCombo.setSelectedIndex(2);
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
        double startX = -0.1123;
        boolean radd = false;
        double endX = -0.1123;
        double startY = -1;
        double endY = 1;
        double ccenterX = ((-1.14865) + (-1.1243)) / 2;
        double ccenterY = (-0.26515 + (-0.2159)) / 2;
        double rad = Math.sqrt((ccenterX - (-1.14865)) * (ccenterX - (-1.14865)) + (ccenterY - (-0.2159)) * (ccenterY - (-0.2159))) * 1.05;
        double total = 60 * 1000;
        double[][] points = {{0.5, 0.}, {0.509743, 0.0014387}, {0.53748, 0.0113132}, {0.578885,
            0.0370981}, {0.62725, 0.0844426}, {0.674181, 0.156488}, {0.71048,
            0.253437}, {0.727115, 0.372422}, {0.716195, 0.507678}, {0.671835,
            0.651017}, {0.590847, 0.792547}, {0.47317, 0.921578}, {0.321998,
            1.02762}, {0.143603, 1.10136}, {-0.0531454, 1.13559}, {-0.257514,
            1.12588}, {-0.457822, 1.07104}, {-0.642451, 0.973279}, {-0.800859,
            0.838023}, {-0.924493, 0.673462}, {-1.00751, 0.489821}, {-1.04727,
            0.298456}, {-1.04449, 0.110842}, {-1.00308, -0.0624308}, {-0.929768,
            -0.212574}, {-0.833333, -0.333333}, {-0.723757, -0.421463},
        {-0.611224, -0.476914}, {-0.505141, -0.502699}, {-0.413237,
            -0.504467}, {-0.340847, -0.489821}, {-0.290455, -0.46745},
        {-0.261514, -0.446166}, {-0.250594, -0.433934}, {-0.25181,
            -0.437004}, {-0.257514, -0.459214}, {-0.259157, -0.501554},
        {-0.248254, -0.562017}, {-0.217347, -0.635761}, {-0.160868,
            -0.715567}, {-0.0758192, -0.792547}, {0.0377972, -0.857028},
        {0.17685, -0.899535}, {0.335258, -0.911767}, {0.504468, -0.887475},
        {0.674181, -0.823154}, {0.833261, -0.71848}, {0.970742, -0.576443},
        {1.07682, -0.40317}, {1.14378, -0.20745}, {1.16667, 0.}, {1.14378,
            0.20745}, {1.07682, 0.40317}, {0.970742, 0.576443}, {0.833261,
            0.71848}, {0.674181, 0.823154}, {0.504468, 0.887475}, {0.335258,
            0.911767}, {0.17685, 0.899535}, {0.0377972, 0.857028}, {-0.0758192,
            0.792547}, {-0.160868, 0.715567}, {-0.217347, 0.635761}, {-0.248254,
            0.562017}, {-0.259157, 0.501554}, {-0.257514, 0.459214}, {-0.25181,
            0.437004}, {-0.250594, 0.433934}, {-0.261514, 0.446166}, {-0.290455,
            0.46745}, {-0.340847, 0.489821}, {-0.413237, 0.504467}, {-0.505141,
            0.502699}, {-0.611224, 0.476914}, {-0.723757, 0.421463}, {-0.833333,
            0.333333}, {-0.929768, 0.212574}, {-1.00308, 0.0624308}, {-1.04449,
            -0.110842}, {-1.04727, -0.298456}, {-1.00751, -0.489821}, {-0.924493,
            -0.673462}, {-0.800859, -0.838023}, {-0.642451, -0.973279},
        {-0.457822, -1.07104}, {-0.257514, -1.12588}, {-0.0531454, -1.13559},
        {0.143603, -1.10136}, {0.321998, -1.02762}, {0.47317, -0.921578},
        {0.590847, -0.792547}, {0.671835, -0.651017}, {0.716195, -0.507678},
        {0.727115, -0.372422}, {0.71048, -0.253437}, {0.674181, -0.156488},
        {0.62725, -0.0844426}, {0.578885, -0.0370981}, {0.53748, -0.0113132},
        {0.509743, -0.0014387}, {0.5, 0.}};
        boolean zach = false;
        String name = radd ? "julia radius " + rad + " around " + ccenterX + "," + ccenterY : "julia_" + startX + "," + startY + "_to_" + endX + "," + endY;
        if (zach) {
            name = "julia zack's pointssss times 2 thirds";
        }
        ImageOutputStream output = new FileImageOutputStream(new File(System.getProperty("user.home") + "/Documents/" + name + ".gif"));
        GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, true);
        int percent = 0;
        while (animated) {
            //double time = System.currentTimeMillis() - start;
            //double p = time / total;
            double p = percent++;
            if (zach) {
                p /= points.length;
            } else {
                p /= 100;
            }
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
            if (zach) {
                juliaX = points[percent][0] * 2 / 3;
                juliaY = points[percent][1] * 2 / 3;
            }
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
            double cY = ((double) (-yWidth * 4)) * yScale / 4 + centerY;
            double incrementX = julia ? juliaX : cX;
            for (int y = -yWidth * 4; y < yWidth * 4; y++) {
                double zX = cX;
                double zY = cY;
                double incrementY = julia ? juliaY : cY;
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
                cY += yScale / 4;
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
            if (!animated) {
                doActualDraw();
            }
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
