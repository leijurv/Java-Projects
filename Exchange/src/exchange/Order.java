/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exchange;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Order{

    private static final Random randomIds=new Random();
    static final int feeFraction=500;
    final Object lock=new Object();
    final long fee;
    final long baseAmt;
    final Currency base;
    final long othAmt;
    final Currency other;
    final Market market;
    final long rate;//10^other.decimals oth is worth *rate* base
    final boolean isBuy;
    final long creationDate;
    ArrayList<Object[]> fufilled=new ArrayList<>();
    long othRemain;
    long baseRemain;
    long feeRemain;
    long cancellationDate;

    public Order(long Rate, long amtOffered, Market m, boolean IsBuy){
        isBuy=IsBuy;
        market=m;
        base=m.base;
        other=m.other;
        rate=Rate;
        if (isBuy){
            baseAmt=amtOffered;
            fee=baseAmt/feeFraction;
            othAmt=(long) (((double) (amtOffered-fee))/((double) rate)*Math.pow(10, other.decimals));
        }else{
            othAmt=amtOffered;
            long tbaseAmt=(long) (((double) (rate*amtOffered))/Math.pow(10, other.decimals));
            fee=tbaseAmt/feeFraction;
            baseAmt=tbaseAmt-fee;
        }
        creationDate=System.currentTimeMillis();
        feeRemain=fee;
        baseRemain=baseAmt;
        othRemain=othAmt;
        cancellationDate=-1;
    }

    public void cancel(){
        synchronized (lock){
            cancellationDate=System.currentTimeMillis();
        }
    }
    public void execute(Order r){
        
    }
}
