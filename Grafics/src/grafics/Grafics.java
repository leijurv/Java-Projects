/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grafics;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public class Grafics extends JPanel implements KeyListener{

  
    String text="";
    public Grafics(){
        super();
        addKeyListener(this);
    }
    @Override
    public void paintComponent(Graphics g){
        g.clearRect(0, 0,640,480);
        g.drawString(text,10,10);
    }
    @Override
    public void keyPressed( KeyEvent key){
          if (key.getKeyCode() == KeyEvent.VK_DELETE){
              if (text.length()!=0){
                  text=text.substring(0,text.length()-1);
              }
          }else{
              text+=key.getKeyChar();
          }
          repaint();
    }
    
    @Override
    public void keyTyped(KeyEvent key){ }   

    @Override
    public void keyReleased(KeyEvent key){ }
    public static void main(String[] args) {
        int Width=640;
        int Height=480;
        JFrame frame=new JFrame("BasiJPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setSize(Width,Height);
        Grafics panel=new Grafics();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
        // TODO code application logic here
    }
}
