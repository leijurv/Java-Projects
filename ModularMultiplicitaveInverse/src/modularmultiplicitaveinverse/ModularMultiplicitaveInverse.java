/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularmultiplicitaveinverse;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author leijurv
 */
public class ModularMultiplicitaveInverse extends JComponent{
    static ModularMultiplicitaveInverse M=new ModularMultiplicitaveInverse();
static JFrame frame;
static BigInteger result;
public void paintComponent(Graphics g){
    g.drawString(result.toString(),10,10);
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BigInteger mod=new BigInteger(JOptionPane.showInputDialog("Mod?"));
        BigInteger number=new BigInteger(JOptionPane.showInputDialog("Number?"));
        result=number.modPow(BigInteger.ZERO.subtract(BigInteger.ONE),mod);
        //System.out.println((new BigInteger("17").modPow(new BigInteger("-1"),new BigInteger("3233"))));
        frame = new JFrame("Modular Multiplicitave Inverse");
        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(20, 20);
        //frame.setUndecorated(true);
        //frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        // TODO code application logic here
    }
}
