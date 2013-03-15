/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package big;
import java.math.BigInteger;
/**
 *
 * @author leijurv
 */
public class Big {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BigInteger modulus=(new BigInteger("2")).pow(6972593).subtract(BigInteger.ONE);
        System.out.println("Got mod");
        System.out.println(modulus);
        /*
        BigInteger t=(new BigInteger("2")).pow(32).subtract(BigInteger.ONE);
        System.out.println("Got t");
        t=t.multiply(new BigInteger("8")).subtract(BigInteger.ONE);
        System.out.println("Got t");
        BigInteger x=(new BigInteger("2")).modPow(t,modulus).subtract(BigInteger.ONE);
        System.out.println("GOT IT");*/
        // TODO code application logic here
    }
}
