/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import cryptolib.Hex;
import cryptolib.RSAKeyPair;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author leif
 */
public class DepositRequestsPane extends JComponent implements ActionListener{
    JButton New;
    JButton Copy;
    JButton Delete;
    int selectedIndex=0;
    
    public DepositRequestsPane(){
        New=new JButton("New Deposit Request");
        New.addActionListener(this);
        New.setActionCommand("New");
        Copy=new JButton("Copy Selected Deposit Request");
        Copy.addActionListener(this);
        Copy.setActionCommand("Copy");
        setLayout(new FlowLayout());
        Copy.setFocusable(false);
        New.setFocusable(false);
        add(New);
        add(Copy);
        setFocusable(true);
    }
    public void paintComponent(Graphics g){
        for (int i=0; i<AIB_Client.depositRequests.size(); i++){
            int Y=i*15+100;
            int X=30;
            DepositRequest W=AIB_Client.depositRequests.get(i);
            g.drawString(new BigDecimal(W.totalValue).divide(new BigDecimal("100000000")).toString(),X,Y);
            g.drawString(AIB_Client.depositRequests.get(i).toString(),X+100,Y);
            //System.out.println(i);
        }
        g.setColor(Color.BLUE);
        g.fillOval(15,selectedIndex*15+90,10,10);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("New")){
            String s=JOptionPane.showInputDialog("How much?");
            BigDecimal I=new BigDecimal(s);
            I=I.multiply(new BigDecimal("100000000"));
            BigInteger x=new BigInteger(I.toString().contains(".")?I.toString().substring(0,I.toString().indexOf(".")):I.toString());
            ArrayList<Integer> dValues=getDValues(x);
            System.out.println(dValues);
            ArrayList<BigInteger> rPrimeValues=Transaction.requestRPrimeValues(dValues);
            AIB_Client.depositRequests.add(new DepositRequest(rPrimeValues,dValues));
            try {
                AIB_Client.saveDepositRequests();
            } catch (Exception ex) {
                Logger.getLogger(DepositRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(rPrimeValues);
        }
        if (ae.getActionCommand().equals("Copy")){
            W w=new W();
            w.setClipboardContents(AIB_Client.depositRequests.get(selectedIndex).toString());
        }
    }
    

    public static ArrayList<Integer> getDValues(BigInteger x){
        if (x.compareTo(BigInteger.ZERO)==0){
            return new ArrayList<Integer>();
        }
        //System.out.println(x);
        int last=0;
        for (int i=0; true; i++){
            if (BigInteger.TEN.pow(i).compareTo(x)!=-1){
                last=i;
                if (BigInteger.TEN.pow(i).compareTo(x)==1){
                    last--;
                }
                break;
            }
        }
        BigInteger w=x.subtract(BigInteger.TEN.pow(last));
        ArrayList<Integer> D=getDValues(w);
        D.add(last);
        return D;
    }

    public static final class W implements ClipboardOwner{
         /**
   * Empty implementation of the ClipboardOwner interface.
   */
   public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
     //do nothing
   }

  /**
  * Place a String on the clipboard, and make this class the
  * owner of the Clipboard's contents.
  */
  public void setClipboardContents( String aString ){
    StringSelection stringSelection = new StringSelection( aString );
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents( stringSelection, this );
  }
  public String getClipboardContents() {
    String result = "";
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    //odd: the Object param of getContents is not currently used
    Transferable contents = clipboard.getContents(null);
    boolean hasTransferableText =
      (contents != null) &&
      contents.isDataFlavorSupported(DataFlavor.stringFlavor)
    ;
    if ( hasTransferableText ) {
      try {
        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
      }
      catch (UnsupportedFlavorException ex){
        //highly unlikely since we are using a standard DataFlavor
        System.out.println(ex);
        ex.printStackTrace();
      }
      catch (IOException ex) {
        System.out.println(ex);
        ex.printStackTrace();
      }
    }
    return result;
  }
    }
}
