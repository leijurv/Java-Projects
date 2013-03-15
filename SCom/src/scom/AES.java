/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scom;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author leijurv
 */
public class AES {/*
    public static String encode(String plaintext,String key){
        char[] pwd=key.toCharArray();
        byte[] tLOL=new byte[pwd.length];
        for (int i=0; i<tLOL.length; i++){
            tLOL[i]=(byte)(pwd[i]);
        }
        SecureRandom r=new SecureRandom(tLOL);
        byte[] salt=new byte[8];
        r.nextBytes(salt);
        byte[] res=encode1(pwd,salt,plaintext);
        String result=new String(res);
        
        return result;
    }
    public static String decode(String plaintext,String key){
        char[] pwd=key.toCharArray();
        byte[] tLOL=new byte[pwd.length];
        for (int i=0; i<tLOL.length; i++){
            tLOL[i]=(byte)(pwd[i]);
        }
        SecureRandom r=new SecureRandom(tLOL);
        byte[] salt=new byte[8];
        r.nextBytes(salt);
        byte[] a=plaintext.getBytes();
        return decode1(pwd,a,salt);
        
    }*/
    public static String encode(String plaintext, BigInteger key) {
        byte[] thing = key.toByteArray();
        Random st=new Random(thing[0]);
        for (int i=0; i<thing.length; i++){
            st.nextInt(130+thing[i]);
        }
        byte[] c=new byte[8];
        st.nextBytes(c);
        char[] pwd = new char[thing.length];
        for (int i = 0; i < pwd.length; i++) {
            pwd[i] = (char)thing[i];
        }
        byte[] encoded = encode1(pwd, c, plaintext);
        BigInteger ting = new BigInteger(encoded);
        String a = ting.toString(2);
        if (a.substring(0, 1).equals("-")) {
            a = "11" + a.substring(1, a.length());
        } else {
            a = "10" + a;
        }
        BigInteger b = new BigInteger(a, 2);

        return b.toString(16);
    }

    public static String decode(String cipherte, BigInteger key) {
        String ciphertex = (new BigInteger(cipherte, 16)).toString(2);
        String ciphertext = (ciphertex.substring(1, 2).equals("1") ? "-" : "") + ciphertex.substring(2, ciphertex.length());

        byte[] thing = key.toByteArray();
        Random st=new Random(thing[0]);
        for (int i=0; i<thing.length; i++){
            st.nextInt(130+thing[i]);
        }
        byte[] c=new byte[8];
        st.nextBytes(c);
        char[] pwd = new char[thing.length];
        for (int i = 0; i < pwd.length; i++) {

            pwd[i] = (char)thing[i];
        }
        byte[] a = new BigInteger(ciphertext, 2).toByteArray();
        while (a.length % 16 != 0) {
            byte b = 0;
            a = add(a, b);
        }
        String encoded = decode1(pwd, a, c);
        return encoded;
    }

    public static byte[] add(byte[] a, byte b) {
        byte[] c = new byte[a.length + 1];
        System.arraycopy(a, 0, c, 1, a.length);
        c[0] = b;
        return c;
    }

    //BTW, from here on, I didn't write the code.
    public static byte[] encode1(char[] password, byte[] salt, String plaintext) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
SecretKey tmp = factory.generateSecret(spec);
SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
/* Encrypt the message. */
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, secret);
AlgorithmParameters params = cipher.getParameters();
byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
            byte[] result = new byte[ciphertext.length + 16];
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

    public static String decode1(char[] password, byte[] ciphertet, byte[] salt) {
        try {
            byte[] ciphertext = new byte[ciphertet.length - 16];
            byte[] iv = new byte[16];
            System.arraycopy(ciphertet, 0, iv, 0, iv.length);
            System.arraycopy(ciphertet, 16, ciphertext, 0, ciphertext.length);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
SecretKey tmp = factory.generateSecret(spec);
SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
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
