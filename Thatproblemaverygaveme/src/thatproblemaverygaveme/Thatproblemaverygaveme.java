/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thatproblemaverygaveme;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Thatproblemaverygaveme {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random r=new Random();
        int t=0;
        int times=0;
        for (int i=0; i<100000000; i++){
            double a=r.nextDouble()/2;//Assume the first break is before 1/2 without loss of generality
            //double b=a+r.nextDouble()*(1-a);//Assume the second break is after the first one without loss of generality
            //double a=r.nextDouble();
            double b=r.nextDouble();
            double second=b-a;
            double third=1-b;
            if (a<=second+third && second<=a+third && third<=a+second){
                t++;
            }
            times++;
        }
        System.out.println((double)t/(double)times);
        double a=0.2;
        double bmin=1;
        double bmax=0;
        for (double b=a+0.00001; b<1; b+=0.00001){
            double second=b-a;
            double third=1-b;
            if (a<=second+third && second<=a+third && third<=a+second){
                if (b<bmin){
                    bmin=b;
                }
                if (bmax<b){
                    bmax=b;
                }
            }
        }
        System.out.println(bmin+":"+bmax+":"+(bmax-bmin));
        // TODO code application logic here
    }
}
