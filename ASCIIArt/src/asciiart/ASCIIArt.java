/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asciiart;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import sun.awt.image.ToolkitImage;

/**
 *
 * @author leijurv
 */
public class ASCIIArt extends JComponent{
    static ASCIIArt M=new ASCIIArt();
    static BufferedImage i;
    static BufferedImage base;
    static BufferedImage[] glyphs;
    static ArrayList<Character> Glyphs;
    static BufferedImage[][] bestt=new BufferedImage[40][40];
    static ToolkitImage a;
    public void paintComponent(Graphics g){
        g.drawString("D:"+D+" m:"+m+" t:"+t,10,10);
        if (processing){
            g.drawImage(result,50,50,null);
        }else{
            g.drawImage(base,50,50,null);
        }
        
        for (int x=0; x<bestt.length; x++){
            for (int y=0; y<bestt[0].length; y++){
                if (bestt[x][y]!=null)
                g.drawImage(bestt[x][y],x*7+50,y*12+50,null);
            }
        }
        
    }
    static int D=5;
    static int m=10;
    static int t=100;
    static BufferedImage result;
    static boolean processing=false;
    public static BufferedImage edge(BufferedImage a){
        result=new BufferedImage(a.getWidth(),a.getHeight(),BufferedImage.TYPE_INT_RGB);
        for (int x=0; x<result.getWidth(); x++){
            M.repaint();
            for (int y=0; y<result.getHeight(); y++){
                int num=0;
                int sum=0;
                int r=new Color(a.getRGB(x,y)).getRed();
                for (int dx=-D; dx<=D; dx++){
                    for (int dy=-D; dy<=D; dy++){
                        int nx=dx+x;
                        int ny=dy+y;
                        if (nx>=0 && nx<result.getWidth() && ny>=0 && ny<result.getHeight() && !(nx==x && nx==y)){
                            int aa=m*Math.abs(r-new Color(a.getRGB(nx, ny)).getRed());
                    sum+=aa>255?0:255-aa;
                    num++;
                        }
                    }
                }
                sum/=num;
                if (sum<t){
                    sum=0;
                }
                float s=((float)sum)*edge+((float)(new Color(a.getRGB(x,y)).getRed()))*basee;
                s=s/((float)edge+(float)basee);
                sum=(int)s;
                result.setRGB(x,y,new Color(sum,sum,sum).getRGB());
            }
        }
        return result;
    }
    static float edge=1;
    static float basee=0;
    public static int compare(BufferedImage a, BufferedImage b){
        int total=0;
        for (int x=0; x<7; x++){
            for (int y=0; y<12; y++){
                int A=new Color(a.getRGB(x,y)).getRed();
                int B=new Color(b.getRGB(x,y)).getRed();
                total+=Math.abs(A-B);
            }
        }
        return total;
    }
    public static int best(BufferedImage a){
        int best=0;
        int bestvalue=Integer.MAX_VALUE;
        for (int i=0; i<glyphs.length; i++){
            int d=compare(a,glyphs[i]);
            if (d<bestvalue){
                bestvalue=d;
                best=i;
            }
        }
        return best;
    }
    public static BufferedImage proces(BufferedImage a){
        BufferedImage result=new BufferedImage(a.getWidth(),a.getHeight(),BufferedImage.TYPE_INT_RGB);
        for (int x=0; x<result.getWidth(); x++){
            for (int y=0; y<result.getHeight(); y++){
                Color c=new Color(a.getRGB(x,y));
                
                //if (c.getRGB()!=a.getRGB(x,y))
                //System.out.println(c.getRGB()+","+a.getRGB(x,y));
                int sum=c.getRed()+c.getGreen()+c.getBlue();
                sum/=3;
                //System.out.println(c.getAlpha());
                //sum=(1-c.getTransparency())*sum+(c.getTransparency()*255);
                //System.out.println(c.getTransparency());
                //System.out.println(sum);
               // System.out.println(a.getRGB(x,y));
                //System.out.println(a.getRGB(x,y));
                result.setRGB(x,y,a.getRGB(x,y));
                
                if (a.getRGB(x,y)==0)
                    result.setRGB(x,y,Color.WHITE.getRGB());
                    
            }
        }
        return result;
    }
    public static void DO(){
        base=a.getBufferedImage();
        //base=proces(base);
        base=edge(base);
    }
    public static void meow() throws Exception{
        bestt=new BufferedImage[bestt.length][bestt[0].length];
        FileOutputStream ff=new FileOutputStream(new File("/Users/leijurv/Documents/thing3.txt"));
        for (int y=0; y<bestt[0].length; y++){
            for (int x=0; x<bestt.length; x++){
           // System.out.println(x);
            
                BufferedImage sub=base.getSubimage(x*7, y*12, 7, 12);
               int  best=best(sub);
               if (best==0){
                   best=(int)(' ');
               }
                //System.out.println(best);
               ff.write(Glyphs.get(best));
                bestt[x][y]=glyphs[best];
            }
            ff.write("\n".getBytes());
            M.repaint();
        }
        ff.close();
    }
    static int glyphz=300;
    public static void generateGlyphs(){
        Font f=new Font("Courier",Font.PLAIN,12);
        Glyphs=new ArrayList<Character>();
        for (char c=0; c<=glyphz; c++){
            if (f.canDisplay(c)){
                //System.out.println(c);
                if (Glyphs.size()==24){
                    System.out.println((int)c);
                }
                Glyphs.add(c);
            }
        }
        
        
        glyphs=new BufferedImage[Glyphs.size()];
        for (int i=0; i<Glyphs.size(); i++){
            BufferedImage I=new BufferedImage(7,12,BufferedImage.TYPE_INT_RGB);
            Graphics G=I.getGraphics();
            G.setColor(Color.WHITE);
            G.fillRect(0,0,7,12);
            G.setFont(f);
            G.setColor(Color.BLACK);
            G.drawString(Glyphs.get(i)+"",0,12);
            glyphs[i]=I;
        }
    }
    static BufferedImage bb;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
         bb=ImageIO.read(new File("/Users/leijurv/Downloads/hand1.png"));
        /*BufferedImage bb=new BufferedImage(500,291,BufferedImage.TYPE_INT_RGB);
        Graphics g=bb.getGraphics();
        g.setFont(new Font("Courier",Font.PLAIN,50));
        g.drawString("HELLO. I AM",0,50);
        g.drawString("THE CAT.",0,100);*/
        ToolkitImage bbb=(ToolkitImage)(bb.getScaledInstance(bb.getWidth(), bb.getHeight(), Image.SCALE_SMOOTH));
        bbb.preload(null);
         bb=bbb.getBufferedImage();
        bb=proces(bb);
        
