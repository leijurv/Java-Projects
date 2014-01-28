/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

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
        New=new JButton("New Address");
        New.addActionListener(this);
        New.setActionCommand("New");
        Copy=new JButton("Copy Address");
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
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            int Y=i*15+100;
            int X=30;
            Address W=AIB_Client.addresses.get(i);
            g.drawString(new BigDecimal(W.value).divide(new BigDecimal("100000000")).toString(),X,Y);
            g.drawString(AIB_Client.addresses.get(i).toString(),X+100,Y);
            //System.out.println(i);
        }
        g.setColor(Color.BLUE);
        g.fillOval(15,selectedIndex*15+90,10,10);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("New")){
            RSAKeyPair R=new RSAKeyPair();
            R.generate(new BigInteger(512,100,new SecureRandom()),new BigInteger(511,100,new SecureRandom()),AIB_Client.e,false);
            AIB_Client.addresses.add(new Address(R));
            System.out.println(R.modulus.toByteArray().length);
            try {
                AIB_Client.saveAddresses();
            } catch (Exception ex) {
                Logger.getLogger(DepositRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(rPrimeValues.get(0).toString(16));
        }
        if (ae.getActionCommand().equals("Copy")){
            W w=new W();
            w.setClipboardContents(AIB_Client.addresses.get(selectedIndex).toString());
        }
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
