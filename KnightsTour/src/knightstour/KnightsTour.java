/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knightstour;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class KnightsTour extends JComponent{
    static ArrayList<Neuron> n;
    static KnightsTour M=new KnightsTour();
    static int wid=Integer.parseInt(JOptionPane.showInputDialog("Board Size? (n*n) (Knights tour is only possible if even)"));//n*n board. 8 means normal chessboard
    static boolean kitten=false;
    static boolean done=false;
    static boolean shouldStart=false;
    static boolean switching=false;
    static boolean showData=false;
    static int iterations=0;
    public void paintComponent(Graphics g){
        g.drawString("Mode 2: "+kitten,10,10);
        g.drawString("Iterations: "+iterations,10,25);
        g.drawString("Loopsize: "+elements,10,40);
        int squareSize=600/wid;
        int offset=50;
        
        int r=offset+squareSize/2;
        for (int x=0; x<wid; x++){
            for (int y=0; y<wid; y++){
                g.setColor(Color.BLACK);
                if ((x+y)%2==0){//Every other square is white
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x*squareSize+offset, y*squareSize+offset, squareSize, squareSize);
            }
        }
        
        ArrayList<Position> loop=new ArrayList<Position>();
        if (done){
        loop.add(new Position(0,0));
        boolean go=true;
        do{
        Position prev=loop.get(loop.size()-1);
        boolean next=true;
        for (int i=0; i<n.size()&&next; i++){
            Neuron N=n.get(i);
            if (((N.a.equals(prev)&&!loop.contains(N.b))||(N.b.equals(prev)&&!loop.contains(N.a)))&&N.curV==1){
                if (N.a.equals(prev)){
                    loop.add(N.b);
                }else{
                    loop.add(N.a);
                }
                next=false;
            }
        }
        
        if (next){
            go=false;
        }
        //System.out.println((loop.size()<3)+" "+(!loop.get(loop.size()-1).equals(new Position(2,1)))+"a"+loop);
        }while((loop.size()<3||!loop.get(loop.size()-1).equals(new Position(2,1)))&&go);
        }
        elements=loop.size();
        //if (!loop.isEmpty())
        //System.out.println(loop);
        for (Neuron N : n){
            g.setColor(Color.blue);
            if (loop.contains(N.a)){
                g.setColor(Color.red);
            }
            //if(true){
            if (N.curV==1){
                g.drawLine(N.a.x*squareSize+r, N.a.y*squareSize+r,N.b.x*squareSize+r,N.b.y*squareSize+r);
                g.setColor(Color.orange);
                if (showData)
                g.drawString(N.curU+"",(N.a.x*squareSize+r+N.b.x*squareSize+r)/2,(N.a.y*squareSize+r+N.b.y*squareSize+r)/2);
            }
        }
        
        
    }
    public static boolean valid(int x, int y){
        if (x<0){
            return false;
        }
        if (y<0){
            return false;
        }
        if (y>wid-1){
            return false;
        }
        if (x>wid-1){
            return false;
        }
        return true;
    }
    static int elements=0;
    static final int[][] moves={{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
public static void go(){
    switching=false;
                shouldStart=true;
                new Thread(){
                    public void run(){
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                }
                switching=true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                }
                switching=false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                }
                kitten=false;
                    }}.start();
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        Random r=new Random();
        n=new ArrayList<Neuron>();
        ArrayList<Position> p=new ArrayList<Position>();
        for (int x=0; x<wid; x++){
            for (int y=0; y<wid; y++){
                p.add(new Position(x,y));
            }
        }
        for (int x=0; x<wid; x++){
            for (int y=0; y<wid; y++){
                Position a=p.get(y+x*wid);
                for (int i=0; i<moves.length; i++){
                    int X=x+moves[i][0];
                    int Y=y+moves[i][1];
                    if (valid(X,Y)){
                        Neuron N=new Neuron();
                        N.a=a;
                        N.b=p.get(Y+X*wid);
                        if (!n.contains(N)){
                            
                            N.meow();
                            n.add(N);
                        }
                    }
                }
            }
        }
        JFrame frame=new JFrame("KT");
        
        M.setLayout(new FlowLayout());
        JButton j=new JButton("Toggle mode switching");
        
        j.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                switching=!switching;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                }
                kitten=false;
            }
        });
        M.add(j);
        JButton J=new JButton("Restart");
        
        J.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                go();
            }
        });
        M.add(J);
        JButton JJ=new JButton("Show/Hide State");
        
        JJ.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                showData=!showData;
            }
        });
        M.add(JJ);
        M.setFocusable(true);
        frame.setSize(700,800);
        frame.setContentPane(M);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
	  });
        
        new Thread(){
            public void run(){
                System.out.println("Thread 1 started");
                while (true){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    M.repaint();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                System.out.println("Thread 2 started");
                while (true){
                    if (done){
                        switching=false;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    while(switching){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    kitten=!kitten;
                    }
                }
            }
        }.start();
        new Thread(){
            public void run(){
                System.out.println("Thread 3 started");
                while (true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (shouldStart){
                        Random r=new Random();
                        shouldStart=false;
                        for (Neuron N : n){
            N.curV=r.nextInt(2);
            N.curU=0;
        }
                        done=false;
                        kitten=false;
                        iterations=0;
        while(!done&&!shouldStart&&iterations<500000){
            for (Neuron u : n){
                u.tick();
            }
            done=true;
            for (Neuron u : n){
                u.dub();
                if (!u.same){
                    done=false;
                }
            }
            iterations++;
        }
        if (!shouldStart){ //Don't say finished if restarted when not done
        System.out.println("Finished!");
           }
        if (iterations==500000){
            go();
        }
        
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(KnightsTour.class.getName()).log(Level.SEVERE, null, ex);
                        }
           if (elements!=wid*wid){
               go();
           }
                    }
                }
            }
        }.start();
        go();
    }
}
