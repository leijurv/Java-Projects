/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lurfsort;

/**
 *
 * @author leijurv
 */
public class Lurfsort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[] a={6,5,2,3,6,8,8,5,3,2,6};
        sort(a,0);
        for (int x : a){
            System.out.println(x);
        }
    }
    public static void sort(int[] a, int pos){
        
        boolean done=true;
        while(done){
            int x=pos+1;
            boolean swap1=false;
            while(x<a.length){
                if (a[x]<a[pos]){
                    swap1=true;
                    int swap=a[x];
                    a[x]=a[pos];
                    a[pos]=swap;
                }
                x++;
            }
            if (!swap1){
                done=false;
            }
        }
        if (pos==a.length-1){
            return;
        }
        sort(a,pos+1);
        
    }
}
