/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularsquareroot;

import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class ModularSquareRoot {
public static int[] S(int mod, int val){
    int[] result=new int[2];
    double c=Math.sqrt(val);
        while(Math.abs(((double)Math.round(c))-c)>0.01 && c<mod*2){
            c=Math.sqrt(c*c+mod);
        }
        if (c>=mod*2 || Math.round(c%mod)==0){
            return new int[0];
        }
        result[0]=(int)c;
        c=Math.sqrt(c*c+mod);
        while(Math.abs(((double)Math.round(c))-c)>0.01 && c<mod*2){
            c=Math.sqrt(c*c+mod);
        }
        result[1]=(int)c;
        return result;
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int mod=17;
        for (int i=1; i<mod; i++){
            System.out.println(i+":"+S(mod,i).length);
        }
        // TODO code application logic here
    }
}
