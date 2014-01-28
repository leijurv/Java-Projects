/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class Transaction {
    ArrayList<BigInteger> To;
    ArrayList<BigInteger> Value;
    public Transaction(ArrayList<BigInteger> to, ArrayList<BigInteger> value){
        To=to;
        Value=value;
        
        BigInteger totalOutValue=BigInteger.ZERO;
        for (BigInteger b : value){
            totalOutValue=totalOutValue.add(b);
        }
        System.out.println("To: "+to);
        System.out.println("Value: "+value);
        System.out.println("Total output (not including change) value: "+totalOutValue);
        
        
        //Fix this so it can need multiple inputs
        BigInteger dist=new BigInteger("-1");
        int index=-1;
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            BigInteger D=AIB_Client.addresses.get(i).value.subtract(totalOutValue);
            if (D.compareTo(BigInteger.ZERO)!=-1){
                if (D.compareTo(dist)==1){
                    dist=D;
                    index=i;
                }
            }
        }
        System.out.println("Using address #"+index+", which has value "+AIB_Client.addresses.get(index).value);
        int[] inputind={index}; 
        
        
        byte[] input=new byte[1+(128+10)*inputind.length];
        input[0]=(byte) inputind.length;//FIX DIS, OVER 128 SHOULD RESOLVE TO NEG FOR BETTER COMPRESSION
        for (int i=0; i<128; i++){
            input[i+1]=AIB_Client.addresses.get(index).address.modulus.toByteArray()[i];
        }
        byte[] stuff=toTen(totalOutValue);
        for (int i=0; i<10; i++){
            input[i+129]=stuff[i];
        }
        
        byte[] output=new byte[1+(128+10)*to.size()];
        output[0]=(byte)to.size();
        //I'm writing this now
        
        
    }
    public static byte[] toTen(BigInteger x){
        byte[] X=new byte[10];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[10-xx.length+i]=xx[i];
        }
        return X;
    }
}
