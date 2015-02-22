/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler493;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Euler493 {
    public static ArrayList<Integer> array(){
        ArrayList<Integer> res=new ArrayList<Integer>(70);
        for(int i=0; i<70; i++){
            res.add(i/10);
        }
        return res;
    }
    public static ArrayList<Integer> array(int[] amts){
        ArrayList<Integer> res=new ArrayList<Integer>(70);
        for(int i=0; i<7; i++){
            int amt=amts[i];
            for(int j=0; j<amt; j++){
                res.add(i);
            }
        }
        return res;
    }
    public static long select(ArrayList<Integer> a, Random r, double b){
        //System.out.println(a+","+b);
        boolean[] n=new boolean[7];
        int x=a.size();
        int amt=20-(70-x);
        for(int i=0; i<amt; i++){
            int p=r.nextInt(x-i);
            n[a.remove(p)]=true;
        }
        long nonzero=0;
        for(int i=0; i<7; i++){
            if(n[i]){
                nonzero++;
            }
        }
        return nonzero;
    }
    public static void select(int[] amts, Random r, double modifier){
        int Tot=0;
        for(int i=0; i<7; i++){
            Tot+=amts[i];
        }
        if(Tot<70){
            double amt=(double)select(array(amts),r,modifier);
            tot+=amt*modifier;
            modTotal+=modifier;
            return;
        }
        for(int i=0; i<7; i++){
            if(amts[i]!=0){
                double mod=modifier*((double)amts[i])/((double)Tot);
                amts[i]--;
                select(amts,r,mod);
                amts[i]++;
            }
        }
    }
    static double tot=0;
    static double modTotal=0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        new Thread(){
            public void run(){
                while(true)
                select(new int[]{10,10,10,10,10,10,10},new Random(),1);
                
            }
        }.start();
        Thread.sleep(10);
        while(true){
                System.out.println(tot/modTotal+","+modTotal);
            Thread.sleep(100);
        }
    }
    
}