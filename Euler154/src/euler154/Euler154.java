/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler154;

/**
 *
 * @author leijurv
 */
public class Euler154 {
    static final int n=200000;
    static int[] nt=new int[n+1];
    static int[] nf=new int[n+1];
    public static int numtwo(int x){
        if (x==0){
            return 0;
        }
        int sum=0;
        for (int i=1; i<=18; i++){
            sum+=Math.floor(((double)x)/Math.pow(2D,(double)i));
        }
        return sum;
    }
    public static int numfive(int x){
        if (x==0){
            return 0;
        }
        int sum=0;
        for (int i=1; i<=8; i++){
            sum+=Math.floor(((double)x)/Math.pow(5D,(double)i));
        }
        return sum;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        System.out.println(numtwo(5207));
        
       for (int i=0; i<=n; i++){
           nt[i]=numtwo(i);
           nf[i]=numfive(i);
       }
       int ntn=nt[n];
       int nfn=nf[n];
       long num=0;
       for (int i=0; i<=n; i++){
           if (i%100==0)
           System.out.println(i+","+num);
           int nti=nt[i];
           int nfi=nf[i];
           for (int j=i; j<n-i*2; j++){
               int k=n-(i+j);
               
               
               int Nt=ntn-(nti+nt[j]+nt[k]);
               if (Nt>=12){
                   if (nfn-(nfi+nf[j]+nf[k])>=12){
                       num+=3;
                   }
               }
           }
       }
       System.out.println(num);
    }
    
}
