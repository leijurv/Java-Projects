/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math.hmwk;
import java.math.*;
/**
 *
 * @author leijurv
 */
public class MathHMWK {

    /**
     * @param args the command line arguments
     */
    static BigInteger[] values=new BigInteger[100];
    public static BigInteger fib(int number){
        if (!values[number-1].equals(BigInteger.ZERO)){
            return values[number-1];
        }
        BigInteger ret=fib(number-1).add(fib(number-2));
        values[number-1]=ret;
        return ret;
    }
    public static void main(String[] args) {
        values[0]=BigInteger.ONE;
        values[1]=new BigInteger("3");
        for (int i=2; i<100; i++){
            values[i]=BigInteger.ZERO;
        }
        for (int i=1; i<=100; i++){
            System.out.println(i + ":" + fib(i));
        }
            
    }
}
