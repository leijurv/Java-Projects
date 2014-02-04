/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aib_server;

import static aib_server.AIB_Server.addresses;
import static aib_server.AIB_Server.hash;
import static aib_server.AIB_Server.slice;
import static aib_server.AIB_Server.verify;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class LogTransaction extends LogEvent{
    BigInteger[] outAmt;
    BigInteger[] inAmt;
    byte[] hash;
    byte[] total;
    boolean Valid;
    long time;
    public LogTransaction(FileInputStream f){
        try {
            byte[] Inputs=new byte[1];
            f.read(Inputs);
            byte[] In=new byte[Inputs[0]*138];
            f.read(In);
            byte[] Outputs=new byte[1];
            f.read(Outputs);
            byte[] Out=new byte[Outputs[0]*138];
            f.read(Out);
            byte[] Sig=new byte[Inputs[0]*128];
            f.read(Sig);
            byte[] Time=new byte[8];
            f.read(Time);
            time=new BigInteger(Time).longValue();
            byte[] Toats=add(Inputs,add(In,add(Outputs,add(Out,Sig))));
            process(Hex.encodeHexString(Toats));
        } catch (IOException ex) {
            Logger.getLogger(LogTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public LogTransaction(String s){
        time=System.currentTimeMillis();
        process(s);
    }
    public void process(String s){
        System.out.println("Processing SendTX");
        if (s.length()!=0){
            total=Hex.decodeHex(s.toCharArray());
            int inputs=total[0];
            hash=hash(total);
            System.out.println(inputs+" inputs");
            System.out.println("Query length "+total.length);
            byte[] sigs=new byte[inputs*128];
            byte[] toats=new byte[total.length-sigs.length];
            for (int i=0; i<toats.length; i++){
                toats[i]=total[i];
            }
            for (int i=0; i<128*inputs; i++){
                sigs[i]=total[i+toats.length];
            }
            
            
            byte[][] input=new byte[inputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<inputs; j++){
                    byte r=total[1+i+j*138];
                    //System.out.println(j+","+i);
                    input[j][i]=r;
                }
            }
            byte[][] inAmts=new byte[inputs][10];
            for (int i=0; i<10; i++){
                for (int j=0; j<inputs; j++){
                    inAmts[j][i]=total[129+i+j*138];
                }
            }
            inAmt=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                inAmt[i]=new BigInteger(inAmts[i]);
            }
            BigInteger toatInput=BigInteger.ZERO;
            for (int i=0; i<inputs; i++){
                toatInput=toatInput.add(inAmt[i]);
            }
            for (int i=0; i<inputs; i++){
                System.out.println("Input from "+snip(new BigInteger(input[i]))+" with value "+inAmt[i]);
            }
            System.out.println("Total input value "+toatInput);
            
            
            byte[] out=slice(total,138*inputs+1);
            int outputs=out[0];
            System.out.println(outputs+" outputs");
            byte[][] output=new byte[outputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<outputs; j++){
                    byte r=out[1+i+j*138];
                    //System.out.println(j+","+i);
                    output[j][i]=r;
                }
            }
            byte[][] outAmts=new byte[outputs][10];
            for (int i=0; i<10; i++){
                for (int j=0; j<outputs; j++){
                    outAmts[j][i]=out[129+i+j*138];
                }
            }
            outAmt=new BigInteger[outputs];
            for (int i=0; i<outputs; i++){
                outAmt[i]=new BigInteger(outAmts[i]);
            }
            for (int i=0; i<outputs; i++){
                System.out.println("Output to "+snip(new BigInteger(output[i]))+" with value "+outAmt[i]);
            }
            BigInteger toatOutput=BigInteger.ZERO;
            for (int i=0; i<outputs; i++){
                toatOutput=toatOutput.add(outAmt[i]);
            }
            System.out.println("Total output value "+toatOutput);
            
            if (toatOutput.compareTo(toatInput)==0){
                System.out.println("Total input equal to total output YAY");
            }else{
                System.out.println("Input value unequal to output value!! NO");
            }
            
            System.out.println("Verifying inputs");
            byte[][] sig=new byte[inputs][128];
            for (int i=0; i<128; i++){
                for (int j=0; j<inputs; j++){
                    sig[j][i]=total[toats.length+i+j*128];
                }
            }
            byte[] hash=hash(toats);
            System.out.println("Hash: "+Hex.encodeHexString(hash));
            boolean valid=true;
            for (int i=0; i<inputs; i++){
            try {
                System.out.println("Sig "+i+" has sig "+new BigInteger(sigs).toString(16));
                if (!verify(toats,sig[i],new BigInteger(input[i]))){
                    System.out.println("Invalid signature for input #"+i);
                    valid=false;
                    break;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AIB_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            if (!valid){
                return;
            }
            BigInteger[] inputAddr=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                inputAddr[i]=new BigInteger(input[i]);
            }
            BigInteger[] outputAddr=new BigInteger[outputs];
            for (int i=0; i<outputs; i++){
                outputAddr[i]=new BigInteger(output[i]);
            }
            Address[] inputA=new Address[inputs];
            Address[] outputA=new Address[outputs];
            for (Address a : addresses){
                for (int i=0; i<inputs; i++){
                    if (a.address.compareTo(inputAddr[i])==0){
                        inputA[i]=a;
                    }
                }
                for (int i=0; i<outputs; i++){
                    if (a.address.compareTo(outputAddr[i])==0){
                        outputA[i]=a;
                    }
                }
            }
            BigInteger[] inputAddrValues=new BigInteger[inputs];
            for (int i=0; i<inputs; i++){
                if (inputA[i]!=null){
                    System.out.println("Input "+i+" matched to address "+snip(inputA[i].address)+" with value "+inputA[i].value);
                    inputAddrValues[i]=inputA[i].value;
                }else{
                    System.out.println("Unable to match input "+i+" with address "+snip(inputAddr[i])+", transaction will probably fail");
                    inputAddrValues[i]=BigInteger.ZERO;
                }
                if (inAmt[i].compareTo(inputAddrValues[i])==1){
                    System.out.println("Not enough in address "+snip(inputA[i].address)+": "+inputAddrValues[i]+", needed at least"+inAmt[i]);
                    return;
                }else{
                    
                }
            }
            for (int i=0; i<inputs; i++){
            if (inputA[i]!=null){
                        System.out.print("Subtracting "+inAmt[i]+" from address "+snip(inputA[i].address)+", bringing balance to ");
                        inputA[i].value=inputA[i].value.subtract(inAmt[i]);
                        System.out.println(inputA[i].value);
                    }
        }
            for (int i=0; i<outputs; i++){
                if (outputA[i]==null){
                    System.out.println("Unable to match output "+i+" with address "+snip(outputAddr[i])+", creating new Address");
                    addresses.add(new Address(outputAddr[i],BigInteger.ZERO,new BigInteger(239,new SecureRandom())));
                    outputA[i]=addresses.get(addresses.size()-1);
                }
                if (outputA[i]!=null){
                    System.out.println("Output "+i+" matched to address "+snip(outputA[i].address)+" with value "+outputA[i].value);
                    System.out.println("Adding value "+outAmt[i]);
                    outputA[i].value=outputA[i].value.add(outAmt[i]);
                }
            }
        }
        try {
            AIB_Server.saveAddresses();
        } catch (Exception ex) {
            Logger.getLogger(LogTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }
    public String toString(){
        return "";
    }
    public long time(){
        return time;
    }
    @Override
    public void write(FileOutputStream f) throws Exception{
        f.write(new byte[] {0});//0 signifies LogTransaction
        f.write(total);
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
    public static byte[] add(byte[] a, byte[] b){
        byte[] r=new byte[a.length+b.length];
        for (int i=0; i<a.length; i++){
            r[i]=a[i];
        }
        for (int i=0; i<b.length; i++){
            r[i+a.length]=b[i];
        }
        return r;
    }
    public static String snip(BigInteger x){
        return (x.toString(16).substring(0,8)+"..."+x.toString(16).substring(x.toString(16).length()-8,x.toString(16).length()));
    }
}
