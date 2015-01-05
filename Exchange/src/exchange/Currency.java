/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

/**
 *
 * @author leijurv
 */
public class Currency{

    int decimals;
    String code;

    public Currency(int Decimals, String Code){
        decimals=Decimals;
        code=Code;
    }

    public String toString(long amt){
        return ""+((double) amt)/Math.pow(10, decimals);
    }
}
