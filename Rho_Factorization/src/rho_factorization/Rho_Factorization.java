/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rho_factorization;
import java.math.BigInteger;
import java.util.Random;
/**
 *
 * @author leif
 */
public class Rho_Factorization {

    /**
     * @param args the command line arguments
     */
    static Random rand=new Random();
    public static void main(String[] args) {
        System.out.println(factor(822549));
    }
    public static  int factor(int n){
        int x=2;
        int y=2;
        int d=1;
        while(d==1){
            x=f(x,n);
            y=f(f(y,n),n);
            d=gcdThing(Math.abs(x-y),n);
        }
        if (d==n){
            return -1;
        }
        return d;
    }
    public static int f(int x, int n){
        
        return (new Random(x)).nextInt(n);
    }
    private static int gcdThing(int a, int b) {
    BigInteger b1 = new BigInteger(""+a); // there's a better way to do this. I forget.
    BigInteger b2 = new BigInteger(""+b);
    BigInteger gcd = b1.gcd(b2);
    return gcd.intValue();
}
}
