/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class Address {
    RSAKeyPair address;
    BigInteger value;
    String addr;
    public static boolean verify(byte[] b) throws NoSuchAlgorithmException{
        byte[] c=new byte[128];
        for (int i=0; i<128; i++){
            c[i]=b[i+(b.length-128)];
            b[i+(b.length-128)]=0;
        }
        RSAKeyPair comPubKey=new RSAKeyPair();
            comPubKey.modulus=AIB_Client.comPubKey;
            comPubKey.pub=AIB_Client.e;
                MessageDigest m=MessageDigest.getInstance("SHA1");
                m.reset();
                m.update(b);
                byte[] sig=m.digest();
                System.out.println("Hsh: "+new BigInteger(sig).toString(16));
                BigInteger k=comPubKey.encode(new BigInteger(c));
                System.out.println("Sig: "+k.toString(16));
                return k.compareTo(new BigInteger(sig).add(AIB_Client.comPubKey).mod(AIB_Client.comPubKey))==0;
    }
    public static BigInteger fetchValue(RSAKeyPair address) throws Exception{
            String s= AIB_Client.load(AIB_Client.web+"getbal/"+address.modulus.toString(16));
            //System.out.println(s);
            byte[] B=Hex.decodeHex(s.toCharArray());
            //System.out.println(B.length);
            byte[] a=new byte[128];
            byte[] b=new byte[10];
            for (int i=0; i<128; i++){
                a[i]=B[i];
            }
            for (int i=0; i<10; i++){
                b[i]=B[i+128];
            }
            if (verify(B) && new BigInteger(a).compareTo(address.modulus)==0){
                   return new BigInteger(b);
            }
            //return BigInteger.ZERO;
               throw new RuntimeException("");
    }
    public static String fetchAddr(RSAKeyPair address) throws Exception{
            String s= AIB_Client.load(AIB_Client.web+"getaddr/"+address.modulus.toString(16));
            byte[] B=Hex.decodeHex(s.toCharArray());
            byte[] a=new byte[128];
            byte[] b=new byte[31];
            for (int i=0; i<128; i++){
                a[i]=B[i];
            }
            for (int i=0; i<31; i++){
                b[i]=B[i+128];
            }
           if (verify(B) && new BigInteger(a).compareTo(address.modulus)==0){
               return new String(b);
                }
                throw new RuntimeException("");
    }
    public Address(RSAKeyPair key){
        address=key;
        boolean finished=false;
        int tries=0;
        while (tries<15 && !finished){
            finished=true;
        try {
            value=fetchValue(key);
        } catch (Exception ex) {
            finished=false;
        }
        tries++;
        }
        if (tries==15){
            value=BigInteger.TEN;//THIS IS HORRIBLE HORRIbLE HORRIbLE CODE
        }
        System.out.println("Took "+tries+" tries to fetch Value for "+(address.modulus.toString(16)));
        finished=false;
        tries=0;
        while (tries<10 && !finished){
            finished=true;
        try {
            addr=fetchAddr(key);
        } catch (Exception ex) {
            finished=false;
        }
        tries++;
        }
        if (tries==10){
            addr="";//SO IS THIS
        }
        System.out.println("Took "+tries+" tries to fetch Addr for "+(address.modulus.toString(16)));
        System.out.println(address.modulus+addr);
    }
    public String toString(){
        return address.modulus.toString(16);
    }
}
