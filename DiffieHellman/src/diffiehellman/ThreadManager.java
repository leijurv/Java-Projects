/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diffiehellman;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author leif
 */
public class ThreadManager {
    Cracker[] crackers;
    int threads;
    BigInteger start;
    BigInteger end;
    BigInteger[][] required;
    BigInteger blockSize=(new BigInteger("2")).pow(2);
    boolean rainbow;
    BigInteger A=new BigInteger("0");
    ThreadSave[] ts=new ThreadSave[0];
    public ThreadManager(int Threads, BigInteger Start, BigInteger End,boolean Rainbow){
        threads=Threads;
        crackers=new Cracker[threads];
        start=Start;
        end=End;
        rainbow=Rainbow;
        genBlocks();
        genCrackers();
    }
    public ThreadManager(int Threads, boolean Rainbow){
        this(Threads,new BigInteger("0"),Cracker.P.subtract(new BigInteger("1")),Rainbow);
    }
    public void genCrackers(){
        for (int i=0; i<threads; i++){
            BigInteger[] job=popReq();
            crackers[i]=new Cracker(job[0],job[1],rainbow,this);
            if (rainbow){
                crackers[i].test=A;
            }
        }
    }
    public static class Updater extends Thread implements Runnable{
        ThreadManager t;
        public Updater(ThreadManager tm){
            t=tm;
        }
        public void run(){
            t.update();
        }
    }
    public void startUpdateThread(){
        (new Updater(this)).start();
    }
    public void genBlocks(){
        BigInteger tjobs=end.subtract(start).divide(blockSize);
        BigInteger jobs=tjobs.multiply(blockSize).add(start).compareTo(end)==0?tjobs:tjobs.add(BigInteger.ONE);
        required=new BigInteger[jobs.intValue()][2];
        required[0][0]=start;
        required[0][1]=required[0][0].add(blockSize);
        for (int i=1; i<required.length-1; i++){
            required[i][0]=required[i-1][1];
            required[i][1]=blockSize.add(required[i][0]);
        }
        required[required.length-1][0]=required[required.length-2][1];
        required[required.length-1][1]=end;
    }
    public void startAllThreads(){
        for (int i=0; i<crackers.length; i++){
            crackers[i].start();
        }
    }
    public BigInteger[] popReq(){
        synchronized (required){
            BigInteger[] job=required[0];
            BigInteger[][] replace=new BigInteger[required.length-1][2];
            for (int i=0x1; i<required.length; i++){
                replace[i-1]=required[i];
            }
            required=replace;
            return job;
        }
    }
    
    public static void writefile(String filename, String[] thing){
        try {
            FileOutputStream a=new FileOutputStream(filename);
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            
            c.write("\n");
            c.write("\n");
            c.write("\n");
            for (int i=0; i<thing.length; i++){
                c.write(thing[i]);
                System.out.println(thing[i]);
                c.write("\n");
            }
            c.close();
        } catch (IOException ex) {
            
        } 
    }
    protected synchronized void removeCracker(int index){
        Cracker[] c=new Cracker[crackers.length-1];
        System.arraycopy(crackers, 0, c, 0, index);
        for (int i=index+1; i<crackers.length; i++){
            c[i-1]=crackers[i];
        }
        crackers=c;
    }
    public synchronized void add(ThreadSave s){
        ThreadSave[] t=new ThreadSave[ts.length+1];
        System.arraycopy(ts, 0, t, 0, ts.length);
        t[ts.length]=s;
        ts=t;
    }
    public void removeThread(int index){
        
    }
    public synchronized void update(){
        for (int i=0; i<threads; i++){
            if (crackers[i].ans.compareTo(A)==0){
                //SOLUTION FOUND
                System.out.println(crackers[i].ans);
                
            }
            if (!crackers[i].running && !crackers[i].saved){
                //System.out.println(i);
                ThreadSave t=new ThreadSave(crackers[i]);
                add(t);
                String[] a=t.saved;
                
                writefile("Thread "+i,a);
                if (required.length>0 && threads>=crackers.length){
                    BigInteger[] job=popReq();
                    crackers[i]=new Cracker(job[0],job[1],rainbow,this);
                    if (rainbow){
                        crackers[i].test=A;
                    }
                    crackers[i].start();
                }
                if (threads>=crackers.length){
                    removeCracker(i);
                }
                
            }
            
        }
    }
}
