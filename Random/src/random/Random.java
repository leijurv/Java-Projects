/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package random;
import java.math.BigInteger;
/**
 *
 * @author leijurv
 */
public class Random {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BigInteger p=new BigInteger("12844205165381031491662259028977553198964984323915864368216177647043137765477");
        String N="10742788291266565907178411279942116612663921794753294588877817210355464150980121879033832926235281090750672083504941996433143425558334401855808989426892463";
        BigInteger n=new BigInteger(N);
        BigInteger q=n.divide(p);
        System.out.println(p);
        System.out.println(q);
        System.out.println(p.multiply(q));
        System.out.println(n);
        BigInteger e=new BigInteger("3735928559");
        BigInteger t=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger m=new BigInteger("5021");
        BigInteger d=e.modPow(BigInteger.ZERO.subtract(BigInteger.ONE),t);
        BigInteger c=m.modPow(e, n);
        System.out.println(c);
        System.out.println(c.modPow(d,n));
        // TODO code application logic here
    }
}
