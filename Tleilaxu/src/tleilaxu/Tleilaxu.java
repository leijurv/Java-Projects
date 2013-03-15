/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tleilaxu;
import cryptolib.AES;
import cryptolib.ReedSolomon;
import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Tleilaxu {
public static void testRS(){
    byte[] x={5,0,2,1};
        System.out.println("Original message is");
        for (byte a :x){
            System.out.println(a);
        }
        byte[] y=ReedSolomon.encode(x,2);
        System.out.println("Encoded version is ");
        for (byte a : y){
            System.out.println(a);
        }
        y[0]=100;
        System.out.println("Corrupted to ");
        for (byte a : y){
            System.out.println(a);
        }
        byte[] z=ReedSolomon.decode(y,2);
        System.out.println("Decoded to");
        for (byte a:z){
            System.out.println(a);
        }
}
public static void testAES(){
    String message="ë";
        Random r=new Random();
        for (int i=1; i<100; i++){
            BigInteger key=new BigInteger(10,r);
            byte[] x=AES.encode(message,key);
            System.out.print(new String(x));
            System.out.print("      ");
            System.out.print(key);
            System.out.print("      ");
            System.out.println(AES.decode(x,key));
        }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        testAES();
    }
}
