/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dragon_curve;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class Dragon_Curve extends JComponent{
    static Dragon_Curve M=new Dragon_Curve();
    static JFrame frame;
    /**
     * @param args the command line arguments
     */
    static int iterations=0;
    
    static boolean[] dir=new boolean[]{true,true,false};
    public static void next(){
        synchronized(dir){
        iterations++;
        
        boolean[] N=new boolean[dir.length*2+1];
        for (int i=0; i<dir.length; i++){
            
            N[i]=dir[i];
            N[2*dir.length-i]=!dir[i];
        }
        
        N[dir.length]=true;
        dir=N;
        
        
        }
    }
    public void paintComponent(Graphics g){
        int dist=2;
        int xPos=frame.getWidth()/2;
        int yPos=frame.getHeight()/2;
        synchronized(dir){
        int cdir=0;//0: up, 1: right, 2:down, 3:left
        cdir=3*(iterations/2);
        for (int i=0; i<=dir.length; i++){
            if (cdir==0){
                
            }
            g.drawLine(xPos,yPos,xPos+(cdir==1?dist:cdir==3?-dist:0),yPos+(cdir==0?-dist:cdir==2?dist:0));
            xPos=xPos+(cdir==1?dist:cdir==3?-dist:0);
            yPos=(yPos+(cdir==0?-dist:cdir==2?dist:0));
            cdir--;
            if (i==500){
                System.out.println( (0-(yPos-(frame.getHeight()/2))/2) +","+ (0-(xPos-(frame.getWidth()/2))/2));
            }
            //System.out.println(dir[i]);
            if (i!=dir.length && dir[i]){
                cdir++;cdir++;
            }
            cdir%=4;
            cdir=cdir==-1?3:cdir;
        }
        System.out.println("end");
        }
    }
    public static void main(String[] args) {
        // TODO code application logic here
        //next();
        //next();
        int i=Integer.parseInt(JOptionPane.showInputDialog("Iteration?"));
        i=i%2!=0?i-1:i;
        for (int x=0; x<i; x++){
            System.out.println(x);
            next();
        }
        frame = new JFrame("Lurf's Dragon Curve Of Awesomeness");
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
}
