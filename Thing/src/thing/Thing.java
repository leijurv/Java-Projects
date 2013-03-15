/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thing;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Thing {

    /**
     * @param args the command line arguments
     */
    public static boolean prime(int i){
        if (i==2 || i==3){
            return true;
        }
        if (i%2==0)
            return false;
       
        for (int n=3; n<=Math.sqrt(i); n+=2){
            if (i%n==0){
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        ArrayList<Integer> row=new ArrayList<Integer>();
        int x=2;
        while(row.size()<100000){
            if (prime(x)){
                row.add(x);
            }
            x++;
        }
        System.out.println(row.get(row.size()-1));
        while(row.size()>1){
            ArrayList<Integer> N=new ArrayList<Integer>();
            for (int i=0; i<row.size()-1; i++){
                N.add(Math.abs(row.get(i)-row.get(i+1)));
            }
            
            row=N;
            int r=row.size();
            if (r%100==0){
                System.out.println(r);
                
            }
        }
        // TODO code application logic here
    }
}
