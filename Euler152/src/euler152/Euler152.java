/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler152;
/**
 *
 * @author leijurv
 */
public class Euler152 {
    static long[] S={2,3,4,5,6,7,8,9,10,12,12,14,15,16,18,20,21,24,25,27,
        28,30,32,35,36,40,42,45,48,49,50,54,56,60,63,64,70,
        72,75,80};
    static long lcm=Long.parseLong("4480842240000");
    static long[] inverses=new long[S.length];
    static long[] residuals=new long[S.length];
    public static int[][] find(long rem,int si,int[] f,int flen){
        if (rem==0){
            System.out.print("SOL");
            int[] res=new int[flen];
            for (int i=0; i<res.length; i++){
                res[i]=f[i];
            }
            print(res);
            return new int[][]{res};
        }
        int[][] res={};
        for (int j=si; j<inverses.length; j++){
            /*
            if (j<10){
                int[] ress=new int[flen];
                for (int i=0; i<ress.length; i++){
                    ress[i]=f[i];
                }
                print(ress);
            }*/
            long nr=rem-inverses[j];
            if (nr>=0){
                f[flen]=j;
                int[][] t=find(nr,j+1,f,flen+1);
                if (t.length!=0){
                    int[][] res1=new int[res.length+t.length][];
                    for (int i=0; i<res.length; i++){
                        res1[i]=res[i];
                    }
                    for (int i=0; i<t.length; i++){
                        res1[i+res.length]=t[i];
                    }
                    res=res1;
                }
            }
            if (rem>residuals[j]){
                break;
            }
        }
        return res;
    }
    public static void print(int[] f){
        for (int i : f){
            System.out.print(i+",");
        }
        System.out.println();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        for (int i=0; i<S.length; i++){
            inverses[i]=lcm/(S[i]*S[i]);
            System.out.println(inverses[i]);
        }
        for (int i=0; i<S.length; i++){
            long s=0;
            for (int j=i; j<S.length; j++){
                s+=inverses[j];
            }
            residuals[i]=s;
            //System.out.println(s);
        }
        System.out.println(residuals[2]);
        System.out.print(lcm/4);
        if (true){
            //return;
        }
        int[] f=new int[S.length];
        int[][] a=find(lcm/2,0,f,0);
        for (int i=0; i<a.length; i++){
            for (int n=0; n<a[i].length; n++){
                System.out.print(S[a[i][n]]+",");
            }
            System.out.println();
        }
        System.out.println(a.length);
        //System.out.println(Long.MAX_VALUE);
        // TODO code application logic here
    }
}
