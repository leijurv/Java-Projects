/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mergesort;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class MergeSort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[] x={6,5,6,3,4,3,5,7,8,9,9,6,3,2,2,4,6,1};
        int[] y=merge_sort(x);
        for (int n : y){
            System.out.println(n);
        }
    }
    public static int[] merge_sort(int[] m){
        if (m.length<=1){
            return m;
        }
        int middle=m.length/2;
        int[] left=new int[middle];
        int[] right=new int[m.length-middle];
        for (int i=0; i<middle; i++){
            left[i]=m[i];
        }
        for (int i=middle; i<m.length; i++){
            right[i-middle]=m[i];
        }
        left=merge_sort(left);
        right=merge_sort(right);
        int[] result=new int[m.length];
        int n=0;
        for (int i=0; i<left.length; i++){
            result[n]=left[i];
            n++;
        }
        for (int i=0; i<right.length; i++){
            result[n]=right[i];
            n++;
        }
        return result;
    }
}
