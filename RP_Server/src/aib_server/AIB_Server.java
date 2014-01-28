/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class AIB_Server {
    static final BigInteger e=new BigInteger("65537");
    static final String Addresses=System.getProperty("user.home")+"/Dropbox/AIB_server/Addresses";
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static RSAKeyPair comKeyPair=new RSAKeyPair();
    public static void setup(){
        addresses.add(new Address(new BigInteger("3233"),new BigInteger("5021"),"YOURFACE"));
        addresses.add(new Address(new BigInteger("10807"),new BigInteger("12340000"),"YOURLIFE"));
comKeyPair.pri=new BigInteger("83374156446774429873694550481645812108834138335187533508679883506841478344454653407583462002728379160005936010232152845900203607261418110281030190618566505266984514447967618960101472648126527225429103019628649092001207451156000110046429132143123004788038578362511400808819284172666518163971226737327430819985");
comKeyPair.pub=e;
comKeyPair.modulus=new BigInteger("95812518035600410503994805360704581672073207036300573104181267870519734981509839211152174319605986165096863495810633205242273997599318920084305802356152031410095278243554997838911172683159778396002043381602230364061683476559432122157675368167117435777718607888823487551080217245394174274812214070625155618899");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        setup();
        ServerSocket S=new ServerSocket(5020);
        boolean running=true;
        System.out.println("Server running. =D");
        while(running){
            Socket s=S.accept();
            (new RequestThread(s)).start();
        }
    }
    public static String processTx(String s){
        //TODO
        return "";
    }
    public static String processGetAddr(String s){
        if (s.length()!=0){
        BigInteger b=new BigInteger(s,16);
        
        String addr="";
        //System.out.println(b);
        for (Address a : addresses){
            if (a.address.compareTo(b)==0){
                addr=a.depAddr;
            }
        }
        while(addr.length()<31){
            addr=" "+addr;
        }
        byte[] yourmom=new byte[128+31+128];
        byte[] B=to128(b);
        for (int i=0; i<128; i++){
            yourmom[i]=B[i];
        }
        for (int i=0; i<31; i++){
            yourmom[i+128]=addr.getBytes()[i];
        }
        for (int i=0; i<128; i++){
            yourmom[i+128+31]=0;
        }
            try {
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(yourmom);
                byte[] sig=m.digest();
                //System.out.println("Hsh: "+new BigInteger(sig).toString(16));
                BigInteger k=comKeyPair.decode(new BigInteger(sig));
                //System.out.println("Sig: "+k.toString(16));
                byte[] S=to128(k);
                
                for (int i=0; i<128; i++){
                    yourmom[i+128+31]=S[i];
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AIB_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(yourmom.length);
            //System.out.println("=D "+Hex.encodeHexString(yourmom));
            return Hex.encodeHexString(yourmom);
        
        }
        return "";
    }
    public static String processGetBal(String s){
        if (s.length()!=0){
        BigInteger bb=new BigInteger(s,16);
        BigInteger b=null;
        for (Address a : addresses){
            if (a.address.compareTo(bb)==0){
                b=a.value;
            }
        }
        byte[] a=toTen(b);
        byte[] yourmom=new byte[128+10+128];
        byte[] B=to128(bb);
        for (int i=0; i<128; i++){
            yourmom[i]=B[i];
        }
        for (int i=0; i<10; i++){
            yourmom[i+128]=a[i];
        }
        for (int i=0; i<128; i++){
            yourmom[i+128+10]=0;
        }
            try {
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(yourmom);
                byte[] sig=m.digest();
                //System.out.println("Hsh: "+new BigInteger(sig).toString(16));
                BigInteger k=comKeyPair.decode(new BigInteger(sig));
                //System.out.println("Sig: "+k.toString(16));
                byte[] S=to128(k);
                
                for (int i=0; i<128; i++){
                    yourmom[i+128+10]=S[i];
                }
            } catch (NoSuchAlgorithmException ex) {
            }
            //System.out.println(yourmom.length);
            //System.out.println("=D "+Hex.encodeHexString(yourmom));
            //System.out.println("NUR");
            return Hex.encodeHexString(yourmom);
        
        }
        //System.out.println("NUR");
        return "";
    }
    public static void snip(BigInteger x){
        System.out.print(x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
    public static byte[] to128(BigInteger x){
        byte[] X=new byte[128];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[128-xx.length+i]=xx[i];
        }
        return X;
    }
    public static byte[] toTen(BigInteger x){
        byte[] X=new byte[10];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[10-xx.length+i]=xx[i];
        }
        return X;
    }
}
