/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aes.stuff;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author leijurv
 */
public class AES {
    public static String encode(String plaintext, BigInteger key){
        byte[] thing=key.toByteArray();
        char[] pwd=new char[thing.length];
        for (int i=0; i<pwd.length; i++){
            char c=Integer.toString((int)thing[i]).toString().charAt(0);
            if (c=='-'){
                c=Integer.toString((int)thing[i]).toString().charAt(1);
            }
            pwd[i]=c;
        }
        byte[] encoded=encode1(pwd,thing,plaintext);
        BigInteger ting=new BigInteger(encoded);
        String n=ting.toString(2);
        if (n.startsWith("-")){
            n="1"+n.substring(1,n.length());
        }else{
            n="0"+n.substring(1,n.length());
        }
        return n;
    }
    public static String decode(String ciphertext, BigInteger key){
        if (ciphertext.startsWith("0")){
            ciphertext=ciphertext.substring(1,ciphertext.length());
        }else{
            ciphertext="-"+ciphertext.substring(1,ciphertext.length());
        }
        byte[] thing=key.toByteArray();
        char[] pwd=new char[thing.length];
        for (int i=0; i<pwd.length; i++){
            char c=Integer.toString((int)thing[i]).toString().charAt(0);
            if (c=='-'){
                c=Integer.toString((int)thing[i]).toString().charAt(1);
            }
            pwd[i]=c;
        }
        byte[] a=new BigInteger(ciphertext,2).toByteArray();
        if (a.length%16!=0){
            byte[] b=new byte[a.length+1];
            for (int i=0; i<a.length; i++){
                b[i]=a[i];
            }
            b[a.length]=0;
            a=b;
        }
        String encoded="";
        try{
        encoded=decode1(pwd,a,thing);
        }catch(Exception e){
            int x=Integer.parseInt("dsaf");
        }
        return encoded;
    }
    public static byte[] encode1(char[] password, byte[] salt, String plaintext){
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters params = cipher.getParameters();
    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext=cipher.doFinal(plaintext.getBytes("UTF-8"));
    byte[] result=new byte[ciphertext.length+16];
            System.arraycopy(iv, 0, result, 0, iv.length);
           System.arraycopy(ciphertext, 0, result, 16, ciphertext.length);
           return result;
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidParameterSpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static String decode1(char[] password, byte[] ciphertet, byte[] salt){
        try {
            byte[] ciphertext=new byte[ciphertet.length-16];
            byte[] iv=new byte[16];
            System.arraycopy(ciphertet, 0, iv, 0, iv.length);
            System.arraycopy(ciphertet, 16, ciphertext, 0, ciphertext.length);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    String plaintext = new String(cipher.doFinal(ciphertext),"UTF-8");
    return plaintext;
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
