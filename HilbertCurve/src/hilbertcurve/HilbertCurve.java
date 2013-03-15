/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hilbertcurve;

import java.awt.*;
import java.awt.event.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
//import java.util.*;
import javax.swing.*;
import sun.instrument.InstrumentationImpl;

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
    /**
     * @param args the command line arguments
     */
    static boolean[] UR = new boolean[1];
    static boolean[] UD = new boolean[1];
    static int i=0;
    public static void add(boolean[] a, boolean b){
        a[i]=b;
        
    }
    public static void addL(boolean[] ur, boolean[] ud) {
        add(ur,false);
        add(ud,false);
        i++;
    }

    public static void addR(boolean[] ur, boolean[] ud) {
        add(ur,true);
        add(ud,false);
        i++;
    }

    public static void addU(boolean[] ur, boolean[] ud) {
        add(ur,true);
        add(ud,true);
        i++;
    }

    public static void addD(boolean[] ur, boolean[] ud) {
        add(ur,false);
        add(ud,true);
        i++;
    }
    
    static int linefail=0;
    
static final boolean allocateAtBeginning=true;
    public static void next() {
        i=0;
        boolean[] nUR=new boolean[1];
        boolean[] nUD=new boolean[1];
        //if (allocateAtBeginning){
        try{
            //System.out.println("DURP");
        nUR = new boolean[4*UR.length];
        nUD = new boolean[4*UD.length];
        } catch (OutOfMemoryError e) {
                nUR=new boolean[0];
                System.gc();
                nUD=new boolean[0];
                handleOOM();
                return;
            }
        //}
        boolean xPos = false;
        boolean yPos = false;
        for (int i = 0; i < UR.length; i++) {
            //int i=0;
            //add(ur,false);
            //add(ud,false);
            //while(!UR.isEmpty()({
            //UR.remove(0);
            //UD.remove(
            try {
                if (UR[i]) {
                    if (UD[i]) {
                        //Up
                        if (!xPos && !yPos) {//Bottom Left
                            addR(nUR, nUD);
                            addU(nUR, nUD);
                            addL(nUR, nUD);
                            addU(nUR, nUD);
                        }
                        if (xPos && !yPos) {//Bottom Right
                            addL(nUR, nUD);
                            addU(nUR, nUD);
                            addR(nUR, nUD);
                            addU(nUR, nUD);
                        }
                        if (!xPos && yPos) {//Top Left
                            addD(nUR, nUD);
                            addR(nUR, nUD);
                            addU(nUR, nUD);
                            addU(nUR, nUD);
                            xPos = true;
                            yPos = false;
                        } else {
                            if (xPos && yPos) {//Top Right
                                addD(nUR, nUD);
                                addL(nUR, nUD);
                                addU(nUR, nUD);
                                addU(nUR, nUD);
                                yPos = false;
                                xPos = false;
                            }
                        }
                    } else {
                        //Right
                        if (!xPos && !yPos) {//Bottom Left
                            addU(nUR, nUD);
                            addR(nUR, nUD);
                            addD(nUR, nUD);
                            addR(nUR, nUD);
                        }
                        if (xPos && !yPos) {//Bottom Right
                            addL(nUR, nUD);
                            addU(nUR, nUD);
                            addR(nUR, nUD);
                            addR(nUR, nUD);
                            yPos = true;
                            xPos = false;
                        } else {
                            if (!xPos && yPos) {//Top Left
                                addD(nUR, nUD);
                                addR(nUR, nUD);
                                addU(nUR, nUD);
                                addR(nUR, nUD);
                            }
                            if (xPos && yPos) {//Top Right
                                addL(nUR, nUD);
                                addD(nUR, nUD);
                                addR(nUR, nUD);
                                addR(nUR, nUD);
                                yPos = false;
                                xPos = false;
                            }
                        }
                    }
                } else {
                    if (UD[i]) {
                        //Down
                        if (!xPos && !yPos) {//Bottom Left
                            addU(nUR, nUD);
                            addR(nUR, nUD);
                            addD(nUR, nUD);
                            addD(nUR, nUD);
                            xPos = true;
                            yPos = true;
                        } else {
                            if (xPos && !yPos) {//Bottom Right
                                addR(nUR, nUD);
                                addL(nUR, nUD);
                                addD(nUR, nUD);
                                addD(nUR, nUD);
                                xPos = false;
                                yPos = true;
                            } else {
                                if (!xPos && yPos) {//Top Left
                                    addR(nUR, nUD);
                                    addD(nUR, nUD);
                                    addL(nUR, nUD);
                                    addD(nUR, nUD);
                                }
                                if (xPos && yPos) {//Top Right
                                    addL(nUR, nUD);
                                    addD(nUR, nUD);
                                    addR(nUR, nUD);
                                    addD(nUR, nUD);
                                }
                            }
                        }
                    } else {
                        //Left
                        //System.out.println(xPos + "" + yPos);
                        if (!xPos && !yPos) {//Bottom Left
                            addR(nUR, nUD);
                            addU(nUR, nUD);
                            addL(nUR, nUD);
                            addL(nUR, nUD);
                            xPos = true;
                            yPos = true;
                        } else {
                            if (xPos && !yPos) {//Bottom Right
                                addU(nUR, nUD);
                                addL(nUR, nUD);
                                addD(nUR, nUD);
                                addL(nUR, nUD);
                            }
                            if (!xPos && yPos) {//Top Left
                                addR(nUR, nUD);
                                addD(nUR, nUD);
                                addL(nUR, nUD);
                                addL(nUR, nUD);
                                xPos = true;
                                yPos = false;
                            }
                            if (xPos && yPos) {//Top Right
                                addD(nUR, nUD);
                                addL(nUR, nUD);
                                addU(nUR, nUD);
                                addL(nUR, nUD);
                            }

                        }
                    }
                }
            } catch (OutOfMemoryError e) {
                nUR=new boolean[0];
                System.gc();
                nUD=new boolean[0];
                handleOOM();
                System.out.println("Failed on line "+i);
                linefail= i;
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
        UR=new boolean[0];
        UD=new boolean[0];
        oom = true;
        M.repaint();
    }

    public void paintComponent(Graphics g) {
        if (oom) {
            g.drawString("Out of memory! I'll bet you were all like 'Oh, lets see how many iterations it can go to', eh? ", 10, 10);
            g.drawString("WELL NOW YOU KNOW. DON'T DO IT AGAIN. You ran out of memory on your computer.",10,25);
           
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
        size=(((600))/((Math.pow(2,iterations+1))-1));
        size=size<2?2:size;
        //size=10;
        //600=size*(iterations+1)
        double xPos = frame.getWidth()/2-(int)(((float)size*(Math.pow(2,iterations+1)-1))/2);
        //int xPos=1;
        //System.out.println(size);
        xPos=xPos<2?1:xPos;
        double yPos = frame.getHeight() - 36;
        try {
            for (int i = 0; i < UD.length-1 && !oom; i++) {
                if (UR[i]) {
                    if (UD[i]) {
                        //System.out.print("u");
                        g.drawLine((int)xPos, (int)yPos, (int)xPos, (int)(yPos - size));
                        yPos -= size;
                    } else {
                        //System.out.print("r");
                        g.drawLine((int)xPos, (int)yPos, (int)(xPos + size), (int)yPos);
                        xPos += size;
                    }
                } else {
                    if (UD[i]) {
                        //System.out.print("d");
                        g.drawLine((int)xPos, (int)yPos, (int)xPos, (int)(yPos + size));
                        yPos += size;
                    } else {
                        //System.out.print("l");
                        g.drawLine((int)xPos, (int)yPos, (int)(xPos - size), (int)yPos);
                        xPos -= size;
                    }
                }
            }
        } catch (NullPointerException e) {
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 30);
        g.setColor(Color.RED);
        g.drawString("Iteration: " + (iterations+1), 0, 10);
        g.drawString("Sides: " + (UR.length - 1), 0, 25);
        g.drawString("Memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage(), 10, frame.getHeight() - 25);

    }

    public static void calc() {
        UR=new boolean[1];
        UD=new boolean[1];
        i=0;
        addR(UR, UD);
        
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
    }

    public static void setupScreen() {
        frame = new JFrame("Lurf's Hilbert Curve Of Awesomeness");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(10, 10);
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
        public void run(){
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