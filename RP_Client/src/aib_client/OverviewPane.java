/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.awt.Graphics;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.JComponent;

/**
 *
 * @author leif
 */
public class OverviewPane extends JComponent{
    public void paintComponent(Graphics g){
        BigInteger o=new BigInteger("0");
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            o=o.add(AIB_Client.addresses.get(i).value);
        }
        BigDecimal X=new BigDecimal(o);
        X=X.divide(new BigDecimal("100000000"));
        g.drawString("Balance: "+X,100,100);
    }
}
