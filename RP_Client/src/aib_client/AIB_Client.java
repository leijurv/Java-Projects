/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leijurv
 */
public class AIB_Client {
    static final BigInteger e=new BigInteger("65537");
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static Gooey gooey;
    static BigInteger comPubKey=new BigInteger("95812518035600410503994805360704581672073207036300573104181267870519734981509839211152174319605986165096863495810633205242273997599318920084305802356152031410095278243554997838911172683159778396002043381602230364061683476559432122157675368167117435777718607888823487551080217245394174274812214070625155618899");
    static String web="http://localhost:5020/";
    public static void saveAddresses(){
        
    }
    public static String load(String webpage) throws Exception{
        URL url = new URL(webpage);
URLConnection con = url.openConnection();
Reader r = new InputStreamReader(con.getInputStream());

StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}

r.close();
return buf.toString();
    }
    public static void snip(BigInteger x){
        System.out.print(x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{ 
        RSAKeyPair R=new RSAKeyPair();
        R.modulus=new BigInteger("3233");
        R.pri=new BigInteger("2753");
        R.pub=new BigInteger("17");
        RSAKeyPair S=new RSAKeyPair();
        S.generate(new BigInteger("101"), new BigInteger("107"), new BigInteger("17"), false);
RSAKeyPair T=new RSAKeyPair();
        T.generate(new BigInteger("11"), new BigInteger("13"), new BigInteger("17"), false);

        addresses.add(new Address(R));
        addresses.add(new Address(S));
        //addresses.add(new Address(T));
gooey=new Gooey();
    }
    
   
}