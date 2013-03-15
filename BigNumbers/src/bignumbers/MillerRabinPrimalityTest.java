/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumbers;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class MillerRabinPrimalityTest {

    /**
     * @param args the command line arguments
     */
    BigNumber n;
    BigNumber k;
    BigNumber s=one;
    BigNumber d;
    static final BigNumber zero=new BigNumber(new boolean[] {false});
    static final BigNumber one=new BigNumber("1");
    static final BigNumber two=new BigNumber("2");
    public MillerRabinPrimalityTest(BigNumber N, BigNumber K){
        //System.out.println(N);
        n=N;
        k=K;
        calc();
    }
    public boolean check(){
        System.out.println("derp");
        Random rand=new Random();
        for (BigNumber q=zero; q.compareTo(k)!=1; q=q.add(one)){
            BigNumber a=new BigNumber(n.val.length-1,rand);
            
            System.out.println(q);
            System.out.println(k);
            System.out.println(q.compareTo(k));
            boolean c=doItr(a);
            if (c){
                return false;
            }
        }
        return true;
    }
    public boolean doItr(BigNumber a){
        BigNumber x=a.modPow(d,n);
        if (x.compareTo(one)==0 || x.compareTo(n.subtract(one))==0){
            return false;
        }
        for (BigNumber r=one; r.compareTo(s)==-1; r=r.add(one)){
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
    //TODO: Find a way to not have to use int here: Find a way to take 2^ a BigNumber without turning to to an int.
    public void calcS(){
        if (n.subtract(one).mod(new BigNumber("2")).compareTo(zero)!=0){
            s=zero;
            return;
        }
        while(n.subtract(one).mod(pow(two,s)).compareTo(zero)!=0){
            //System.out.println(s);
            s=s.add(one);
        }
        s=s.subtract(one);
    }
    public BigNumber pow(BigNumber a, BigNumber b){
        return a.pow(Integer.parseInt(b.toString()));
    }
    public void calcD(){
        d=n.divide(pow(two,s));
    }
    public static void main(String[] args) {
        for (int i=0; i<10; i++){
            
        BigNumber x=new BigNumber("5010");
        MillerRabinPrimalityTest t=new MillerRabinPrimalityTest(x,new BigNumber("1000"));
        boolean pr=t.check();
        while(!pr){
            x=x.add(MillerRabinPrimalityTest.one);
            t=new MillerRabinPrimalityTest(x,new BigNumber("1000"));
            pr=t.check();
        }
        System.out.println(x);
    }
    }
}
