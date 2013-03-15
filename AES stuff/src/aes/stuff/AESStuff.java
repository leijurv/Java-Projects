/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aes.stuff;


import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class AESStuff {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String s="Hello, World!";
        for (int i=0; i<500; i++){
            BigInteger n=new BigInteger(100,new Random());
            String e=AES.encode(s,n);
            
            String d="";
            try{
                d=AES.decode(e,n);
            }catch(Exception w){
                System.out.println(e);
                System.out.println(n);
                System.out.println(d);
                System.out.println();
            }
            
        }
    }
}
