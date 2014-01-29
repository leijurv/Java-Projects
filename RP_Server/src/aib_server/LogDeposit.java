/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class LogDeposit extends LogEvent{
    BigInteger to;
    BigInteger amount;
    byte[] B;
    public LogDeposit(FileInputStream f){
        try {
            byte[] b=new byte[138];
            f.read(b);
            process(b);
        } catch (IOException ex) {
            Logger.getLogger(LogDeposit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public LogDeposit(byte[] b){
        process(b);
    }
    public void process(byte[] b){
        B=b;
        byte[] first=new byte[128];
        byte[] second=new byte[10];
        for (int i=0; i<128; i++){
            first[i]=b[i];
        }
        for (int i=0; i<10; i++){
            second[i]=b[i+128];
        }
        to=new BigInteger(first);
        amount=new BigInteger(second);
        System.out.println("Loaded LogDeposit to address "+AIB_Server.snip(to)+" amount "+amount);
        for (Address a : AIB_Server.addresses){
            if (a.address.compareTo(to)==0){
                a.value=a.value.add(amount);
                return;
            }
        }
        System.out.println("Unable to complete LogDeposit");
    }
    @Override
    public String toString() {
        return "";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date time() {
        return null;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(FileOutputStream f) throws Exception {
        f.write(new byte[] {1});
        f.write(B);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
