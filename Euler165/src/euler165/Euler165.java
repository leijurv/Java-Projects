/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler165;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Euler165 {
    //static final Object lock=new Object();
    static double[] dupX=new double[1000*8000];
    static double[] dupY=new double[1000*8000];
    static int pos=0;
    static long[][] seg=new long[5000][4];
    /*
     static boolean done1=false;
     static boolean done2=false;*/
    public static void print(long[] f){
        for (long i : f){
            System.out.print(i+",");
        }
        System.out.println();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException{
        long[] S=new long[20001];
        S[0]=290797;
        for (int i=1; i<S.length; i++){
            S[i]=(S[i-1]*S[i-1])%50515093;
        }
        for (int i=0; i<5000; i++){
            int n=i*4;
            seg[i]=new long[]{S[n+1]%500,S[n+2]%500,S[n+3]%500,S[n+4]%500};
        }
        print(seg[1]);
        //ArrayList<double[]> s=new ArrayList<double[]>();
        //ArrayList<double[]> dup=new ArrayList<double[]>();
        //s.ensureCapacity(5000000);
        //dup.ensureCapacity(5000000);
        /*
         new Thread(){
         public void run(){
         for (int t1i=0; t1i<2500; t1i++){
         System.out.println(t1i+","+pos);
         calc(t1i);
         }
         done1=true;
         }
         }.start();
         new Thread(){
         public void run(){
         for (int t1i=2500; t1i<5000; t1i++){
         System.out.println(t1i+","+pos);
         calc(t1i);
         }
         done2=true;
         }
         }.start();
        
         while(!done1 || !done2){
         Thread.sleep(5000);
         System.out.println("Waiting... "+done1+","+done2);
         }*/
        int x=1000*5000;
        System.out.println(x);
        for (int i=0; i<5000; i++){
            System.out.println(i+","+pos);
            calc(i);
        }
        done();
    }
    public static void done(){
        System.out.print("{");
        for (int i=0; i<pos; i++){
            System.out.print("{"+dupX[i]+","+dupY[i]+"},");
        }
        System.out.println("}");
        /*
        int tot=0;
        System.out.print("{");
        for (int i=0; i<pos; i++){
            if (du[i]){
                System.out.print("{"+dup[i][0]+","+dup[i][1]+"},");
                tot++;
            }
        }*/
        System.out.println("}");
        System.out.println(pos);
        //System.out.println(tot);
    }
    public static void calc(int i){
        for (int j=0; j<5000; j++){
            long[] seg1=seg[i];
            long[] seg2=seg[j];
            double[] d=find(seg1[0],seg1[1],seg1[2],seg1[3],seg2[0],seg2[1],seg2[2],seg2[3]);
            if (d!=null){
                //du[pos]=cont(dup,pos,d);
                if(cont(pos,d)){
                    dupX[pos]=d[0];
                    dupY[pos++]=d[1];
                }
            }
        }
    }
    public static boolean cont(int pos,double[] b){
        for (int i=0; i<pos; i++){
            if (dupX[i]==b[0]&&dupY[i]==b[1]){
                return false;
            }
        }
        return true;
    }
    public static double[] find(double x1,double y1,double x2,double y2,double x3,double y3,double x4,double y4){
        try{
            if (x1<x3&&x2<x3&&x1<x4&&x2<x4){
                return null;
            }
            if (x1>x3&&x2>x3&&x1>x4&&x2>x4){
                return null;
            }
            if (y1<y3&&y2<y3&&y1<y4&&y2<y4){
                return null;
            }
            if (y1>y3&&y2>y3&&y1>y4&&y2>y4){
                return null;
            }
            double a=-((-x2*x3*y1+x2*x4*y1+x1*x3*y2-x1*x4*y2+x1*x4*y3-x2*x4*y3-x1*x3*y4+x2*x3*y4)/(x3*y1-x4*y1-x3*y2+x4*y2-x1*y3+x2*y3+x1*y4-x2*y4));
            if ((x1<a&&a<x2)||(x2<a&&a<x1)){
                double b=-((x2*y1*y3-x4*y1*y3-x1*y2*y3+x4*y2*y3-x2*y1*y4+x3*y1*y4+x1*y2*y4-x3*y2*y4)/(-x3*y1+x4*y1+x3*y2-x4*y2+x1*y3-x2*y3-x1*y4+x2*y4));
                if ((y3<b&&b<y4)||(y4<b&&b<y3)){
                    return new double[]{a,b};
                }
            }
        } catch (Exception e){
            return null;
        }
        return null;
    }
}
