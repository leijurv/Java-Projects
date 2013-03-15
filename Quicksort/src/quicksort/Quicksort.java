/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package quicksort;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Quicksort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {//2
        Random r=new Random();
        int[] a=new int[100];
        for (int i=0; i<a.length; i++){
            a[i]=r.nextInt(100);
        }
        long b=System.currentTimeMillis();
        System.out.println(b);
        sort(a);
        long c=System.currentTimeMillis();
        System.out.println(c);
        System.out.println(c-b);
        System.out.println("q");
        for (int i=0; i<a.length; i++){
            System.out.println(a[i]);
        }
    }

	public static void sort(int[] values) {
		if (values ==null || values.length==0){
			return;
		}
		quicksort(values, 0, values.length - 1);
	}

	private static void quicksort(int[] numbers, int low, int high) {
            int number=numbers.length;
		int i = low, j = high;
		int pivot = numbers[low + (high-low)/2];
		while (i <= j) {
			while (numbers[i] < pivot) {
				i++;
			}
			while (numbers[j] > pivot) {
				j--;
			}
			if (i <= j) {
				exchange(numbers, i, j);
				i++;
				j--;
			}
		}
		if (low < j) {
                quicksort(numbers, low, j);
            }
		if (i < high) {
                quicksort(numbers, i, high);
            }
	}

	private static void exchange(int[] numbers, int i, int j) {
		int temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;
	}
}
