/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hilbertcurve;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author leif
 */
public class HilbertCurve extends JComponent implements ActionListener {

    static HilbertCurve M = new HilbertCurve();
    static int iterations = 0;
    static double size = 2;
    static boolean oom = false;
    static JFrame frame;
    static boolean drawww = false;
    /**
     * @param args the command line arguments
     */
    static boolean[] UR = new boolean[1];
    static boolean[] UD = new boolean[1];
    static int i = 0;

    public static void add(boolean[] a, boolean b) {
        a[i] = b;

    }

    public static void Add(boolean[] a, boolean[] b, boolean c, boolean d) {
        add(a, c);
        add(b, d);
        i++;
    }
    static int linefail = 0;
    static final boolean allocateAtBeginning = true;

    public static void next() {
        i = 0;
        boolean[] nUR;
        boolean[] nUD;
        //if (allocateAtBeginning){
        try {
            //System.out.println("DURP");
            nUR = new boolean[4 * UR.length];
            nUD = new boolean[4 * UD.length];
        } catch (OutOfMemoryError e) {
            nUR = new boolean[0];
            System.gc();
            nUD = new boolean[0];
            handleOOM();
            return;
        }
        //}
        boolean xPos = false;
        boolean yPos = false;
        for (int S = 0; S < UR.length; S++) {
            //int i=0;
            //add(ur,false);
            //add(ud,false);
            //while(!UR.isEmpty()({
            //UR.remove(0);
            //UD.remove(
            try {
                if (UR[S]) {
                    if (UD[S]) {
                        //Up
                        if (!xPos && !yPos) {//Bottom Left
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, false, false);
                            Add(nUR, nUD, true, true);
                        }
                        if (xPos && !yPos) {//Bottom Right
                            Add(nUR, nUD, false, false);
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, true, true);
                        }
                        if (!xPos && yPos) {//Top Left
                            Add(nUR, nUD, false, true);
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, true, true);
                            xPos = true;
                            yPos = false;
                        } else {
                            if (xPos && yPos) {//Top Right
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, true, true);
                                Add(nUR, nUD, true, true);
                                yPos = false;
                                xPos = false;
                            }
                        }
                    } else {
                        //Right
                        if (!xPos && !yPos) {//Bottom Left
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, false, true);
                            Add(nUR, nUD, true, false);
                        }
                        if (xPos && !yPos) {//Bottom Right
                            Add(nUR, nUD, false, false);
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, true, false);
                            yPos = true;
                            xPos = false;
                        } else {
                            if (!xPos && yPos) {//Top Left
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, true, false);
                                Add(nUR, nUD, true, true);
                                Add(nUR, nUD, true, false);
                            }
                            if (xPos && yPos) {//Top Right
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, true, false);
                                Add(nUR, nUD, true, false);
                                yPos = false;
                                xPos = false;
                            }
                        }
                    }
                } else {
                    if (UD[S]) {
                        //Down
                        if (!xPos && !yPos) {//Bottom Left
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, false, true);
                            Add(nUR, nUD, false, true);
                            xPos = true;
                            yPos = true;
                        } else {
                            if (xPos && !yPos) {//Bottom Right
                                Add(nUR, nUD, true, false);
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, false, true);
                                xPos = false;
                                yPos = true;
                            } else {
                                if (!xPos && yPos) {//Top Left
                                    Add(nUR, nUD, true, false);
                                    Add(nUR, nUD, false, true);
                                    Add(nUR, nUD, false, false);
                                    Add(nUR, nUD, false, true);
                                }
                                if (xPos && yPos) {//Top Right
                                    Add(nUR, nUD, false, false);
                                    Add(nUR, nUD, false, true);
                                    Add(nUR, nUD, true, false);
                                    Add(nUR, nUD, false, true);
                                }
                            }
                        }
                    } else {
                        //Left
                        //System.out.println(xPos + "" + yPos);
                        if (!xPos && !yPos) {//Bottom Left
                            Add(nUR, nUD, true, false);
                            Add(nUR, nUD, true, true);
                            Add(nUR, nUD, false, false);
                            Add(nUR, nUD, false, false);
                            xPos = true;
                            yPos = true;
                        } else {
                            if (xPos && !yPos) {//Bottom Right
                                Add(nUR, nUD, true, true);
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, false, false);
                            }
                            if (!xPos && yPos) {//Top Left
                                Add(nUR, nUD, true, false);
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, false, false);
                                xPos = true;
                                yPos = false;
                            }
                            if (xPos && yPos) {//Top Right
                                Add(nUR, nUD, false, true);
                                Add(nUR, nUD, false, false);
                                Add(nUR, nUD, true, true);
                                Add(nUR, nUD, false, false);
                            }

                        }
                    }
                }
            } catch (OutOfMemoryError e) {
                nUR = new boolean[0];
                System.gc();
                nUD = new boolean[0];
                handleOOM();
                System.out.println("Failed on line " + S);
                linefail = S;
                return;
            }
        }
        UR = nUR;
        UD = nUD;
    }
