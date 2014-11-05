/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler147;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class Euler147 {
    static double rot;
    static JFrame frame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        frame=new JFrame("147");
        //int n=Integer.parseInt(JOptionPane.showInputDialog("n?"));
        //int m=Integer.parseInt(JOptionPane.showInputDialog("m?"));
        rot=0;
        frame.setContentPane(new JComponent(){
           public void paintComponent(Graphics g){
               draw(g);
               /*
               if (Math.cos(rot)<0){
                   g.drawLine(cent2x-10,270,cent2x-10,240);
               }else{
                   g.drawLine(cent2x+10,270,cent2x+10,240);
               }
               if (Math.cos(rot+Math.PI)<0){
                   g.drawLine(cent1x+10,270,cent1x+10,240);
               }else{
                   g.drawLine(cent1x-10,270,cent1x-10,240);
               }*/
               
               //g.drawLine(150,600,150,400);
               /*
                int size=50;
                int x=20;
                int y=20;
                for (int X=0; X<=n; X++){
                    g.drawLine(x+size*X,y,x+size*X,y+size*m);
                    int xx=X;
                    int yy=0;
                    while(xx<n && yy<m){
                        xx++;
                        yy++;
                    }
                    g.drawLine(x+size*X,y,x+size*xx,y+size*yy);
                    xx=X;
                    yy=0;
                    while(xx>0 && yy<m){
                        xx--;
                        yy++;
                    }
                    g.drawLine(x+size*X,y,x+size*xx,y+size*yy);
                    
                }
                for (int Y=0; Y<=m; Y++){
                    
                    g.drawLine(x,y+size*Y,x+size*n,y+size*Y);
                    
                    int xx=0;
                    int yy=Y;
                    while(xx<n && yy<m){
                        xx++;
                        yy++;
                    }
                    
                    g.drawLine(x,y+size*Y,x+size*xx,y+size*yy);
                    
                    xx=n;
                    yy=Y;
                    while(xx>0 && yy<m){
                        xx--;
                        yy++;
                    }
                    g.drawLine(x+size*n,y+size*Y,x+size*xx,y+size*yy);
                }
                */
           } 
        });
        frame.setSize(2000,2000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        

      // create a new BufferedOutputStream with the last argument
      ImageOutputStream output = 
        new FileImageOutputStream(new File("/Users/leijurv/Desktop/steam.gif"));
      
      // create a gif sequence with the type of the first image, 1 second
      // between frames, which loops continuously
      GifSequenceWriter writer = 
        new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1, false);
      
      
      
        while(rot<Math.PI*2-0.01){
            BufferedImage image=new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);
            draw(image.getGraphics());
            writer.writeToSequence(image);
            frame.repaint();
            Thread.sleep(50);
            rot+=Math.PI/32;
        }
        writer.close();
      output.close();
    }
    public static void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,2000,2000);
        g.setColor(Color.BLACK);
        int crankX=frame.getWidth()/2-176;
               int crankY=600;
               int height1=(int)(100*Math.sin(rot));
               int offset1=(int)(6*Math.cos(rot));
               
               int height2=(int)(100*Math.sin(rot+Math.PI));
               int offset2=(int)(6*Math.cos(rot+Math.PI));
               
               int height3=(int)(50*Math.sin(rot+Math.PI/2));
               int offset3=-(int)(3*Math.cos(rot+Math.PI/2));
               
               
               g.drawLine(crankX,crankY,crankX+=50,crankY);
               g.drawLine(crankX,crankY,crankX-=offset1,crankY-=height1);
               g.drawLine(crankX,crankY,crankX+=76+offset1*2,crankY);
               int cent1x=crankX-(76/2+offset1);
               g.drawLine(crankX,crankY,crankX-=offset1,crankY+=height1);
               g.drawLine(crankX,crankY,crankX+=100,crankY);
               
               g.drawLine(crankX,crankY,crankX-=offset2,crankY-=height2);
               g.drawLine(crankX,crankY,crankX+=76+offset2*2,crankY);
               int cent2x=crankX-(76/2+offset2);
               g.drawLine(crankX,crankY,crankX-=offset2,crankY+=height2);
               g.drawLine(crankX,crankY,crankX+=50,crankY);
               
               int cent2=cent2x+height3;
               int cent1=cent1x+height3;
               
               g.drawLine(crankX,crankY,crankX+=50,crankY);
               g.drawLine(crankX,crankY,crankX-=offset3,crankY+=height3);
               g.drawLine(crankX,crankY,crankX+=76+offset3*2,crankY);
               int cent3x=crankX-(76/2+offset3);
               g.drawLine(crankX,crankY,crankX-=offset3,crankY-=height3);
               g.drawLine(crankX,crankY,crankX+=100,crankY);
               
               g.drawLine(cent3x,600+height3,cent3x,260);
               g.drawOval(cent3x-10,255,10,10);
               
               
               
               g.drawLine(cent1x,600-height1,cent1x,400-height1);
               g.drawLine(cent1x-(76/2),400-height1,cent1x+(76/2),400-height1);
               g.drawLine(cent1x-(76/2+4),500,cent1x-(76/2+4),290);
               g.drawLine(cent1x+(76/2+4),500,cent1x+(76/2+4),290);
               g.drawLine(cent1x-(76/2+4),290,cent1x-10,290);
               g.drawLine(cent1x+(76/2+4),290,cent1x+10,290);
               g.drawLine(cent1x+10,290,cent1x+10,270);
               g.drawLine(cent1x-10,290,cent1x-10,270);
               
               g.drawLine(cent2x,600-height2,cent2x,400-height2);
               g.drawLine(cent2x-(76/2),400-height2,cent2x+(76/2),400-height2);
               g.drawLine(cent2x-(76/2+4),500,cent2x-(76/2+4),290);
               g.drawLine(cent2x+(76/2+4),500,cent2x+(76/2+4),290);
               g.drawLine(cent2x-(76/2+4),290,cent2x-10,290);
               g.drawLine(cent2x+(76/2+4),290,cent2x+10,290);
               g.drawLine(cent2x+10,290,cent2x+10,270);
               g.drawLine(cent2x-10,290,cent2x-10,270);
               
               int midX=(cent1x+cent2x)/2;
               g.drawLine(midX+10,240,cent2x+50,240);
               g.drawLine(cent1x-50,240,midX-10,240);
               g.drawLine(midX+10,240,midX+10,200);
               g.drawLine(midX-10,240,midX-10,200);
               g.drawString("From boiler",midX-30,190);
               
               g.drawLine(cent2x-10,270,cent1x+10,270);
               g.drawLine(cent2x+10,270,cent2x+50,270);
               g.drawLine(cent1x-10,270,cent1x-50,270);
               
               g.setColor(Color.PINK);
               g.fillRect(midX-9,200,19,41);
               g.fillRect(cent1+11,241,cent2-cent1-21,29);
               if (height3<0){
                   g.fillRect(cent1x-9,270,19,21);
                   g.fillRect(cent1x-(76/2+3),291,83,(400-height1)-291);
                   float f=(float)(height2+100);
                   //g.setColor(new Color(255-(int)(1.275F*f),175-(int)(0.875F*f),(int)(f*0.4F+175)));
                   g.setColor(new Color(255-(int)(0.4F*f),175,(int)(f*0.4F+175)));
                   g.fillRect(cent2x-9,270,19,21);
                   g.fillRect(cent2x-(76/2+3),291,83,(400-height2)-291);
                   g.fillRect(cent2+10,241,(cent2x+50)-(cent2+10),29);
               }else{
                   g.fillRect(cent2x-9,270,19,21);
                   g.fillRect(cent2x-(76/2+3),291,83,(400-height2)-291);
                   float f=(float)(height1+100);
                   g.setColor(new Color(255-(int)(0.4F*f),175,(int)(f*0.4F+175)));
                   g.fillRect(cent1x-9,270,19,21);
                   g.fillRect(cent1x-(76/2+3),291,83,(400-height1)-291);
                   g.fillRect(cent1x-50,241,(cent1-10)-(cent1x-50),29);
               }
               g.setColor(Color.BLACK);
               g.drawLine(cent3x-5,255,cent2+10,255);
               g.drawLine(cent2-10,255,cent1+10,255);
               g.drawRect(cent1-10,241,20,28);
               g.drawRect(cent2-10,241,20,28);
    }
    
}
