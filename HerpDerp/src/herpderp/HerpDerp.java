/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package herpderp;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author leijurv
 */
public class HerpDerp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MathContext dp=new MathContext(500);
        BigDecimal b=new BigDecimal("0.1231",dp);
        System.out.println(b);
        for (int i=0; i<200; i++){
        b=b.pow(2).subtract(new BigDecimal(2,dp),dp);
        System.out.println(b);
        }
        long l=9223372036854775807L;
        long q=1000000000000000000L;
                //0
        // TODO code application logic here
    }
}
