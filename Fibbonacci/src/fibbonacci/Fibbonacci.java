/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fibbonacci;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Fibbonacci {
    static ArrayList<BigInteger> values=new ArrayList<BigInteger>();
    /**
     * @param args the command line arguments
     */
    public static BigInteger get(int i){
        if (values.size()>i && values.get(i)!=null){
            return values.get(i);
        }
        if (i-values.size()>10000){
            get(i-10000);
        }
        
        BigInteger r=get(i-1).add(get(i-2));
        values.add(i,r);
        if (i>2)
        values.set(i-3, null);
        if (i%10000==0){
            System.out.println(i);
        }
        return r;
    }
    public static BigInteger[] get1(int i){
        if (i==1){
            return new BigInteger[]{BigInteger.ZERO,BigInteger.ONE};
        }
        BigInteger[] n=get1(i-1);
        return new BigInteger[] {n[1],n[0].add(n[1])};
    }
    public static void main(String[] args) {
        values.add(BigInteger.ZERO);
        values.add(BigInteger.ONE);
        get(1000000);
        System.out.println(values.get(1000000).toString());
    }
}
