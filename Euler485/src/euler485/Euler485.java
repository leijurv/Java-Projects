/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler485;
/**
 *
 * @author leijurv
 */
public class Euler485 {
    static int[] numDiv=new int[100100001];
    public static int numDivv(int i){
        if (numDiv[i]!=-1){
            return numDiv[i];
        }
        if (i%2==0 && (i/2)%2!=0){
            return 2*numDivv(i/2);
        }
        if (i%3==0 && (i/3)%3!=0){
            return 2*numDivv(i/3);
        }
        int sum=0;
        double d=Math.sqrt(i);
        for (int n=1; n<d; n++){
            if (i%n==0){
                sum++;
            }
        }
        sum*=2;
        if (d%1==0){
            sum++;
        }
        numDiv[i]=sum;
        return sum;
    }
    public static int[] M(int n,int k){
        int max=-1;
        int m=0;
        for (int j=n; j<=n+k-1; j++){
            int nn=numDivv(j);
            if (nn>max){
                max=nn;
                m=j;
            }
        }
        return new int[]{max,m};
    }
    public static long S(int u,int k){
        long sum=0;
        int mostRecentMax=-1;
        int mostRecentIndex=-1;
        for (int n=1; n<=u-k+1; n++){
            if (n%100000==0){
                System.out.println(n+","+(u-k+1)+","+sum);
            }
            if (mostRecentIndex==-1){
                int[] d=M(n,k);
                mostRecentMax=d[0];
                mostRecentIndex=d[1];
            }else{
                if (mostRecentIndex<n){
                    System.out.println("Recalculatign lol");
                    int[] d=M(n,k);
                    mostRecentMax=d[0];
                    mostRecentIndex=d[1];
                }else{
                    if (mostRecentMax<=numDivv(n+k-1)){
                        mostRecentMax=numDivv(n+k-1);
                        mostRecentIndex=n+k-1;
                    }
                }
            }
            sum+=mostRecentMax;
        }
        return sum;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        long t=System.currentTimeMillis();
        for (int i=0; i<numDiv.length; i++){
            numDiv[i]=-1;
        }
        System.out.println(System.currentTimeMillis()-t);
        System.out.println(S(100000000,100000));
        //System.out.println(S(1000,10));
    }
}
