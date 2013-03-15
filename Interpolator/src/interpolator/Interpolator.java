/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpolator;
import fraction.Fraction;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import polynomial.Polynomial;
/**
 *
 * @author leijurv
 */
public class Interpolator extends JComponent implements MouseListener{
static Interpolator M=new Interpolator();
static JFrame frame;
static Fraction[] xVal=new Fraction[0];
static Fraction[] yVal=new Fraction[0];
public int convX(Fraction f){
    return f.multiply(new Fraction(100)).toInt();
}
public int convY(Fraction f){
    return 700-f.multiply(new Fraction(100)).toInt();
}
public Interpolator(){
    addMouseListener(this);
}
public void paintComponent(Graphics g){
    if (xVal.length>1){
    Polynomial p=Polynomial.interpolate(xVal,yVal);
    System.out.println(p);
    int xPrev=0;
    int yPrev=0;
    for (Fraction f=new Fraction(0); f.compareTo(new Fraction(10))==-1; f=f.add(new Fraction(1,2))){
        int xCur=convX(f);
        int yCur=convY(p.eval(f));
        System.out.println("Plotting point"+f+":"+p.eval(f)+","+xCur+":"+yCur);
        //g.drawLine(xPrev,yPrev,xCur,yCur);
        g.drawRect(xCur,yCur,5,5);
        xPrev=xCur;
        yPrev=yCur;
    }
    for (int i=0; i<xVal.length; i++){
        int x=convX(xVal[i]);
        int y=convY(yVal[i]);
        g.fillRect(x,y,5,5);
        
    }
   
}g.setColor(Color.RED);
   g.fillRect(convX(new Fraction(1)),convY(new Fraction(1)),5,5);}
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frame = new JFrame("Chaos Game");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setSize(1000, 700);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // TODO code application logic here
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int X=me.getX();
        int Y=me.getY();
        Fraction y=new Fraction(700).subtract(new Fraction(Y)).divide(new Fraction(100));
        Fraction x=new Fraction(X).divide(new Fraction(100));
        Fraction[] nX=new Fraction[xVal.length+1];
        Fraction[] nY=new Fraction[yVal.length+1];
        for (int i=0; i<xVal.length; i++){
            nX[i]=xVal[i];
            nY[i]=yVal[i];
        }
        nX[nX.length-1]=x;
        nY[nY.length-1]=y;
        xVal=nX;
        yVal=nY;
        System.out.println("Adding position"+X+":"+Y+","+x+":"+y);
        M.repaint();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
