/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seccom;

import cryptolib.AES;
import cryptolib.Hex;
import cryptolib.RSAKeyPair;
import cryptolib.ReedSolomon;
import cryptolib.SHA1;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class SecCom {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        RSAKeyPair mine=new RSAKeyPair();
        RSAKeyPair theirs=new RSAKeyPair();
        Random r=new Random(5021);
        mine.generate(new BigInteger(510,100,r), new BigInteger(514,100,r), new BigInteger("65537"),false);
        theirs.generate(new BigInteger(510,100,r), new BigInteger(514,100,r), new BigInteger("65537"),false);
        String encoded=encode("HERPDERP123",mine,theirs,r);
        System.out.println(decode(encoded,theirs,mine));
    }
    public static String encode(String message,RSAKeyPair mine,RSAKeyPair theirs,Random r){
        BigInteger session_key=new BigInteger(256,r);
        byte[] message_hash=(new SHA1()).hash(message);
        BigInteger hash=new BigInteger(message_hash);
        BigInteger encoded_hash=mine.decode(hash);
        String Encoded_Hash=Hex.encodeHexString(encoded_hash.toByteArray());
        while(Encoded_Hash.length()<258){
            Encoded_Hash="0"+Encoded_Hash;
        }
        String toBeEncoded=Encoded_Hash+message;
        byte[] encoded_message=AES.encode(toBeEncoded,session_key);
        byte[] esk=theirs.encode(session_key).toByteArray();
        String Esk=Hex.encodeHexString(esk);
        while(Esk.length()<258){
            Esk="0"+Esk;
        }
        String Encoded=Hex.encodeHexString(encoded_message);
        String result=Esk+Encoded;
        System.out.println(result);
        byte[] c=Hex.decodeHex(result.toCharArray());
        return Hex.encodeHexString(ReedSolomon.encode(c,6));
    }
    public static String decode(String ciphertex,RSAKeyPair mine,RSAKeyPair theirs){
        char[] u=ciphertex.toCharArray();
        String ciphertext=Hex.encodeHexString(ReedSolomon.decode(Hex.decodeHex(u), 6));
        System.out.println(ciphertext);
        String Esk=ciphertext.substring(0,258);
        String Encoded=ciphertext.substring(258,ciphertext.length());
        byte[] esk=Hex.decodeHex(Esk.toCharArray());
        BigInteger session_key=mine.decode(new BigInteger(esk));
        byte[] encoded=Hex.decodeHex(Encoded.toCharArray());
        String Decoded=AES.decode(encoded,session_key);
        String message=Decoded.substring(258,Decoded.length());
        String hash=Decoded.substring(0,258);
        byte[] message_hash=(new SHA1()).hash(message);
        BigInteger Hash=new BigInteger(message_hash);
        BigInteger decoded_hash=theirs.encode(new BigInteger(Hex.decodeHex(hash.toCharArray())));
        boolean b=decoded_hash.equals(Hash);
        if (!b){
            System.out.println("WROND HASH!");
        }
        return message;
    }
}
