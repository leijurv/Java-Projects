/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package silykitty;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class SilyKitty {
static BufferedImage[] diff0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        BufferedImage[] a=new BufferedImage[13];
        for (int i=0; i<13; i++){
            URL u=new URL("http://ebony.extra.hu/moira/afx/vtab"+(i+1<10?"0"+(i+1):i+1)+".jpg");
            HttpURLConnection c=(HttpURLConnection) u.openConnection();
            c.addRequestProperty("Authorization","Basic bW5mb3Z1dG90aTp2aXRpdml0aQ==");
            a[i]=ImageIO.read(c.getInputStream());
            System.out.println(i+","+a[i].getWidth()+","+a[i].getHeight());
        }
        diff0=new BufferedImage[13];
        
        
        JFrame frame=new JFrame("sadf");
        frame.setSize(2000,2000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new JComponent(){
            public void paintComponent(Graphics g){
                int width=a[0].getWidth()/3;
                int height=a[0].getHeight()/3;
                int num=frame.getWidth()/width;
                for (int i=0; i<13; i++){
                    g.drawImage(diff0[i],(i%num)*width,((i/num+1)-1)*height,width,height,null);
                }
            }
        });
        JButton button=new JButton("do the meow");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){
                calc(a,Integer.parseInt(JOptionPane.showInputDialog("Which one to compare? 1-13"))-1,frame);
            }
        });
        frame.setLayout(new FlowLayout());
        frame.add(button);
        calc(a,0,frame);
        frame.setVisible(true);
    }
    public static void calc(BufferedImage[] a, int ind, JFrame frame){
        for (int i=0; i<13; i++){
            BufferedImage res=new BufferedImage(a[ind].getWidth(),a[ind].getHeight(),a[ind].getType());
            for (int x=0; x<a[ind].getWidth(); x++){
                for (int y=0; y<a[ind].getHeight(); y++){
                    res.setRGB(x,y,(a[ind].getRGB(x,y)+a[i].getRGB(x,y))/2);
                    //res.setRGB(x,y,a[i].getRGB(x,));
                }
            }
            diff0[i]=res;
            System.out.println(i);
        }
        frame.repaint();
    }
}
