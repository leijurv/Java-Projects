/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package alias;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import sun.awt.image.ToolkitImage;

/**
 *
 * @author leijurv
 */
public class Alias extends JComponent implements MouseListener,MouseMotionListener{
    static Alias M=new Alias();
    static BufferedImage old;
    static Image displayOld;
    static BufferedImage New;
    static int newX=400;
    static int newY=300;
    static int mouseX=0;
    static int mouseY=0;
    static long A=0;
    static long B=0;
    public Alias(){
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    public void paintComponent(Graphics g){
        g.drawImage(displayOld,0,0,null);
        g.drawImage(New,newX+50,0,null);
        g.setColor(Color.RED);
        for (int i=1; i<newX/100; i++){
            g.drawLine(i*100,0,i*100,2000);
            g.drawLine(i*100+newX+50,0,i*100+newX+50,2000);
        }
        for (int i=1; i<newY/100; i++){
            g.drawLine(0,i*100,2000,i*100);
        }
        g.setColor(Color.BLUE);
        g.drawLine(0,mouseY,2000,mouseY);
        g.drawLine(mouseX,0,mouseX,2000);
        g.drawLine(mouseX+newX+50,0,mouseX+newX+50,2000);
        g.drawLine(mouseX-(newX+50),0,mouseX-(newX+50),2000);
        g.setColor(Color.RED);
        g.drawString("Java Image Scaling: "+A+"ms My Image Scaling: "+B+" ms",10,10);
    }
    public static void main(String[] args) throws Exception{
        JFrame frame=new JFrame("Image Scaler");
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	frame.setVisible(true);
        old=ImageIO.read(new File("/Users/leijurv/Downloads/kittens.jpg"));
        recalc();
        
    }
    public static void recalc(){
        double oldx=old.getWidth();
        double oldy=old.getHeight();
        double newx=newX;
        double newy=newY;
        long a=System.currentTimeMillis();
        Image n = old.getScaledInstance((int)newx,(int)newy,Image.SCALE_SMOOTH);
        displayOld=new BufferedImage((int)newx,(int)newy,BufferedImage.TYPE_INT_RGB);
        Graphics g = ((BufferedImage)displayOld).createGraphics();
        g.drawImage(n,0,0,null);
        g.dispose();
        long b=System.currentTimeMillis();
        New=new BufferedImage((int)newx,(int)newy,BufferedImage.TYPE_INT_RGB);
        for (double x=0; x<oldx; x++){
            for (double y=0; y<oldy; y++){
                double NewX=x/oldx*newx;
                double NewY=y/oldy*newy;
                for (double X=Math.floor(NewX); X<Math.ceil(NewX+newx/oldx) && X<newx; X++){
                    for (double Y=Math.floor(NewY); Y<Math.ceil(NewY+newy/oldy) && Y<newy; Y++){
                        //(x,y) in old image is partially inside of (X,Y) in new image
                        double amtX=NewX>X?NewX+newx/oldx<X+1?newx/oldx:X+1-NewX:NewX+newx/oldx<X+1?NewX+newx/oldx-X:1;
                        double amtY=NewY>Y?NewY+newy/oldy<Y+1?newy/oldy:Y+1-NewY:NewY+newy/oldy<Y+1?NewY+newy/oldy-Y:1;
                        double multiple=amtX*amtY;
                        Color Old=new Color(old.getRGB((int)x,(int)y));
                        Color Neo=new Color(New.getRGB((int)X,(int)Y));
                        int red=(int)(Old.getRed()*multiple+Neo.getRed());
                        int green=(int)(Old.getGreen()*multiple+Neo.getGreen());
                        int blue=(int)(Old.getBlue()*multiple+Neo.getBlue());
                        Color N=new Color(red>255?255:red,green>255?255:green,blue>255?255:blue);
                        New.setRGB((int)X,(int)Y,N.getRGB());
                    }
                }
            }
        }
        long c=System.currentTimeMillis();
        A=b-a;
        B=c-b;
        M.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        newX=e.getX();
        newY=e.getY();
        recalc();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX=e.getX();
        mouseY=e.getY();
        M.repaint();
    }
}
