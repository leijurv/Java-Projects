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
        BigInteger o=AIB_Client.rValueStorage.getTotalValue();
        BigDecimal X=new BigDecimal(o);
        X=X.divide(new BigDecimal("100000000"));
        g.drawString("Balance: "+X,100,100);
    }
}
