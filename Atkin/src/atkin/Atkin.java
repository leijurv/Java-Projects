/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atkin;
/**
 *
 * @author leijurv
 */
public class Atkin {
    public static boolean isPrime(int n){
        if (n==1){
            return false;
        }
        if (n==2){
            return true;
        }
        if (n%2==0){
            return false;
        }
        for (int i=3; i<=Math.sqrt(n); i+=2){
            if (n%i==0){
                return false;
            }
        }
        return true;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        //int limit=100000000;
        int limit=100000;
        long t=System.currentTimeMillis();
        boolean[] ip=new boolean[limit+1];
        ip[2]=true;
        ip[3]=true;
        double sq=Math.sqrt(limit);
        for (int x=1; x<sq; x++){
            if (x%10000==0)
            System.out.println(x);
            int xx4=4*x*x;
            int xx3=3*x*x;
            for (int y=1; y<sq; y++){
                int yy=y*y;
                int n=xx4+yy;
                if (n<=limit&&(n%12==1||n%12==5)){
                    ip[n]=!ip[n];
                }
                n=xx3+yy;
                if (n<=limit&&n%12==7){
                    ip[n]=!ip[n];
                }
                n=xx3-yy;
                if (x>y&&n<=limit&&n%12==11){
                    ip[n]=!ip[n];
                }
            }
        }
        for (int n=5; n<=sq; n++){
            if (ip[n]){
                for (int j=1; n*n*j<=limit; j++){
                    ip[n*n*j]=false;
                }
            }
        }
        System.out.println(System.currentTimeMillis()-t);
        int num=0;
        for (int i=0; i<ip.length; i++){
            if (ip[i]){
                num++;
            }
        }
        System.out.println("Num: "+num);
        
        for (int i=0; i<ip.length; i++){
            if (ip[i]^isPrime(i)){
                System.out.println("lol"+i);
            }
        }
    }
}
