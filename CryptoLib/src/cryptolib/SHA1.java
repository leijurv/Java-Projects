/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptolib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class SHA1 {
    private MessageDigest crypt;
    public SHA1(){
        try {
            crypt=MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
        }
        crypt.reset();
    }
    /**
     * Hash function
     * @param b The String to be hashed
     * @return The hashed value
     */
    
    public byte[] hash(String s){
        return hash(s.getBytes());
    }
    /**
     * Hash function
     * @param b The bytes to be hashed
     * @return The hashed value
     */
    public byte[] hash(byte[] b){
        crypt.update(b);
        byte[] c=crypt.digest();
        crypt.reset();
        return c;
    }
    /**
     * Hash function
     * @param b The bytes to be hashed
     * @return The hashed value, in hex.
     */
    public String hashHex(byte[] b){
        return Hex.encodeHexString(hash(b));
    }
     
}
