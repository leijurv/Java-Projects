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
public class ThreadSave {
    String[] saved;
    public ThreadSave(Cracker c){
        
        BigInteger Start=c.start;
                BigInteger End=c.end;
                BigInteger Progress=c.index;
                BigInteger[] Saved=c.alr;
                String[] a=new String[Saved.length+4];
                a[0]=Start.toString(16);
                a[1]=End.toString(16);
                a[2]=Progress.toString(16);
                a[3]=c.rainbow?c.test.toString(16):"-1";
                
                for (int n=0; n<Saved.length; n++){
                    //System.out.println(saved[n]);
                    a[n+4]=Saved[n].toString(10);
                }
                saved=a;
    }
}
