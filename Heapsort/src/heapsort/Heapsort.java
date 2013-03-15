package heapsort;

import java.util.Random;

public class Heapsort {

    public static void main(String ar[]) {
        int[] toSort={6,4,6,8,3,6,7,5,4,3,6,7,8};
        heapSort(toSort);
        for (int i : toSort){
            System.out.println(i);
        }
    }
    public static void heapSort(int[] a){
        int end=a.length-1;
        while(end>0){
            swap(a,end,0);
            end--;
            siftDown(a,0,end);
        }
    }
    
    public static void heapify(int[] a){
        int start=(a.length-2)/2;
        while(start>=0){
            siftDown(a,start,a.length-1);
            start--;
        }
    }
    
    public static void siftDown(int[] a, int start,int end){
        int root=start;
        while(root*2+1<=end){
            int child=root*2+1;
            int swap=root;
            if (a[swap]<a[child]){
                swap=child;
            }
            if (child+1<=end && a[swap]<a[child+1]){
                swap=child+1;
            }
            if (swap!=root){
                swap(a,root,swap);
                root=swap;
            }else{
                return;
            }
            
        }
        
    }
public static void swap(int[] a, int b, int c){
    int x=a[b];
    a[b]=a[c];
    a[c]=x;
}
    
}