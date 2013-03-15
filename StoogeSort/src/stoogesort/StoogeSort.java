/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stoogesort;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class StoogeSort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {//18
       Random r=new Random();
        int[] a=new int[100];
        for (int i=0; i<a.length; i++){
            a[i]=r.nextInt(100);
        }
        long b=System.currentTimeMillis();
        System.out.println(b);
        stoogesort(a,0,a.length-1);
        long c=System.currentTimeMillis();
        System.out.println(c);
        System.out.println(c-b);
        System.out.println("q");
        for (int i=0; i<a.length; i++){
            System.out.println(a[i]);
        }
    }
    public static void stoogesort(int[] l, int i, int j){
        if (l[j]<l[i]){
            int a=l[j];
            l[j]=l[i];
            l[i]=a;
        }
        if ((j-i+1)>=3){
            int t=(j-i+1)/3;
            stoogesort(l,i,j-t);
            stoogesort(l,i+t,j);
            stoogesort(l,i,j-t);
        }
    }
}
