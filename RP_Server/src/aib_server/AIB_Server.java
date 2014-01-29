/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;


import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spongycastle.asn1.*;
/**
 *
 * @author leif
 */
public class AIB_Server {
    static final BigInteger e=new BigInteger("65537");
    static final String Save=System.getProperty("user.home")+"/Dropbox/RP_server/Addresses";
    static final String Save2=System.getProperty("user.home")+"/Dropbox/RP_server/Log";
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static final RSAKeyPair comKeyPair=new RSAKeyPair();
    static ArrayList<LogEvent> Log=new ArrayList<LogEvent>();
    public static void saveLogs() throws Exception{
        File fstream=new File(Save2);
        FileOutputStream f=new FileOutputStream(fstream);
        synchronized(Log){
            for (LogEvent l : Log){
                l.write(f);
            }
            f.close();
        }
    }
    public static void readLogs() throws Exception{
        File fstream=new File(Save2);
        FileInputStream f=new FileInputStream(fstream);
        synchronized(Log){
            Log=new ArrayList<LogEvent>();
            //System.out.println(f.available());
            while(f.available()!=0){
                Log.add(LogEvent.create(f));
            }
            f.close();
        }
    }
    public static void saveAddresses() throws Exception{
         File fstream=new File(Save);
         FileOutputStream f=new FileOutputStream(fstream);
         synchronized(addresses){
            for (Address a : addresses){
                f.write(to128(a.address));
                f.write(toTen(a.value));
                f.write(to30(a.privKey.toByteArray()));
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
            for (int i=0; i<r/168; i++){
                byte[] R=new byte[128];
                f.read(R);
                byte[] S=new byte[10];
                f.read(S);
                
                byte[] T=new byte[30];
                f.read(T);
                BigInteger b=new BigInteger(S);
                b=BigInteger.ZERO;//Will be recreated by logs
                Address e=new Address(new BigInteger(R),b,new BigInteger(T));
                System.out.println("Loaded address "+snip(e.address)+" with value "+new BigInteger(S)+" with address "+e.depAddr+" and privkey "+e.privKey.toString(16));
                addresses.add(e);
            }
            f.close();
         }
    }
    public static void setup(){
        
        
        /*addresses.add(new Address(new BigInteger("24020791567495045215642065098106976790718580862353753650273116703458638404109145341712527426373897746399520171943504496355138983511621038572113831301549097251604814930086307353245172517660389318908257398118671272810218206810306586747566519936154692410187626160745255451982459450427003693146228840518387841101"),new BigInteger("12340000"),new BigInteger(239,new SecureRandom())));
   
        byte[] r=new byte[0];
        r=LogTransaction.add(r,to128(addresses.get(0).address));
        r=LogTransaction.add(r,toTen(addresses.get(0).value));
        Log.add(new LogDeposit(r));*/
        comKeyPair.pri=new BigInteger("21237045006372166362343735671067154651005240837175711672036432501367744380726761523099917307895886222611348141610156734035441374189102166814928011873966331620177069811864633392634727116725767267394440079044576074261644881084359423298636982612361207340331894984808401184717391810830519841114676118399154603777");
   comKeyPair.pub=e;
   comKeyPair.modulus=new BigInteger("30544972536158817251655212322229910774764747152393991481592671659617650517484303426664602567870181612853441670471510377880014074973251738281831565513412018318239736924037011987096211978883338308643969623592748817141184518938883172148789404470797180317762752846760841810232802667272390174890293292561296762453");

    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        setup();
        //saveAddresses();
        //saveLogs();
        readAddresses();
        readLogs();
        /*
        ECKey k=new ECKey(new BigInteger("abcd",16));
        System.out.println(Hex.encodeHexString(k.getPrivKeyBytes()));
        System.out.println(Hex.encodeHexString(k.getPubKeyHash()));
        System.out.println(k.toStringWithPrivate());
        
      
        System.out.println(k.toAddress(Address.MainNet).toString());*/
       
        ServerSocket S=new ServerSocket(5020);
        boolean running=true;
        System.out.println("Server running. =D");
        while(running){
            Socket s=S.accept();
            (new RequestThread(s)).start();
        }
    }
    public static boolean verify(byte[] b,byte[] c,BigInteger modulus) throws NoSuchAlgorithmException{

        RSAKeyPair comPubKey=new RSAKeyPair();
            comPubKey.modulus=modulus;
            comPubKey.pub=new BigInteger("65537");
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(b);
                byte[] sig=m.digest();
                System.out.println("Hsh: "+new BigInteger(sig).mod(modulus).toString(16));
                BigInteger k=comPubKey.encode(new BigInteger(c));
                System.out.println("Sig: "+k.toString(16));
                return k.compareTo(new BigInteger(sig).add(modulus).mod(modulus))==0;
    }
    public static byte[] hash(byte[] r){
        try {
            MessageDigest m=MessageDigest.getInstance("SHA1");
            m.reset();
            m.update(r);
            byte[] sig=m.digest();
            return sig;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Son, you're an IDIOT");
        }
        return null;
    }
    public static String processTx(String s){
        LogEvent event=new LogTransaction(s);
        Log.add(event);
        try {
            saveLogs();
        } catch (Exception ex) {
            Logger.getLogger(LogTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public static byte[] slice(byte[] input, int amt){
        byte[] output=new byte[input.length-amt];
        for (int i=0; i<output.length; i++){
            output[i]=input[i+amt];
        }
        return output;
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
        BigInteger b=BigInteger.ZERO;
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
                //byte[] S={};
                //if (k.toByteArray().length<=128){
                byte[] S=to128(k);
                //}
                //S=k.toByteArray();
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
    public static String snip(BigInteger x){
        return (x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
    public static byte[] to128(BigInteger x){
        byte[] X=new byte[128];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            if (xx.length==129){
                if (i<X.length){
                    X[i]=xx[i+1];
                }       
            }else{
                X[128-xx.length+i]=xx[i];
            }
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
    public static byte[] to30(byte[] xx){
        byte[] X=new byte[30];
        /*for (int i=0; i<X.length; i++){
            X[i]=" ".getBytes()[0];
        }*/
        for (int i=0; i<xx.length; i++){
            X[30-xx.length+i]=xx[i];
        }
        return X;
    }
}
