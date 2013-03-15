/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scom;

import java.math.BigInteger;
import java.util.Random;
/**
 *
 * @author leif
 */
public class MessageCoder {
    BigInteger session_key;
    BigInteger encoded_session_key;
    RSAKeyPair my_key;
    RSAKeyPair their_key;
    public MessageCoder(RSAKeyPair MyKey,RSAKeyPair TheirKey){
        my_key=MyKey;
        their_key=TheirKey;
    }
    public String encode(String message){
        session_key=new BigInteger(256,new Random());
        encoded_session_key=my_key.decode(their_key.encode(session_key));
        String ESK_Base16=encoded_session_key.toString(16);
        String encMessage=AES.encode(message,session_key);
        String res=ESK_Base16+","+encMessage;
        return res;
    }
    public String decode(String message){
        String[] m=message.split(",");
        String ESK_Base16=m[0];
        String encMessage=m[1];
        encoded_session_key=new BigInteger(ESK_Base16,16);
        session_key=my_key.decode(their_key.encode(encoded_session_key));
        return AES.decode(encMessage,session_key);
    }
}
