/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sha1;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leijurv
 */
public class Sha1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String thing="";
        MessageDigest cript = MessageDigest.getInstance("SHA-1");
              cript.reset();
              cript.update(thing.getBytes("utf8"));
              String password = new String(Hex.encodeHex(cript.digest()));
              System.out.println(password);
        // TODO code application logic here
    }
}
