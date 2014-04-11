/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author leif
 */
public class Transaction {
    ArrayList<BigInteger> To;
    ArrayList<BigInteger> Value;
    public static ArrayList<Integer> get(BigInteger value, ArrayList<Integer> no){
        BigInteger dist=new BigInteger("-1");
        int index=-1;
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            BigInteger D=AIB_Client.addresses.get(i).value.subtract(value);
            
            if (D.compareTo(BigInteger.ZERO)!=-1 && !no.contains(i)){
                if (D.compareTo(dist)==1){
                    dist=D;
                    index=i;
                }
            }
        }
        if (index!=-1){
            ArrayList<Integer> x=new ArrayList<Integer>();
            x.add(index);
            return x;
        }
        int maxIndex=0;
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            if (AIB_Client.addresses.get(i).value.compareTo(AIB_Client.addresses.get(maxIndex).value)!=-1  && !no.contains(i)){
                maxIndex=i;
            }
        }
        no.add(maxIndex);
        ArrayList<Integer> r=get(value.subtract(AIB_Client.addresses.get(maxIndex).value),no);
        r.add(maxIndex);
        return r;
    }
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
        /*BigInteger dist=new BigInteger("-1");
        int index=-1;
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            BigInteger D=AIB_Client.addresses.get(i).value.subtract(totalOutValue);
            if (D.compareTo(BigInteger.ZERO)!=-1){
                if (D.compareTo(dist)==1){
                    dist=D;
                    index=i;
                }
            }
        }*/
        ArrayList<Integer> r=get(totalOutValue,new ArrayList<Integer>());
        System.out.println(r);
        //System.out.println("Using address #"+index+", which has value "+AIB_Client.addresses.get(index).value);
        int[] inputind=new int[r.size()];
        for (int i=0; i<inputind.length; i++){
            inputind[i]=r.get(i);
        }
        
        
        byte[] input=new byte[1];
        input[0]=(byte) inputind.length;//FIX DIS, OVER 128 SHOULD RESOLVE TO NEG FOR BETTER COMPRESSION
        for (int i=0; i<inputind.length; i++){
            input=add(input,to128(AIB_Client.addresses.get(inputind[i]).address.modulus));
            BigInteger vlaue=AIB_Client.addresses.get(inputind[i]).value;
            if (vlaue.compareTo(totalOutValue)==1){
                input=add(input,toTen(totalOutValue));
            }else{
                input=add(input,toTen(vlaue));
            }
            totalOutValue=totalOutValue.subtract(AIB_Client.addresses.get(inputind[i]).value);
        }
        
        byte[] output=new byte[1];
        output[0]=(byte)to.size();
        for (int i=0; i<to.size(); i++){
            output=add(output,to128(to.get(i)));
            output=add(output,toTen(value.get(i)));
        }
        
        byte[] toats=add(input,output);
        byte[] hash=hash(toats);
        System.out.println("Hash: "+Hex.encodeHexString(hash));
        System.out.println("YORUMOM "+(new BigInteger(hash).mod(AIB_Client.addresses.get(inputind[0]).address.modulus)).toString(16));
        byte[] sigs=new byte[0];
        for (int i=0; i<inputind.length; i++){
            sigs=add(sigs,to128(AIB_Client.addresses.get(inputind[i]).address.decode(new BigInteger(hash))));
            System.out.println(AIB_Client.addresses.get(inputind[i]).address.decode(new BigInteger(hash)).toString(16));
        }
        
        byte[] total=add(toats,sigs);
        String request=Hex.encodeHexString(total);
        System.out.println(request);
        try {
            String s= AIB_Client.load(AIB_Client.web+"sendtx/"+request);
            
        } catch (Exception ex) {
            JOptionPane.showInputDialog("Error while sending transaction to server",JOptionPane.ERROR_MESSAGE);
        }
        synchronized(AIB_Client.addresses){
        for (int i=0; i<AIB_Client.addresses.size(); i++){
            AIB_Client.addresses.set(i,new Address(AIB_Client.addresses.get(i).address));
        }
        }
    }
    public static byte[] hash(byte[] r){
        try {
            MessageDigest m=MessageDigest.getInstance("SHA1");
            m.reset();
            m.update(r);
            byte[] sig=m.digest();
            return sig;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("No Sha1");
        }
        return null;
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
    public static byte[] to128(BigInteger x){
        byte[] X=new byte[128];
        byte[] xx=x.toByteArray();
        for (int i=0; i<xx.length; i++){
            X[128-xx.length+i]=xx[i];
        }
        return X;
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
