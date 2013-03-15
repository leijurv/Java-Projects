/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diffiehellman;

import java.math.BigInteger;

/**
 *
 * @author leif
 */
public class Cracker extends Thread implements Runnable{
    static BigInteger P;
    static BigInteger G;
    BigInteger test;
    boolean rainbow;
    BigInteger start;
    BigInteger index;
    BigInteger end;
    BigInteger[] alr;
    boolean running;
    boolean done;
    BigInteger ans;
    ThreadManager tm;
    boolean saved;
    public Cracker(BigInteger Start, BigInteger End, boolean Rainbow, ThreadManager T){
        start=Start;
        rainbow=Rainbow;
        end=End;
        alr=new BigInteger[0];
        running=true;
        done=false;
        ans=new BigInteger("-1");
        tm=T;
        saved=false;
        index=new BigInteger(start.toString());
    }
    @Override
    public void run(){
        BigInteger c=new BigInteger("-1");
        
        while ((rainbow || c.compareTo(test)!=0) && running && index.compareTo(end)!=0){
            
            done=false;
            synchronized (index){
                index=index.add(BigInteger.ONE);
            }
            c=G.modPow(index,P);
            System.out.print(index);
            System.out.print(rainbow);
            System.out.print(":");
            System.out.println(c);
            if (rainbow){
                add(c);
            }else{
                if (c.compareTo(test)==0){
                    ans=index;
                }
            }
            done=true;
        }
       
        running=false;
        tm.startUpdateThread();
    }
    public void halt(){
        running=false;
    }
    public void add(BigInteger a){
            BigInteger[] b=new BigInteger[alr.length+1];
            System.arraycopy(alr, 0, b, 0, alr.length);
            b[alr.length]=a;
            alr=b;
    }
}
