/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phish;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author leif
 */
public class Phish extends JComponent implements KeyListener{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Phish p=new Phish();
    }
    JFrame background;
    JFrame foreground;
    boolean alr=false;
    GraphicsDevice gd;
    Thing t;
    public static class Thing extends JComponent{
        public void paintComponent(Graphics g){
            
        }
    }
    public Phish(){
         foreground = new JFrame("");

        foreground.setLayout(new FlowLayout());
        foreground.setSize(200,200);
        t=new Thing();
        t.setFocusable(true);
        foreground.setLocation(700,300);
        //foreground.setX(5);
        //foreground.se
        JButton Cancel=new JButton("Cancel");
        //Positioning for Cancel button here
        t.add(Cancel);
        
        foreground.setContentPane(t);
       background = new JFrame("");

        background.setLayout(new FlowLayout());
        this.setFocusable(true);
        background.setContentPane(this);
        //background.setSize(1000, 700);
        background.setUndecorated(true);
        background.setExtendedState(background.MAXIMIZED_BOTH);
        
        background.setVisible(true);
        /*background.addWindowListener(new WindowAdapter() {
            
            
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });*/
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        gd.setFullScreenWindow(background);
        addKeyListener(this);
    }
    public void paintComponent(Graphics g){
        g.setColor(Color.BLACK);
        
        g.fillRect(0, 0, background.getWidth(), background.getHeight());
    }
    @Override
    public void keyTyped(KeyEvent ke) {
        System.out.println("durp");
        alr=true;
        
        gd.setFullScreenWindow(null);
        background.dispose();
        foreground.setVisible(true);
        //background.add(foreground);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
