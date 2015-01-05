/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Market extends Thread{
    Currency base;
    Currency other;
    final Object buyLock=new Object();
    ArrayList<Order> buys=new ArrayList<>();
    final Object sellLock=new Object();
    ArrayList<Order> sells=new ArrayList<>();
    public Market(Currency Base,Currency Other){
        base=Base;
        other=Other;
    }
    private void insert(Order o){
        if (o.isBuy){
            long rate=o.rate;
            boolean b=false;
            synchronized (buyLock){
                if (buys.isEmpty()||buys.get(0).rate<rate){
                    buys.add(0,o);
                    b=true;
                }
            }
            if (b){
            }
            synchronized (buyLock){
                if (buys.get(buys.size()-1).rate>rate){
                    buys.add(o);
                    return;
                }
                for (int i=1; i<buys.size(); i++){
                    if (buys.get(i-1).rate>rate&&buys.get(i).rate<rate){
                        buys.add(i,o);
                        return;
                    }
                }
            }
        }else{
            long rate=o.rate;
            boolean b=false;
            synchronized (sellLock){
                if (sells.isEmpty()||sells.get(0).rate>rate){
                    sells.add(0,o);
                    b=true;
                }
            }
            synchronized (sellLock){
                if (sells.get(sells.size()-1).rate<rate){
                    sells.add(o);
                    return;
                }
                for (int i=1; i<sells.size(); i++){
                    if (sells.get(i-1).rate<rate&&sells.get(i).rate>rate){
                        sells.add(i,o);
                        return;
                    }
                }
            }
        }
        //Should never happen
        throw new RuntimeException("You broke it");
    }
    public boolean check(){
        synchronized (sellLock){
            if (sells.isEmpty()){
                return false;
            }
            synchronized (buyLock){
                if (buys.isEmpty()){
                    return false;
                }
                return buys.get(0).rate>=sells.get(0).rate;
            }
        }
    }
    public void run(){
        while (true){
            while (check()){
            }
        }
    }
}
