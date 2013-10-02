/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package breeding;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class Breeding extends JComponent implements KeyListener{
    static final int DoubleBreeding=Integer.parseInt(JOptionPane.showInputDialog("What's the chance of having one extra child if one parent is ff? (e.g. 100 would mean a 1/100 chance)"));
    static final int HalfBreeding=Integer.parseInt(JOptionPane.showInputDialog("What's the chance of having one extra child if one parent is Ff? (e.g. 200 would mean a 1/200 chance)"));
    static final int KILL=Integer.parseInt(JOptionPane.showInputDialog("What fraction of FFs should be killed? (e.g. 23 would be 1/23 of then)"));
    static Breeding M=new Breeding();
    static JFrame frame;
    static ArrayList<String> output=new ArrayList<String>();
    static int offset=0;
    public Breeding(){
        addKeyListener(this);
    }
    public void paintComponent(Graphics g){
        for (int i=0; i+offset<output.size(); i++){
            g.drawString(output.get(i+offset),10,10+15*i);
            if (10+15*i>frame.getHeight()){
                break;
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
        Random r=new Random(0);
        int bunnies=100000;
        ArrayList<Bunny> start=new ArrayList<Bunny>(bunnies);
        for (int i=0; i<bunnies; i++){
            start.add(new Bunny(false,false,r));
        }
        output.add("Culling 1/"+KILL+" FFs each generation");
        output.add("Listing every generation for first 20, then every 10th");
        output.add("Normal couples have 2 children");
        output.add("If a Ff is one parent, 1/"+HalfBreeding+" chance of having one more child");
        output.add("If a ff is one parent, 1/"+DoubleBreeding+" chance of having one more child");
        output.add("This is additive. If the parents are Ff and ff, then there's a 1/"+DoubleBreeding+"+1/"+HalfBreeding+" chance of having 3 children, and a 1/"+(DoubleBreeding*HalfBreeding)+" chance of having 4 children.");
        output.add("Starting with ");
        print(start);
        for (int i=0; i<=498; i++){
            start=breed(start,r);
            KILLSOMEBUNNIES(start,r);
            //if (i%10==8 || i<=18){
            output.add("Generation "+(i+2)+", there are ");
            print(start);
            M.repaint();
            //}
        }
    }
    public static ArrayList<Bunny> breed(ArrayList<Bunny> bunnies, Random r){
        ArrayList<Bunny> N=new ArrayList<Bunny>();
        while(bunnies.size()>1){
            Bunny a=bunnies.remove(r.nextInt(bunnies.size()));
            Bunny b=bunnies.remove(r.nextInt(bunnies.size()));
            /*boolean verbose=a.Trait1||a.Trait2||b.Trait1||b.Trait2;
            if (verbose){
                System.out.println(a.Trait1+","+a.Trait2+":"+b.Trait1+","+b.Trait2);
            }*/
            int children=2;
            if (r.nextInt(DoubleBreeding)==0){
                if (a.Trait1&&a.Trait2){
                    children++;
                }
                if (b.Trait1&&b.Trait2){
                    children++;
                }
            }
            if (r.nextInt(HalfBreeding)==0){
            if (a.Trait1||a.Trait2 && !(a.Trait1&&a.Trait2)){
                children++;
            }
            if (b.Trait1||b.Trait2 && !(b.Trait1&&b.Trait2)){
                children++;
            }
            }
            for (int i=0; i<children; i++){
                Bunny c=new Bunny(a,b,r);
                /*if (verbose){
                    System.out.println(c.Trait1+","+c.Trait2);
                    if (c.Trait1||c.Trait2){
                        System.out.println(++kitties);
                    }
                    System.out.println(++meow);
                }*/
                N.add(c);
            }
        }
        return N;
    }
    public static void KILLSOMEBUNNIES(ArrayList<Bunny> SLAUGHTERLIST, Random KILLCHOOSER){
        int KILLRATIO=KILL;
        for (int BUNNYTOBEKILLED=0; BUNNYTOBEKILLED<SLAUGHTERLIST.size()-1; BUNNYTOBEKILLED++){
            if (SLAUGHTERLIST.get(BUNNYTOBEKILLED).SHOULDDIE() && KILLCHOOSER.nextInt(KILLRATIO)==0){
                SLAUGHTERLIST.remove(BUNNYTOBEKILLED);
            }
        }
    }
    public static void print(ArrayList<Bunny> bunnies){
        int normals=0;
        int doubloon=0;
        for (Bunny b : bunnies){
            if (b.Trait1&&b.Trait2){
                doubloon++;
            }
            if ((!b.Trait1)&&(!b.Trait2)){
                normals++;
            }
        }
        output.set(output.size()-1,output.get(output.size()-1)+(bunnies.size()+" bunnies, "+normals+" FF, "+(bunnies.size()-(normals+doubloon))+" Ff, "+doubloon+" ff."));
        //System.out.println(bunnies.size()+","+normals+","+(bunnies.size()-(normals+doubloon))+","+doubloon);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==38){
            if (offset>0){
                offset--;
            }
            M.repaint();
        }
        if (e.getKeyCode()==40){
            
            if (offset<output.size()-5){
                offset++;
            }
            M.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
