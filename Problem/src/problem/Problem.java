/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Problem {
    public static boolean prime(int n){
        if (n==2||n==3||n==5){
            return true;
        }
        if (n==1||n==4){
            return false;
        }
        for (int i=2; i<=Math.sqrt(n); i++){
            if (n%i==0){
                return false;
            }
        }
        return true;
    }
    public static boolean fic(int i, int num){
        switch(i){
            case 0:
                return num%2==0;
            case 1:
                return 60%num==0;
            case 2:
                return num>12;
            case 3:
                return num%2==1;
            case 4:
                return 126%num==0;
            case 5:
                return prime(num);
            case 6:
                return num<13;
            case 7: 
                return (Math.sqrt(num)-Math.floor(Math.sqrt(num)))<0.001;
            case 8:
                return num%3==0;
            case 9:
                return num%5==0;
                
        }
        return false;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[] nums={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,20,21,24,25,27,30,60};
        ArrayList<Integer> I=new ArrayList<Integer>();
        for (int i : nums){
            I.add(i);
        }
        System.out.println(I);
        int[] used={1,4,9,16,25};
        for (int i :used){
            I.remove((Object)new Integer(i));
        }
        System.out.println(I);
        for (int i=0; i<9; i++){
            System.out.print(i+":");
            for (int j=0; j<I.size(); j++){
                if (fic(i,I.get(j))){
                    System.out.print(I.get(j)+",");
                }
            }
            System.out.println();
        }
        // TODO code application logic here
    }
}
