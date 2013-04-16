/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler423;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Euler423 {
    static ArrayList<Integer> cache=new ArrayList<Integer>();
    public static boolean prime(int x){
        if (x==2){
            return true;
        }
        if (x==3){
            return true;
        }
        for (int i=2; i<=Math.sqrt(x);i++){
            if (x%i==0){
                return false;
            }
        }
        return true;
    }
    public static int primepi(int q){
        if (q==2){
            return 1;
        }
        if (q==50000000){
            return 3001134;
        }
        
        if (cache.get(q)!=0){
            return cache.get(q);
        }
        if (q%1000==0){
            System.out.println(q);
        }
        int x=primepi(q-1)+(prime(q)?1:0);
        cache.set(q,x);
        return x;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int value=1205;
        for (int i=0; i<=value; i++){
            cache.add(0);
            if (i%100==5){
                primepi(i);
            }
        }
        System.out.println("MEOW");
        int PRIMEPI=primepi(value);
        System.out.println("Primepi"+PRIMEPI);
        long[] F=new long[PRIMEPI+1];
        System.out.println("Did it");
        F[0]=5;
        F[1]=1;
        int i=1;
        long x=1000000007;
        long overall=0;
        int pp=1;
        long y=System.currentTimeMillis();
        while(i<value-1){
            
            i++;
            pp+=prime(i+1)?1:0;
            for (int n=F.length-1; n>=1; n--){
                F[n]=(5*F[n]+F[n-1])%x;
            }
            F[0]=5*F[0]%x;
            long sum=0;
            for (int I=0; I<=pp; I++){
                sum+=6*F[I];
            }
            sum=sum%x;
            overall=(overall+sum)%x;
            for (int I=0; I<F.length; I++){
                //System.out.print(F[I]+",");
            }
           // System.out.println();
        }
        System.out.println(System.currentTimeMillis()-y);
        System.out.println(overall+42);
    }
}
