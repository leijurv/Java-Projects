/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keylistenerprinter;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author leijurv
 */
public class KeyListenerPrinter extends JPanel implements KeyListener{
    public KeyListenerPrinter(){
        super();
        addKeyListener(this);
        
    }
    int l=0;
    public static void main(String[] args) {
         JFrame frame=new JFrame("Spanish conjugations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640,480);
        KeyListenerPrinter panel=new KeyListenerPrinter();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
    public void paintComponent(Graphics g){
        g.clearRect(0, 0, 640, 480);
        g.drawString(Integer.toString(l),20,20);
    }
    @Override
    public void keyTyped(KeyEvent ke) {
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        l=ke.getKeyCode();
        repaint();
        System.out.println(ke.getKeyCode());
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
