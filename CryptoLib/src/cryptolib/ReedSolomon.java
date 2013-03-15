/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptolib;

/**
 *
 * @author leif
 */
public class ReedSolomon {
    /**
     * 
     * @param b The byte array to be encoded
     * @param ecBytes The amount of error-correcting bytes to be added
     * @return The error-correcting bytes appended to the input
     */
    public static byte[] decode(byte[] b, int ecBytes){
        
        int[] toEncode=new int[b.length];
        for (int i=0; i<b.length; i++){
            toEncode[i]=b[i]+128;
        }
        ReedSolomonDecoder e=new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
        int[] code=toEncode;
        try {
            e.decode(code,ecBytes);
        } catch (ReedSolomonException ex) {
            return new byte[0];
        }
        byte[] result=new byte[code.length-ecBytes];
        for (int i=0; i<result.length; i++){
            result[i]=(byte)(code[i]-128);
        }
        return result;
    }
    /**
     * 
     * @param b The message and the error-correcting bytes 
     * (Can handle up to and including floor(ecBytes/2) errors, e.g. zeroing out a byte)
     * @param ecBytes The amount of error-correcting bytes (Must be same amount as when it was encoded)
     * @return The message, with errors fixed
     */
    public static byte[] encode(byte[] b, int ecBytes){
        int[] toEncode=new int[b.length];
        for (int i=0; i<b.length; i++){
            toEncode[i]=b[i]+128;
        }
        ReedSolomonEncoder e=new ReedSolomonEncoder(GF256.QR_CODE_FIELD);
        int[] code=new int[toEncode.length+ecBytes];
        System.arraycopy(toEncode, 0, code, 0, toEncode.length);
        for (int i=toEncode.length; i<code.length; i++){
            code[i]=0;
        }
        e.encode(code,ecBytes);
        byte[] result=new byte[code.length];
        for (int i=0; i<code.length; i++){
            result[i]=(byte)(code[i]-128);
        }
        return result;
    }
    
}