        int h=50;
        float sc=(float)bb.getHeight(null)/((float)(12*h));
        float w=((float)bb.getWidth(null))/(7*sc);
        int W=(int)w;
        System.out.println(h+","+w);
        bestt=new BufferedImage[W][h];
        
        a=(ToolkitImage)(bb.getScaledInstance(7*W, 12*h, Image.SCALE_SMOOTH));
        a.preload(null);
        //a.getBufferedImage()
        DO();
        JFrame frame=new JFrame("MEOW");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
        frame.setContentPane(M);
        frame.setVisible(true);
        
        System.out.println(a.getWidth()+","+a.getHeight());
        JButton rs=new JButton("m");
        rs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                edge=Float.parseFloat(JOptionPane.showInputDialog("Edge?"));
                basee=Float.parseFloat(JOptionPane.showInputDialog("Base?"));
                new Thread(){
                    public void run(){
                processing=true;
                DO();
                processing=false;
                    }
                }.start();
            }
        });
        frame.add(rs);
       JButton rd=new JButton("lol");
       rd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bestt=new BufferedImage[bestt.length][bestt[0].length];
                D=Integer.parseInt(JOptionPane.showInputDialog("D?"));
                m=Integer.parseInt(JOptionPane.showInputDialog("m?"));
                t=Integer.parseInt(JOptionPane.showInputDialog("t?"));
                new Thread(){
                    public void run(){
                processing=true;
                DO();
                processing=false;
                    }
                }.start();
            }
        });
       frame.setLayout(new FlowLayout());
       frame.add(rd);
       JButton dothemeow=new JButton("meow");
       dothemeow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(){
                    public void run(){
                
                  
                try {
                    meow();
                } catch (Exception ex) {
                    Logger.getLogger(ASCIIArt.class.getName()).log(Level.SEVERE, null, ex);
                }
                    }}.start();
            }
        });
       frame.add(dothemeow);
       JButton regen=new JButton("lol1");
       regen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                glyphz=Integer.parseInt(JOptionPane.showInputDialog("GLyphz?"));
                generateGlyphs();
                
            }
        });
       frame.add(regen);
       generateGlyphs();
        
        
        
        
    }
    
}
