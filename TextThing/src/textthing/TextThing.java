/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package textthing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class TextThing extends JComponent{
    public void paintComponent(Graphics g){
        if (text!=null){
            //g.drawImage(text,0,0,null);
        }
        if (result!=null){
            g.drawImage(result,0,0,null);
        }
    }
    static BufferedImage text;
    static BufferedImage result;
static TextThing M=new TextThing();
static JFrame frame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        String message=JOptionPane.showInputDialog("Text? Seperate lines with |."); 
        frame=new JFrame("TextThing");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        System.out.println("DONE");
        Thread.sleep(1000);
        text=new BufferedImage(frame.getHeight(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);
        result=new BufferedImage(text.getWidth(),text.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics g=text.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, text.getWidth(), text.getHeight());
        g.setColor(Color.BLACK);
        int height=100;
        //Copperplate Gothic Bold
        //Verdana Bold
        g.setFont(new Font("Courier",Font.BOLD,height));
       
//String message="ABCDEF|GHIJKL|MNOPQR|STUVWX|YZ -.,!?)(";
        //String message="ADAM IS|A POO-POO|FACE!";
        String[] mess=message.split("\\|");
        for (int i=0; i<mess.length; i++){
            g.drawString(mess[i],10,10+height+(height+10)*i);
        }
        
        int white=Color.WHITE.getRGB();
        int black=Color.BLACK.getRGB();
        result.setRGB(0,0,white);
        for (int x=1; x<result.getWidth(); x++){
            result.setRGB(x,0,white);
            result.setRGB(0,x,white);
            for (int y=1; y<result.getHeight(); y++){
                if (text.getRGB(x,y)==white && text.getRGB(x-1,y-1)==black && (text.getRGB(x-1,y)==black || text.getRGB(x,y-1)==black)){
                    result.setRGB(x,y,black);
                }else{
                    result.setRGB(x,y,white);
                }
            }
        }
        frame.repaint();
    }
    
}
