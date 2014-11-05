/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euler484;

/**
 *
 * @author leijurv
 */
public class Euler484 {
    
    public static int AD(int p){
        if (p%2==0){
            return p/2+AD(p/2)*2;
        }
        double d=Math.sqrt(p);
        for (int i=3; i<=d; i+=2){
            if (p%i==0){
                return p/i+AD(p/i)*i;
            }
        }
        return 1;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        System.out.println(AD(20));
    }
    
}
