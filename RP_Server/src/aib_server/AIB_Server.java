/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;
import com.google.bitcoin.store.WalletProtobufSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author leif
 */
public class AIB_Server {
    static final BigInteger e=new BigInteger("65537");
    static final String Addresses=System.getProperty("user.home")+"/Dropbox/RP_server/Addresses";
    static ArrayList<Address> addresses=new ArrayList<Address>();
    static RSAKeyPair comKeyPair=new RSAKeyPair();
    static ArrayList<LogEvent> Log=new ArrayList<LogEvent>();
    public static void setup(){
        
        
        addresses.add(new Address(new BigInteger("24020791567495045215642065098106976790718580862353753650273116703458638404109145341712527426373897746399520171943504496355138983511621038572113831301549097251604814930086307353245172517660389318908257398118671272810218206810306586747566519936154692410187626160745255451982459450427003693146228840518387841101"),new BigInteger("12340000"),"YOURLIFE"));
   
        
        
        comKeyPair.pri=new BigInteger("21237045006372166362343735671067154651005240837175711672036432501367744380726761523099917307895886222611348141610156734035441374189102166814928011873966331620177069811864633392634727116725767267394440079044576074261644881084359423298636982612361207340331894984808401184717391810830519841114676118399154603777");
   comKeyPair.pub=e;
   comKeyPair.modulus=new BigInteger("30544972536158817251655212322229910774764747152393991481592671659617650517484303426664602567870181612853441670471510377880014074973251738281831565513412018318239736924037011987096211978883338308643969623592748817141184518938883172148789404470797180317762752846760841810232802667272390174890293292561296762453");

    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        String filePrefix="cat";
        File directory=new File("/Users/leijurv/Desktop");
        
    BlockChain vChain;
    SPVBlockStore vStore;
    Wallet vWallet;
    PeerGroup vPeerGroup;
    boolean vUseAutoSave = true;
InetAddress[] vPeerAddresses=null;
    
   File vChainFile, vWalletFile;
        NetworkParameters params=new NetworkParameters(1);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new Exception("Could not create named directory.");
            }
        }
        FileInputStream walletStream = null;
        try {
            vChainFile = new File(directory, filePrefix + ".spvchain");
            vWalletFile = new File(directory, filePrefix + ".wallet");
            boolean shouldReplayWallet = vWalletFile.exists() && !vChainFile.exists();
            if (vWalletFile.exists()) {
                walletStream = new FileInputStream(vWalletFile);
                vWallet = new WalletProtobufSerializer().readWallet(walletStream);
                if (shouldReplayWallet)
                    vWallet.clearTransactions(0);
            } else {
                vWallet = new Wallet(params);
            }
            if (vUseAutoSave) vWallet.autosaveToFile(vWalletFile, 1, TimeUnit.SECONDS, null);
            vStore = new SPVBlockStore(params, vChainFile);
            vChain = new BlockChain(params, vWallet, vStore);
            vPeerGroup = new PeerGroup(params, vChain);
            vPeerGroup.addWallet(vWallet);
            if (vPeerAddresses != null) {
                for (InetAddress addr : vPeerAddresses) vPeerGroup.addAddress(addr);
                vPeerAddresses = null;
            } else {
                vPeerGroup.addPeerDiscovery(new DnsDiscovery(params));
            }
            vPeerGroup.startAndWait();
            vPeerGroup.downloadBlockChain();
            // Make sure we shut down cleanly.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override public void run() {
                    try {
                        WalletAppKit.this.stopAndWait();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (BlockStoreException e) {
            throw new IOException(e);
        } finally {
            if (walletStream != null) walletStream.close();
        }
        
        
        
        
        
        
        
        setup();
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
    public static void snip(BigInteger x){
        System.out.print(x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
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
}
