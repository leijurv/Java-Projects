/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import cryptolib.Hex;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class DepositRequest {
    BigInteger totalValue;
    ArrayList<BigInteger> wValues;
    ArrayList<BigInteger> R1PrimeValues;
    ArrayList<BigInteger> R2PrimeValues;
    ArrayList<Integer> denominationIDs;
    public DepositRequest(ArrayList<BigInteger> RPrimeValues, ArrayList<Integer> denominations){
        R1PrimeValues=RPrimeValues;
        denominationIDs=denominations;
        if (RPrimeValues.size()!=denominations.size()){
            throw new IllegalArgumentException("Number of RPrimeValues must be same as number of denomation IDs");
        }
        calculateValue();
        wValues=new ArrayList<BigInteger>();
        SecureRandom R=new SecureRandom();
        for (int i=0; i<RPrimeValues.size(); i++){
            wValues.add(randomW(R,AIB_Client.denominationPubKeys.get(denominationIDs.get(i))));
        }
        calculateR2();
    }
    private void calculateValue(){
        totalValue=new BigInteger("0");
        for (int i=0; i<denominationIDs.size(); i++){
            totalValue=totalValue.add(BigInteger.TEN.pow(denominationIDs.get(i)));
        }
    }
    private void calculateR2(){
        R2PrimeValues=new ArrayList<BigInteger>();
        for (int i=0; i<R1PrimeValues.size(); i++){
            //System.out.println(AIB_Client.denominationPubKeys.get(denominationIDs.get(i)));
            R2PrimeValues.add(R1PrimeValues.get(i).multiply(wValues.get(i).modPow(AIB_Client.e,AIB_Client.denominationPubKeys.get(denominationIDs.get(i)))).mod(AIB_Client.denominationPubKeys.get(denominationIDs.get(i))));
        }
    }
    public DepositRequest(int len, byte[] X){
        if (X.length%257!=0){
            throw new IllegalArgumentException("BAD LENGTH");
        }
        denominationIDs=new ArrayList<Integer>();
        R1PrimeValues=new ArrayList<BigInteger>();
        wValues=new ArrayList<BigInteger>();
        for (int i=0; i<len; i++){
            denominationIDs.add(new Integer(X[i*257]));
            byte[] ROnPrime=new byte[128];
            for (int n=0; n<128; n++){
                ROnPrime[n]=X[n+i*257+1];
            }
            R1PrimeValues.add(decode(new BigInteger(ROnPrime)));
            byte[] W=new byte[128];
            for (int n=0; n<128; n++){
                W[n]=X[n+i*257+129];
            }
            wValues.add(decode(new BigInteger(W)));
        }
        calculateValue();
        calculateR2();
    }
    private static BigInteger randomW(SecureRandom r, BigInteger n){
        BigInteger w;
        do{
            w=new BigInteger(1000,r);
            System.out.println(w.gcd(n).compareTo(BigInteger.ONE)+","+w.compareTo(n));
        }while(w.gcd(n).compareTo(BigInteger.ONE)!=0 || w.compareTo(n)!=-1);
        return w;
    }
    public byte[] export(){
        String soFar="";
        byte[] s=new byte[257*wValues.size()+1];//1 byte for denomination ID, 128 for wValue and R1PrimeValue. R2PrimeValue is calculates on init.
        s[0]=new Integer(denominationIDs.size()).byteValue();
        for (int i=0; i<R2PrimeValues.size(); i++){
            s[257*i+1]=denominationIDs.get(i).byteValue();
            byte[] W=to128(encode(R1PrimeValues.get(i)).toByteArray());
            System.arraycopy(W, 0, s, 257*i+2, 128);
            byte[] w=to128(encode(wValues.get(i)).toByteArray());
            System.arraycopy(w, 0, s, 257*i+130, 128);
        }
        return s;
    }
    private BigInteger encode(BigInteger i){
        return i;
    }
    private BigInteger decode(BigInteger i){
        return i;
    }
    public static byte[] to128(byte[] x){
        byte[] X=new byte[128];
        int offset=128-x.length;
        int W=0;
        if (x[0]==0 && offset==-1){
            W++;
            offset++;
        }
        for (int i=0; i<x.length-W; i++){
            X[i+offset]=x[i];
        }
        return X;
    }
    public ArrayList<BigInteger> fufill(ArrayList<BigInteger> R3PrimeValues){
        ArrayList<BigInteger> wInverse=new ArrayList<BigInteger>();
        for (int i=0; i<wValues.size(); i++){
            wInverse.add(wValues.get(i).modPow(new BigInteger("-1"),AIB_Client.denominationPubKeys.get(denominationIDs.get(i))));
        }
        ArrayList<BigInteger> RValues=new ArrayList<BigInteger>();
        for (int i=0; i<wValues.size(); i++){
            RValues.add(R3PrimeValues.get(i).multiply(wInverse.get(i)).mod(AIB_Client.denominationPubKeys.get(denominationIDs.get(i))));
        }
        return RValues;
    }
    public String toString(){
        String s="";
        for (int i=0; i<R1PrimeValues.size(); i++){
            s=s+Hex.encodeHexString(new byte[] {denominationIDs.get(i).byteValue()});
            //System.out.println(R2PrimeValues.get(i).toByteArray().length);
            //System.out.println(R2PrimeValues.get(i).toByteArray()[0]);
            s=s+Hex.encodeHexString(to128(R2PrimeValues.get(i).toByteArray()));
            //s=s+R2PrimeValues.get(i);
        }
        return s;
    }
    
}
