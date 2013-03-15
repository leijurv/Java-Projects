/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aes;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author leijurv
 */
public class AES {
    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        System.out.print("Encode or decode? (e/d) >");
        String a=scan.nextLine();
        System.out.print("Key? (Number) >");
        String k=scan.nextLine();
        BigInteger key=new BigInteger(k);
        if (a.equals("d")){
            System.out.print("What would you like to decode?  >");
            String thing=scan.nextLine();
            System.out.println(decode(thing,key));
            return;
        }
        if (a.equals("e")){
            System.out.print("What would you like to encode?  >");
            String thing=scan.nextLine();
            System.out.println(encode(thing,key));
            return;
        }
        System.out.println("Unknown");
    }
    public static String encode(String plaintext, BigInteger key){
        byte[] thing=key.toByteArray();
        char[] pwd=new char[thing.length];
        for (int i=0; i<pwd.length; i++){
            pwd[i]=Integer.toString((int)thing[i]<0?0-(int)thing[i]:(int)thing[i]).toString().charAt(0);
        }
        byte[] encoded=encode1(pwd,thing,plaintext);
        BigInteger ting=new BigInteger(encoded);
        String a=ting.toString(2);
        if (a.substring(0,1).equals("-")){
            a="11"+a.substring(1,a.length());
        }else{
            a="10"+a;
        }
        BigInteger b=new BigInteger(a,2);
        
        return b.toString(16);
    }
    public static String decode(String cipherte, BigInteger key){
        String ciphertex=(new BigInteger(cipherte,16)).toString(2);
        String ciphertext= (ciphertex.substring(1,2).equals("1")?"-":"") +ciphertex.substring(2,ciphertex.length());
        
        byte[] thing=key.toByteArray();
        char[] pwd=new char[thing.length];
        for (int i=0; i<pwd.length; i++){
            
            pwd[i]=Integer.toString((int)thing[i]<0?0-(int)thing[i]:(int)thing[i]).toString().charAt(0);
        }
        byte[] a=new BigInteger(ciphertext,2).toByteArray();
        while(a.length%16!=0){
            byte b=-128;
            a=add(a,b);
        }
        String encoded=decode1(pwd,a,thing);
        return encoded;
    }
    public static byte[] add(byte[] a, byte b){
        byte[] c=new byte[a.length+1];
        System.arraycopy(a, 0, c, 1, a.length);
        c[0]=b;
        return c;
    }
    
    
    //BTW, from here on, I didn't write the code.
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
