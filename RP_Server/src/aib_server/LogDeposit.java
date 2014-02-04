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
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author leijurv
 */
public class LogDeposit extends LogEvent{
    BigInteger to;
    BigInteger amount;
    byte[] B;
    byte[] txHash;
    long time;
    public boolean verify(){
        try {
            System.out.println("Verifying LogDeposit to address "+AIB_Server.snip(to)+" amount "+amount+" txid "+Hex.encodeHexString(txHash)+" time "+time);
            String s=AIB_Server.load("http://blockchain.info/rawtx/"+Hex.encodeHexString(txHash));
            if (s.equals("Invalid Transaction Hash") || s.equals("Transaction not found")){
                return false;
            }
            JSONTokener R=new JSONTokener(s);
            JSONObject o=(JSONObject)R.nextValue();
            
        } catch (Exception ex) {
            return false;
        }
        return false;
    }
    public LogDeposit(FileInputStream f){
        try {
            byte[] b=new byte[170];
            f.read(b);
            byte[] Time=new byte[8];
            f.read(Time);
            time=new BigInteger(Time).longValue();
            process(b);
        } catch (IOException ex) {
            Logger.getLogger(LogDeposit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public LogDeposit(byte[] b){
        time=System.currentTimeMillis();
        process(b);
    }
    public void process(byte[] b){
        B=b;
        byte[] first=new byte[128];
        byte[] second=new byte[10];
        byte[] third=new byte[32];
        for (int i=0; i<128; i++){
            first[i]=b[i];
        }
        for (int i=0; i<10; i++){
            second[i]=b[i+128];
        }
        for (int i=0; i<32; i++){
            third[i]=b[i+138];
        }
        to=new BigInteger(first);
        amount=new BigInteger(second);
        txHash=third;
        System.out.println("Loaded LogDeposit to address "+AIB_Server.snip(to)+" amount "+amount+" txid "+Hex.encodeHexString(txHash)+" time "+time);
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

    public long time() {
        return time;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(FileOutputStream f) throws Exception {
        f.write(new byte[] {1});
        f.write(B);
        f.write(to8(new BigInteger(""+time)));
    }
    public static byte[] to8(BigInteger x){
        byte[] X=new byte[8];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[8-xx.length+i]=xx[i];
        }
        return X;
    }

    
}
