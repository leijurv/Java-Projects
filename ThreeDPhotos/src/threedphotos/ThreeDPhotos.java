/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package threedphotos;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author leijurv
 */
public class ThreeDPhotos extends JComponent{
    static ThreeDPhotos M=new ThreeDPhotos();
    static File[][] files;
    static int ind=0;
    static BufferedImage Fin;
    static JFrame frame;
    public void paintComponent(Graphics g){
        g.drawImage(Fin,0,0,(int)((float)Fin.getWidth()/(float)Fin.getHeight()*500F),500,null);
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        File f=new File("/Users/leijurv/Documents/3dd/");
        File[] Files=f.listFiles();
        files=new File[Files.length/2][2];
        for (int i=0; i<files.length; i++){
            files[i]=new File[] {Files[i*2],Files[i*2+1]};
        }
        process();
        frame=new JFrame("LOL");
        frame.setSize(2000,2000);
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
	frame.setVisible(true);
        new Thread(){
            public void run(){
                while(true){
                try {
                    //Thread.sleep(500);
                    ind++;
                    process();
                } catch (Exception ex) {
                    Logger.getLogger(ThreeDPhotos.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
            }
        }.start();
        for (String s:ImageIO.getWriterFormatNames())System.out.println(s);
    }
    public static BufferedImage fix(BufferedImage a){
        BufferedImage result=new BufferedImage(a.getHeight(), a.getWidth(),a.getType());
        for (int i=0; i<a.getWidth(); i++){
            for (int n=0; n<a.getHeight(); n++){
                result.setRGB(n,i,a.getRGB(i,a.getHeight()-n-1));
            }
        }
        return result;
    }
    public static void process() throws Exception{
        File a=files[ind][0];
        File b=files[ind][1];
        BufferedImage A=ImageIO.read(a);
        BufferedImage B=ImageIO.read(b);
        if (A.getWidth()>A.getHeight()){
            A=fix(A);
        }
        if (B.getWidth()>B.getHeight()){
            B=fix(B);
        }
        BufferedImage result=new BufferedImage((int)(A.getWidth()*2.05F),A.getHeight(),A.getType());
        Graphics g=result.getGraphics();
        g.drawImage(B,0,0,null);
        g.drawImage(A,(int)(A.getWidth()*1.05F),0,null);
        
        ImageIO.write(result, "png", new File("/Users/leijurv/Documents/good3d/"+(ind+44)+".png"));
        
        Fin=result;
        M.repaint();
    }
}
