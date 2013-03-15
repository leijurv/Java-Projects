/*/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buttontest;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author leijurv
 */
public class ButtonTest extends JPanel implements KeyListener,MouseListener{
    static BetterButton mybutton=new BetterButton(10,10,50,50,"I am a button");
    public interface Button{
        int xpos=0;
        int ypos=0;
        int xsize=0;
        int ysize=0;
        String text="";
        void checkClicked(MouseEvent m);
        void onClick();
        void draw(Graphics g);
        
    }
    public static class BetterButton extends ExampleButton{
        public BetterButton(int x, int y, int x1, int y1, String tex){
            super(x,y,x1,y1,tex);
        }
        @Override
        public void onClick(){
            System.out.println("ca");
        }
    }
    public static class ExampleButton implements Button{
        String text;
        int xpos;
        int ypos;
        int xsize;
        int ysize;
        public ExampleButton(int x, int y, int x1, int y1, String tex){
            xpos=x;
            ypos=y;
            xsize=x1;
            ysize=y1;
            text=tex;
        }
        public void onClick(){
            System.out.println("I got clicked!");
        }
        public void checkClicked(MouseEvent m){
            int x=m.getX();
            int y=m.getY();
            System.out.print(x);
            System.out.print(" ");
            System.out.print(y);
            System.out.print(" ");
            System.out.print(xsize);
            System.out.print(" ");
            System.out.print(ysize);
            System.out.print(" ");
            System.out.print(xpos);
            System.out.print(" ");
            System.out.println(ypos);
            if (x<xpos+xsize && x>xpos && y>ypos && y<ypos+ysize){
                onClick();
            }
        }
        public void draw(Graphics g){
            g.drawRect(xpos, ypos, xpos+xsize, ypos+ysize);
            g.drawString(text,xpos+1,ypos+10);
        }
    }
    public ButtonTest(){
        super();
        addKeyListener(this);
        addMouseListener(this);
    }
    @Override
    public void paintComponent(Graphics g){
        g.clearRect(0,0,640,480);
        mybutton.draw(g);
    }
    @Override
    public void keyPressed( KeyEvent key){
    }
    @Override
    public void keyTyped(KeyEvent key){ }   
    @Override
    public void keyReleased(KeyEvent key){ }
    @Override
    public void mousePressed(MouseEvent e) {
        mybutton.checkClicked(e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    public static void main(String[] args) {
        int Width=640;
        int Height=480;
        JFrame frame=new JFrame("Spanish Conjugations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setSize(Width,Height);
        ButtonTest panel=new ButtonTest();
        panel.setFocusable(true);
        frame.setContentPane(panel);
        frame.setVisible(true);
        // TODO code application logic here
    }
}
