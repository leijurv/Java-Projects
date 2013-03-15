/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ackermann;

import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class Ackermann {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BigInteger a=A(new BigInteger("4"),new BigInteger("2"));
        System.out.println();
    }
    public static BigInteger A(BigInteger m, BigInteger n){
        System.out.println("A("+m+","+n+")");
        if (m.compareTo(BigInteger.ZERO)==0){
            return BigInteger.ONE.add(n);
        }
        if (n.compareTo(BigInteger.ZERO)==0){
            return A(m.subtract(BigInteger.ONE),BigInteger.ONE);
        }
        return A(m.subtract(BigInteger.ONE),A(m,n.subtract(BigInteger.ONE)));
    }
}
