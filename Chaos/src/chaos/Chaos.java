/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chaos;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leijurv
 */
public class Chaos extends JComponent implements KeyListener{
static MathContext mc=new MathContext(100);
static Chaos M=new Chaos();
static JFrame frame;
static int xOffset=0;
static int yOffset=0;
public Chaos(){
    addKeyListener(this);
}
public static double Iterate(double x, double r){
    return r*x*(1-x);
}
static BufferedImage bi;
static int progress=0;
public void paintComponent(Graphics g){
    
    synchronized((Object)progress){
        if (progress==-1){
            g.drawImage(bi, 0, 0, null);
            
            for (int i=1; i<500; i++){
                double n=((double)i)/10;
                int d=(int)((n-0.0025)/0.0025-400)+xOffset;
                g.drawString(n+"",d,10);
                
            }
        }else{
            g.drawString("Done "+progress+" out of 3139",10,10);
        }
    }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frame=new JFrame("Bifurcation diagram of logistic map");
	  M.setFocusable(true);
	  (frame).setContentPane(M);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
              System.exit(0);
	  }
	  });
          Do();
          
    }
    public static void Do(){
        bi=new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
          int c=Color.WHITE.getRGB();
          for (int i=0; i<frame.getWidth(); i++){
              for (int n=0; n<frame.getHeight(); n++){
                  bi.setRGB(i,n,c);
              }
          }
         int cur=0;
        for (int i=800; i<4000; i++){
        double current=0.0025+i*0.00125;
        ArrayList<Double> result=get(current);
        if (result.size()<35 && result.size()>cur){
            
            cur=result.size();
            System.out.println(cur);
        }
        for (double x : result){
            if (x!=0){
            int y=(int)(800-(x*800));
            //y+=50;
            //System.out.println(y);
            
            int u=0;
           switch(cur){
               case 2:
                   u=(Color.RED).getRGB();
                   break;
               case 3:
                   u=(Color.ORANGE).getRGB();
                   break;
               case 5:
                   u=(Color.YELLOW).getRGB();
                   break;
                   case 9:
                   u=(Color.GREEN).getRGB();
                   break;/*
                       case 17:
                   u=(Color.BLUE).getRGB();
                   break;
                           case 21:
                   u=(Color.MAGENTA).getRGB();
                   break;*/
                   
           }
           try{
            bi.setRGB((i/2)-400+xOffset,y-10+yOffset, u);
           }catch(Exception e){
               
           }
            //g.fillRect(i*2+50, new BigDecimal("0").subtract(x.multiply(new BigDecimal("200"))).intValue()+300, 1, 1);
        }
        }
        synchronized((Object)progress){
            progress=i;
        }
        M.repaint();
    }
        synchronized((Object)progress){
            progress=-1;
        }
        M.repaint();
    }
    public static ArrayList<Double> get(double r){
        double x=0.5;
        for (int i=0; i<100000; i++){
            x=Iterate(x,r);
        }
        double l=x;
        ArrayList<Double> ar=new ArrayList<Double>();
        x=Iterate(x,r);
        int t=1;
        ar.add(x);
        
        while(Math.abs(l-x)>0.0000001 && t<100){
            t++;
            x=Iterate(x,r);
            ar.add(x);
        }
        if (Math.abs(l-x)<0.0000001){
            ar.add(0D);
        }
        return ar;
    }
public static class G extends Thread{
    public void run(){
        Do();
    }
}
    @Override
    public void keyTyped(KeyEvent ke) {
        char c=ke.getKeyChar();
        if (c=='a'){
            xOffset-=-50;
        }
        if (c=='d'){
            xOffset+=-50;
        }
        if (c=='w'){
            yOffset-=-50;
        }
        if (c=='s'){
            yOffset+=-50;
        }
        (new G()).start();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}