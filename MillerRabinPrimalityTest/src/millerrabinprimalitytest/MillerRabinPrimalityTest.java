/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package millerrabinprimalitytest;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class MillerRabinPrimalityTest {

    /**
     * @param args the command line arguments
     */
    BigInteger n;
    BigInteger k;
    BigInteger s=one;
    BigInteger d;
    static final BigInteger zero=BigInteger.ZERO;
    static final BigInteger one=BigInteger.ONE;
    static final BigInteger two=new BigInteger("2");
    public MillerRabinPrimalityTest(BigInteger N, BigInteger K){
        n=N;
        k=K;
        calc();
    }
    public boolean check(){
        Random rand=new Random();
        for (BigInteger q=zero; q.compareTo(k)!=1; q=q.add(one)){
            BigInteger a=new BigInteger(n.bitCount()-1,rand);
            //System.out.println(a);
            boolean c=doItr(a);
            if (c){
                return false;
            }
        }
        return true;
    }
    public boolean doItr(BigInteger a){
        BigInteger x=a.modPow(d,n);
        if (x.compareTo(one)==0 || x.compareTo(n.subtract(one))==0){
            return false;
        }
        for (BigInteger r=one; r.compareTo(s)==-1; r=r.add(one)){
            x=x.modPow(two,n);
            if (x.compareTo(one)==0){
                return true;
            }
            if (x.compareTo(n.subtract(one))==0){
                return false;
            }
        }
        return true;
    }
    public final void calc(){
        calcS();
        calcD();
                
    }
    //TODO: Find a way to not have to use int here: Find a way to take 2^ a biginteger without turning to to an int.
    public void calcS(){
        if (n.subtract(one).mod(new BigInteger("2")).compareTo(zero)!=0){
            s=zero;
            return;
        }
        while(n.subtract(one).mod(pow(two,s)).compareTo(zero)==0){
            s=s.add(one);
        }
        s=s.subtract(one);
    }
    public BigInteger pow(BigInteger a, BigInteger b){
        return a.pow(b.intValue());
    }
    public void calcD(){
        d=n.divide(pow(two,s));
    }
}