//LOL, from here on down, it's just displaying it and handleing 
    //it when some derp decides to find out how many iterations it can go

    public static void handleOOM() {

        System.gc();
        UR = new boolean[0];
        UD = new boolean[0];
        oom = true;
        M.repaint();
    }

    public void paintComponent(Graphics g) {
        if (oom) {
            g.drawString("Out of memory! I'll bet you were all like 'Oh, lets see how many iterations it can go to', eh? ", 10, 10);
            g.drawString("WELL NOW YOU KNOW. DON'T DO IT AGAIN. You ran out of memory on your computer.", 10, 25);

            return;
        }
        /*
         switch(iterations){
         case 0:
         size=600;
         break;
         case 1:
         size=100;
         break;
         case 2:
         size=50;
         break;
         case 3:
         size=25;
         break;
         case 4:
         size=20;
         break;
         case 5:
         size=10;
         case 6:
         size=5;
         break;
         case 7:
         size=2;
         break;
         }*/
        size = (((600)) / ((Math.pow(2, iterations + 1)) - 0));
        size = size < 2 ? 2 : size;
        //size=10;
        //600=size*(iterations+1)
        double xPos = frame.getWidth() / 2 - (int) (((float) size * (Math.pow(2, iterations + 1) - 1)) / 2);
        //int xPos=1;
        //System.out.println(size);
        xPos = xPos < 2 ? 1 : xPos;
        double yPos = frame.getHeight() - 40;
        ArrayList<Integer> XXX = new ArrayList<Integer>();
        ArrayList<Integer> YYY = new ArrayList<Integer>();
        XXX.add((int) xPos);
        YYY.add((int) yPos);
        //boolean drawww=true;
        try {
            for (int i = 0; i < UD.length - 1 && !oom; i++) {
                int a = (int) xPos;
                int b = (int) yPos;
                if (UR[i]) {
                    if (UD[i]) {
                        //System.out.print("u");
                        //if (drawww) {
                        //g.drawLine((int) xPos, (int) yPos, (int) xPos, (int) (yPos - size));
                        //}
                        yPos -= size;

                    } else {
                        //System.out.print("r");
                        //if (drawww) {
                        //g.drawLine((int) xPos, (int) yPos, (int) (xPos + size), (int) yPos);
                        //}
                        xPos += size;
                    }
                } else {
                    if (UD[i]) {
                        //System.out.print("d");
                        //if (drawww) {
                        //g.drawLine((int) xPos, (int) yPos, (int) xPos, (int) (yPos + size));
                        //}
                        yPos += size;
                    } else {
                        //System.out.print("l");

                        //g.drawLine((int) xPos, (int) yPos, (int) (xPos - size), (int) yPos);

                        xPos -= size;
                    }
                }
                if (drawww) {
                    g.drawLine(a, b, (int) xPos, (int) yPos);
                }
                XXX.add((int) xPos);
                YYY.add((int) yPos);
            }
        } catch (NullPointerException e) {
        }
        //System.out.println(size);
        if (!drawww) {
            for (int i = 1; i < XXX.size() - 1; i++) {
                int xp=(int)XXX.get(i-1);
                int x=(int)XXX.get(i);
                int xn=(int)XXX.get(i+1);
                int yp=(int)YYY.get(i-1);
                int y=(int)YYY.get(i);
                int yn=(int)YYY.get(i+1);
                int zachispoopy = (int) size;
                if (xp!= xn && yp!= yn) {
                    int angle = 0;
                    if (yn > yp) {
                        if (xn > xp) {
                            angle = 0;
                            //g.drawString("CATS",XXX.get(i),YYY.get(i));
                            if (x == xp) {
                                angle = 2;
                                y-=zachispoopy;
                            }else{
                                x-=zachispoopy;
                            }

                            //YY=-KERMIT;
                        } else {
                            angle = 3;
                            if (x == xn) {
                                angle = 1;

                            } else {
                                x-=zachispoopy;
                                y-=zachispoopy;
                            }

                            //XX=KERMIT;
                            //YY=-KERMIT;
                        }
                    } else {
                        if (xn > xp) {
                            angle = 1;
                            if (x != xp) {
                                x-=zachispoopy;
                                y-=zachispoopy;
                                angle = 3;
                            }

                            //XX=300;
                            //YY=-KERMIT;
                        } else {
                            angle = 2;
                            if (x == xp) {
                                angle = 0;
                                x-=zachispoopy;
                            } else {
                                y-=zachispoopy;
                            }


                            //XX=-KERMIT;
                            //YY=KERMIT;
                        }
                    }
                    g.drawArc(x, y, zachispoopy, zachispoopy, angle*90, 90);

                } else {
                    zachispoopy/=2;
                    if (xp > xn) {
                        //g.drawLine(XXX.get(i - 1) - (int) size / 2, YYY.get(i - 1), XXX.get(i + 1) + (int) size / 2, YYY.get(i + 1));
                        xp -= zachispoopy;
                        xn += zachispoopy;
                    }
                    if (xp < xn) {
                        //g.drawLine(XXX.get(i - 1) + (int) size / 2, YYY.get(i - 1), XXX.get(i + 1) - (int) size / 2, YYY.get(i + 1));
                        xp += zachispoopy;
                        xn -= zachispoopy;
                    }
                    if (yp > yn) {
                        //g.drawLine(XXX.get(i - 1), YYY.get(i - 1) - (int) size / 2, XXX.get(i + 1), YYY.get(i + 1) + (int) size / 2);
                        yp -= zachispoopy;
                        yn += zachispoopy;
                    }
                    if (yp < yn) {
                        //g.drawLine(XXX.get(i - 1), YYY.get(i - 1) + (int) size / 2, XXX.get(i + 1), YYY.get(i + 1) - (int) size / 2);
                        yp += zachispoopy;
                        yn -= zachispoopy;
                    }
                    g.drawLine(xp, yp, xn, yn);
                }
            }
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 30);
        g.setColor(Color.RED);
        g.drawString("Iteration: " + (iterations + 1), 0, 10);
        g.drawString("Sides: " + (UR.length - 1), 0, 25);
        g.drawString("Memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage(), 10, frame.getHeight() - 25);

    }

    public static void calc() {
        UR = new boolean[1];
        UD = new boolean[1];
        i = 0;
        Add(UR,UD,true,false);

        for (int i = 0; i <= iterations && !oom; i++) {
            next();
        }
    }

    public HilbertCurve() {
        JButton b1 = new JButton("Next");
        b1.setVerticalTextPosition(AbstractButton.TOP);
        b1.setHorizontalTextPosition(AbstractButton.LEADING);
        b1.setActionCommand("Next");
        b1.addActionListener(this);
        add(b1);
        JButton b2 = new JButton("Prev");
        b2.setVerticalTextPosition(AbstractButton.TOP);
        b2.setHorizontalTextPosition(AbstractButton.CENTER);
        b2.setActionCommand("Prev");
        b2.addActionListener(this);
        add(b2);
        JButton b3 = new JButton("Toggle Curves");
        b3.setVerticalTextPosition(AbstractButton.TOP);
        b3.setHorizontalTextPosition(AbstractButton.CENTER);
        b3.setActionCommand("Toggle");
        b3.addActionListener(this);
        add(b3);
    }

    public static void setupScreen() {
        frame = new JFrame("Lurf's Hilbert Curve Of Awesomeness");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        //frame.setSize(10, 10);
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {

        //(x+iy)*(a+ib)=1
        //a=x/(x^2+y^2)
        //b=-(y/(x^2+y^2))
        calc();
        setupScreen();
    }

    public static class calcu extends Thread {

        public void run() {
            calc();
            System.gc();
            M.repaint();
        }
    }

    public static class nextw extends Thread {

        public void run() {
            next();
            System.gc();
            M.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Next")) {
            iterations++;
            (new nextw()).start();
        }
        if (ae.getActionCommand().equals("Toggle")) {
            drawww = !drawww;
            M.repaint();
        }
        if (ae.getActionCommand().equals("Prev")) {
            if (iterations > 0) {
                iterations--;
            }
            if (oom) {
                iterations = 0;
                oom = false;
            }
            (new calcu()).start();
        }

    }
}