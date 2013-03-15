/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbow_crackr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leijurv
 */
public class Rainbow_Crackr extends Thread{
static final String path=System.getProperty("user.home")+"/Desktop/Rainbow_crack/";
static MessageDigest crypt;
static{
    try {
            crypt = MessageDigest.getInstance("SHA-1");
        } catch (Exception ex) {
        }
}
    /**
     * @param args the command line arguments
     */
public Rainbow_Crackr(byte[] Goal){
    goal=Hex.encodeHexString(Goal);
}
String goal;
public void run(){
    try{
  // Open the file that is the first 
  // command line parameter
  FileInputStream fstream = new FileInputStream(path+goal.substring(0,2)+".txt");
  // Get the object of DataInputStream
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  //Read File Line By Line
  int i=0;
  while ((strLine = br.readLine()) != null && running)   {
      if (strLine.startsWith(goal)){
          running=false;
          System.out.println(strLine);
      }
      i++;
      if (i%10000==0){
          System.out.println(i);
      }
  }
  if (strLine==null){
  }
  //Close the input stream
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
}
static int[] pos=new int[0];
static boolean running=true;
    public static void main(String[] args) throws UnsupportedEncodingException {
        
        crypt.reset();
        crypt.update("1024".getBytes("utf8"));
        byte[] goal=crypt.digest();
        crypt.reset();
        
        
            (new Rainbow_Crackr(goal)).start();
        
    }
}
