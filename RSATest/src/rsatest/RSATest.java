/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rsatest;
import java.math.BigInteger;
import java.util.Random;
/**
 *
 * @author leijurv
 */
public class RSATest {

    /**
     * @param args the command line arguments
     */
    public static void gen(RSAKeyPair r, Random rand, int bits){
        //System.out.println("Calculating P");
       BigInteger p=new BigInteger(bits/2,100,rand);
        //System.out.println("Calculated P");
        //System.out.println("Calculating Q");
        BigInteger q=new BigInteger(bits/2,100,rand);
        //System.out.println("Calculated Q");
        r.generate(p,q,new BigInteger("65537"),false);
    }
        
    public static void main(String[] args) {
        RSAKeyPair r=new RSAKeyPair();
        Random rand=new Random(5021);
        int bits=2048;
        gen(r,rand,bits);
        
        
        String s="Happy Bar Mitzvah, Zach! I can't wait to hear those two hours of Hebrew! If you're reading this, nice job decoding it. I assume you used Mathematica or whatever. Thanks for inviting me! -Leif";
        byte[] b=s.getBytes();
        BigInteger x=new BigInteger(b);
        x=r.encode(x);
        String n=r.modulus.toString(10);
        String d=r.pri.toString(10);
        String message=x.toString(10);
        
        
        System.out.println("RSA ASCII Hex");
        
        System.out.println("Encoded message: 0x"+message);
        
        System.out.println("D: 0x"+d);
        
        System.out.println("N: 0x"+n);
        
        
        
        RSAKeyPair rsa=new RSAKeyPair();
        rsa.modulus=new BigInteger(n,16);
        rsa.pri=new BigInteger(d,16);
        BigInteger result=rsa.decode(new BigInteger(message,16));
        byte[] bytes=result.toByteArray();
        System.out.println(new String(bytes));
        for (byte u : b){
            System.out.print(u+",");
        }
        System.out.println();
        System.out.println((rsa.modulus).bitLength());
        System.out.println((new BigInteger(b)).bitLength());
        
    }
}
