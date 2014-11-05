/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler439;

/**
 *
 * @author leijurv
 */
public class Euler439 {
    public static long divSig(long a){
        long sum=0;
        for (long i=1; i<=Math.sqrt(a); i++){
            if (a%i==0){
                sum+=i;
                sum+=a/i;
            }
        }
        if (Math.sqrt(a)%1==0){
            sum-=Math.sqrt(a);
        }
        return sum;
    }
    public static long S(long N){
        long sum=0;
        for (long i=1; i<=N; i++){
            for (long j=i; j<=N; j++){
                sum+=2*divSig(i*j);
            }
        }
        return sum;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        for (int i=1; i<=9; i++){
            System.out.println(i+","+divSig(i));
        }
        System.out.println(S(3));
        System.out.println(S(1000));
        // TODO code application logic here
    }
    
}
