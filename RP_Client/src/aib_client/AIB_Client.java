/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class AIB_Client {
    static final BigInteger e=new BigInteger("65537");
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static Gooey gooey;
    static BigInteger comPubKey=new BigInteger("30544972536158817251655212322229910774764747152393991481592671659617650517484303426664602567870181612853441670471510377880014074973251738281831565513412018318239736924037011987096211978883338308643969623592748817141184518938883172148789404470797180317762752846760841810232802667272390174890293292561296762453");
    static String web="http://192.168.13.19:5020/";
    static final String Save=System.getProperty("user.home")+"/Dropbox/RP_client/Addresses";
    public static byte[] to128(BigInteger x){
        byte[] X=new byte[128];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[128-xx.length+i]=xx[i];
        }
        return X;
    }
    public static void saveAddresses() throws Exception{
         File fstream=new File(Save);
         FileOutputStream f=new FileOutputStream(fstream);
         synchronized(addresses){//Am I using this right?
            for (Address a : addresses){
                f.write(to128(a.address.modulus));
                f.write(to128(a.address.pri));
            }
            f.close();
         }
    }
    public static void readAddresses() throws Exception{
         File fstream=new File(Save);
         FileInputStream f=new FileInputStream(fstream);
         int r=f.available();
         synchronized(addresses){
            addresses=new ArrayList<Address>();
            for (int i=0; i<r/256; i++){
                RSAKeyPair kittycatspurringloudlynearbirdies=new RSAKeyPair();
                byte[] R=new byte[128];
                f.read(R);
                byte[] S=new byte[128];
                f.read(S);
                kittycatspurringloudlynearbirdies.modulus=new BigInteger(R);
                kittycatspurringloudlynearbirdies.pri=new BigInteger(S);
                kittycatspurringloudlynearbirdies.pub=e;
                addresses.add(new Address(kittycatspurringloudlynearbirdies));
            }
            f.close();
         }
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
    public static String snip(BigInteger x){
        return (x.toString(16).substring(0,32)+"..."+x.toString(16).substring(x.toString(16).length()-32,x.toString(16).length()));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){ 
        try {
            readAddresses();
        } catch (Exception ex) {
        }
        
gooey=new Gooey();
    }
    
   
}