/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reed_solomon;

import cryptolib.AES;
import cryptolib.GenericGF;
import cryptolib.ReedSolomon;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author leif
 */
public class Reed_Solomon{
    static final int base=10;
public static void screwup(byte[] a,int change,Random r){
    for (int i=0; i<change; i++){
        a[r.nextInt(a.length)]=(byte) (r.nextInt(256)-128);
    }
}
public static void main(String[] args) throws UnsupportedEncodingException{
    byte[] b=new byte[5];
    b[0]=1;
    b[1]=2;
    b[2]=3;
    b[3]=4;
    b[4]=5;
    b=ReedSolomon.encode(b,2);
    for (byte B : b){
        System.out.println(B);
    }
    System.out.println("next"+b.length);
    //b[5]+=2;
    ReedSolomon.decode(b,2);
}
    /**
     * @param args the command line arguments
     */
    
    public static void man(String[] args) throws UnsupportedEncodingException {
        Scanner scan=new Scanner(System.in);
        BigInteger X=new BigInteger("1000000000000000000000000",base);
        int ec = 257;
        Random r=new Random();
        
        
        byte[] b=X.toByteArray();
        System.out.println("Using "+ec+" error-correcting bytes.");
       
        System.out.println();
        System.out.print("Encoding:  ");
        print(b);
        
        
        b=ReedSolomon.encode(b, ec);
        System.out.print("Encoded:   ");
        print(b);
        
        
        screwup(b,ec/2,r);
        System.out.print("Messed up: ");
        print(b);
        
        b=ReedSolomon.decode(b,ec);
        System.out.print("Decoded:   ");
        print(b);
    }
    public static void print(byte[] b){
        
        for (int i=0; i<b.length; i++){
            System.out.print((b[i]+128)+(i!=b.length-1?",":""));
        }
        System.out.print(" or ");
        try {
            String s=new String(b,"utf8");
            //System.out.print(s);
        } catch (UnsupportedEncodingException ex) {
        }
        System.out.print("in base "+base+": "+new BigInteger(b).toString(base));
    System.out.println();
    }
    
}
