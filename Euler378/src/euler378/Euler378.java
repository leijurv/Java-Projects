/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler378;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Euler378 {
    static long[] t=new long[60000001];
    public static long divSig(long i){
        long sum=0;
        for (long n=1; n<Math.sqrt(i); n++){
            if (i%n==0){
                sum++;
            }
        }
        sum*=2;
        if (Math.sqrt(i)%1==0){
            sum++;
        }
        return sum;
    }
    public static void main(String[] args){
        System.out.println("start");
        //System.out.println(divSig(360));
        for (long i=1; i<t.length; i++){
            if (i%1000==0){
                System.out.println("a"+i);
            }
            t[(int)i]=divSig(((i+1)*i)/2);
        }
        System.out.println("meow");
        long[][] l=new long[t.length][];
        for (int i=1; i<t.length; i++){
            if (i%1000==0){
                System.out.println("b"+i);
            }
            long k=t[i];
            long sum=0;
            for (int n=1; n<i; n++){
                if (t[n]>k){
                    sum++;
                }
            }
            l[i]=new long[]{k,sum};
            System.out.println(i+","+k+","+sum);
        }
        long sm=0;
        for (int j=1; j<t.length; j++){
            if (j%1000==0){
                System.out.println("c"+j);
            }
            long k=t[j];
            for (int i=1; i<j; i++){
                if (l[i][0]>k){
                    sm+=l[i][1];
                }
            }
        }
        System.out.println(sm);
        /*
        T[n_] := n*(n + 1)/2; Monitor[
 t = DivisorSigma[0, T[#]] & /@ Range[60000000]; l = {}; 
 For[i = 1, i <= Length[t], i++, k = t[[i]]; 
  AppendTo[l, {k, Length[Select[t[[;; i - 1]], # > k &]]}]]; sm = 0; 
 For[j = 1, j <= 60000000, j++, k = t[[j]]; 
  Select[l[[;; j - 1]], 
   If[#[[1]] > t[[j]], sm += #[[2]]; True, False] &]];, {i, j, sm}]
        */
    }
    
}
