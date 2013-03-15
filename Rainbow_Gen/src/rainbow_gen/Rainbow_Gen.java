/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbow_gen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leijurv
 */
public class Rainbow_Gen extends Thread {/*
    static JFrame frame;
    static DURP M=new DURP();
    public static class DURP extends JComponent{
    public void paintComponent(Graphics g){
    g.drawString("Rainbow Table Generator by LurfJurv",10,10);
    g.drawString("Path: "+path,10,25);
    g.drawString("Password length: 3",10,40);
    synchronized(current){
    g.drawString("Current: "+current, 10, 55);
    }
    
    }
    }*/


    /**
     * @param args the command line arguments
     */
    static final String path=System.getProperty("user.home")+"/Desktop/Rainbow_crack/";

    static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    static final String numbers = "0123456789";
    static final String others = "`~-_=+[{]}\\|;:'" + '"' + ",<.>/?";
    static final String Alphabet = alphabet.toUpperCase();
    static final String Numbers = "!@#$%^&*()";
    static final String Hard = alphabet + numbers + Alphabet + Numbers + others;
    static final char[] alpha = Hard.toCharArray();
    int len;
    String soFar;
    MessageDigest crypt;
    boolean running;
    FileWriter fstream;
    public Rainbow_Gen(int Len, String SoFar) {
        soFar = SoFar;
        len = Len;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
        }
        running = true;
    }
    int id=0;
public void save(String s){
        try {
            fstream.write(s+"\n");
        } catch (IOException ex) {
        }
        if (total>1000000){
            System.out.println("Moving to file "+id);
            total=0;
            try {
                fstream.close();
            } catch (IOException ex) {
            }
            id++;
            try {
            fstream = new FileWriter(path+"rainbow"+id+".txt", true);
            //M.repaint();
        } catch (Exception ex) {
        }
        }
}

    public void Save(String s) {
        String S=s;
        save(Hex.encodeHexString(getHash(s)) + ":" + S);
    }
int total;
    @Override
    public void run() {
        try {
            fstream = new FileWriter(path+"rainbow"+id+".txt", true);
            
            
            //M.repaint();
        } catch (Exception ex) {
        }
        Do(len, soFar);
    }
    public void Do(int Len, String SoFar) {
        total++;
            Save(SoFar);
        
        if (Len == 0) {
            return;
        }
        for (int i = 0; i < alpha.length && running; i++) {
            Do(Len - 1, SoFar + alpha[i]);
        }
    }

    public byte[] getHash(String s) {
        crypt.reset();
        try {
            crypt.update(s.getBytes("utf8"));
        } catch (UnsupportedEncodingException ex) {
        }
        return crypt.digest();
    }
    /*
    public static void screen(){
    JFrame frame=new JFrame("Rainbow crack");
    M.setFocusable(true);
    (frame).setContentPane(M);
    frame.setLayout(new FlowLayout());
    frame.setSize(200,100);
    //frame.setUndecorated(true);
    //frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    frame.setVisible(true);
    frame.addWindowListener(new WindowAdapter(){
    public void windowClosing(WindowEvent e){
    System.exit(0);
    }
    });
    }*/

    public static void main(String[] args){
        (new Rainbow_Gen(3,"")).start();

    }
}