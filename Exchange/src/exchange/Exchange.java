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
public class Exchange{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        Currency btc=new Currency(9, "BTC");
        Currency usd=new Currency(2, "USD");
        Market m=new Market(usd, btc);
        Order o=new Order(40000, 4000, m, true);
        System.out.println(o.othAmt+","+btc.toString(o.othAmt));
        Order oo=new Order(40000, 100000000, m, false);
        System.out.println(usd.toString(oo.baseAmt));
    }
}
