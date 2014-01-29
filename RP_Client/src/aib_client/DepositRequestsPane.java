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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
public class DepositRequestsPane extends JComponent implements ActionListener,MouseListener{
    JButton New;
    JButton Copy;
    JButton Load;
    JButton Save;
    int selectedIndex=0;
    
    public DepositRequestsPane(){
        New=new JButton("New Address");
        New.addActionListener(this);
        New.setActionCommand("New");
        Copy=new JButton("CAT Address");
        Copy.addActionListener(this);
        Copy.setActionCommand("Copy");
        setLayout(new FlowLayout());
        Copy.setFocusable(false);
        New.setFocusable(false);
        
        Load=new JButton("Load Addresses from file");
        Load.addActionListener(this);
        Load.setActionCommand("Load");
        Load.setFocusable(false);
        add(Load);
        
        Save=new JButton("Save Addresses to file");
        Save.addActionListener(this);
        Save.setActionCommand("Save");
        Save.setFocusable(false);
        add(Save);
        
        add(New);
        add(Copy);
        setFocusable(true);
        addMouseListener(this);
    }
    public void paintComponent(Graphics g){
        g.setColor(new Color(180,180,255));
g.fillRect(15,selectedIndex*15+88,getWidth()-40,15); 

g.setColor(Color.BLACK);
g.drawString("Value",30,85);
g.drawString("Address",130,85);
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            int Y=i*15+100;
            int X=30;
            Address W=AIB_Client.addresses.get(i);
            g.drawString(new BigDecimal(W.value).divide(new BigDecimal("100000000")).toString(),X,Y);
            g.drawString(AIB_Client.snip(AIB_Client.addresses.get(i).address.modulus),X+100,Y);
            //System.out.println(i);
        }
       // g.setColor(Color.BLUE);
            
//g.fillOval(15,selectedIndex*15+90,10,10);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if (ae.getActionCommand().equals("New")){
            RSAKeyPair R=new RSAKeyPair();
            R.generate(new BigInteger(511,20,new SecureRandom()),new BigInteger(511,20,new SecureRandom()),new BigInteger("65537"),true);
            AIB_Client.addresses.add(new Address(R));
            //System.out.println(R.modulus.toByteArray().length);
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
        if (ae.getActionCommand().equals("Load")){
            try {
                AIB_Client.readAddresses();
            } catch (Exception ex) {
                Logger.getLogger(DepositRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (ae.getActionCommand().equals("Save")){
            try {
                AIB_Client.saveAddresses();
            } catch (Exception ex) {
                Logger.getLogger(DepositRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x=e.getX();
        int y=e.getY();
        if (y>85 && y<AIB_Client.addresses.size()*15+100 && x>30){
            int ind=(y-92)/15;
            if (ind<AIB_Client.addresses.size() && ind>=0){
                selectedIndex=ind;
            }
            repaint();
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
