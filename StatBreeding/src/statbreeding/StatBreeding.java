/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statbreeding;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author leijurv
 */
public class StatBreeding extends JComponent{
    static double qFf=1.001;
    static double qff=1.001;
    static double select=100;
    static int ev=100;
    static double sw=0.0001;
    
    static StatBreeding M=new StatBreeding();
    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,1000,1000);
        for (int i=0; i<50; i++){
            Color c=new Color(i*5,i*5,i*5);
            g.setColor(c);
            g.fillRect((i%10)*85+15,((i-i%10)/10)*85+15,80,80);
        }
    }
    public static void print(double f){
        System.out.print(((double)((int)(f*100000)))/1000);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame frame=new JFrame("Cat");
        frame.setContentPane(M);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        run();
    }
    public static void run(){
        double FF=1-sw;
        double Ff=sw;
        double ff=0;
        System.out.println("Starting with "+(Ff*100)+"% Ff");
        System.out.println("Culling 1/"+select+" of FF and Ff each generation");
        System.out.println(((qff-1)*100)+"% of ffs will have 1 extra child");
        System.out.println(((qFf-1)*100)+"% of Ffs will have 1 extra child");
        System.out.println("Updating every "+ev+"th generation");
        System.out.println("FF       Ff    ff");
        for (int i=0; i<=10000; i++){
            double[] d=next(FF,Ff,ff);
            FF=d[0];
            Ff=d[1];
            ff=d[2];
            if (i%ev==0){
                print(FF);
                System.out.print("%, ");
                print(Ff);
                System.out.print("%, ");
                print(ff);
                System.out.println("%");
            }
        }
    }
    public static double[] next(double FF, double Ff, double ff){
        double FF_FF=FF*FF;
        double FF_Ff=FF*Ff*2*qFf;
        double FF_ff=FF*ff*2*qff;
        double Ff_Ff=Ff*Ff*qFf*qFf;
        double Ff_ff=Ff*ff*2*qff*qFf;
        double ff_ff=ff*ff*qff*qff;
        double nFF=FF_FF+FF_Ff/2+Ff_Ff/4;
        double nFf=FF_Ff/2+FF_ff+Ff_Ff/2+Ff_ff/2;
        double nff=Ff_Ff/4+Ff_ff/2+ff_ff;
        nFF*=(1-1/select);
        nFf*=(1-1/select);
        double toat=nFF+nFf+nff;
        nFF/=toat;
        nFf/=toat;
        nff/=toat;
        //System.out.println("o"+(FF_FF+FF_Ff+FF_ff+Ff_Ff+Ff_ff+ff_ff));
        //System.out.println("p"+(nFF+nFf+nff));
        return new double[] {nFF,nFf,nff};
    }
}
