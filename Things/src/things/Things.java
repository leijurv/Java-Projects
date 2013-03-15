/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package things;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leif
 */
public class Things extends JComponent{

    /**
     * @param args the command line arguments
     */
    static JFrame frame;
    public void paintComponent(Graphics g){
        int y=10;
        for (String n : derp){
            g.drawString(n,10,y);
            y+=15;
        }
    }
    static Things M=new Things();
    static String result="";
    static ArrayList<String> derp=new ArrayList<String>();
    public static void print(int y){
        for (int i=1; i<y; i++){
            if (coprime(i,y)){
            for (int n=1; n<y; n++){
                if (coprime(n,y)){
                    int x=(i*n )%y;
                    result+=x+"  ";
                    //System.out.print(x);
                    //System.out.print("  ");
                    if (Integer.toString(x).length()==1){
                        //System.out.print(" ");
                        result+=" ";
                    }
                }
            }
            derp.add(result);
            result="";
            System.out.println();
            }
        }
    }
    public static boolean coprime(int a, int b){
        if (a>b) {
            return coprime(b,a);
        }
        if (a==1){
            return true;
        }
        if (b%a==0){
            return false;
        }
        for (int i=2; i<a; i++){
            if (a%i==0 && b%i==0){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        
        print(Integer.parseInt(JOptionPane.showInputDialog("Please enter a number.")));
        frame = new JFrame("Lurf's Hilbert Curve Of Awesomeness");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(10, 10);
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
    }
}
