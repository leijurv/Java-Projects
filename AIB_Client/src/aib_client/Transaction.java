/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import cryptolib.Hex;
import cryptolib.RSAKeyPair;
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
    OtherDepositRequest To;
    RValueStorage From;
    public Transaction(OtherDepositRequest to, RValueStorage from){
        To=to;
        From=from;
        BigInteger value=to.totalValue;
        ArrayList<Integer> values=from.denomIDs;
        int[] nums=new int[AIB_Client.denominationPubKeys.size()];
        for (int i : values){
            nums[i]++;
        }
        ArrayList<Integer> W=DepositRequestsPane.getDValues(value);
        //System.out.println(W);
        int[] w=new int[nums.length];
        for (int i : W){
            w[i]++;
        }
        for (int Q : w){
            //System.out.print(Q+",");
        }
        //System.out.println();
        for (int Q : nums){
            //System.out.print(Q+",");
        }
        //System.out.println();
        int last=0;
        boolean need=false;
        for (int i=0; i<nums.length; i++){
            if (w[i]>nums[i]){
                need=true;
            }
            if (w[i]<nums[i]){
                if (need){
                    last=i;
                    need=false;
                }
            }
        }
        for (int i=0; i<last; i++){
            w[i]=0;
        }
        w[last]++;
        for (int x : w){
            //System.out.print(x+",");
        }
        //System.out.println();
        BigInteger totalValue=new BigInteger("0");
        for (int i=0; i<w.length; i++){
            BigInteger q=BigInteger.TEN.pow(i).multiply(new BigInteger(Integer.toString(w[i])));
            totalValue=totalValue.add(q);
        }
        //System.out.println(totalValue);
        //System.out.println(value);
        BigInteger dif=totalValue.subtract(value);
        ArrayList<Integer> BeingSent=DepositRequestsPane.getDValues(totalValue);
        ArrayList<Integer> BeingSentBack=DepositRequestsPane.getDValues(dif);
        //System.out.println();
        int numOutputToThem=values.size();
        int numInput=BeingSent.size();
        int numOutputToMe=BeingSentBack.size();
        System.out.println("Amount being taken from R values.  Amount being sent to them.  Amount being sent back to me.");
        System.out.println("Number of R values "+numInput+","+numOutputToThem+","+numOutputToMe);
        System.out.println("Value in bitcoins "+fix(totalValue)+","+fix(value)+","+fix(dif));
        System.out.println("Value of R values "+FIX(BeingSent)+","+FIX(W)+","+FIX(BeingSentBack));
        //Requesting R' values for the amount being sent back
        ArrayList<BigInteger> RPrimeValues=requestRPrimeValues(BeingSentBack);
        ArrayList<Integer> dValues=new ArrayList<Integer>();
        dValues.addAll(BeingSent);
        ArrayList<Integer> storageLocations=new ArrayList<Integer>();
        ArrayList<BigInteger> RValuesBeingUsed=new ArrayList<BigInteger>();//Getting R values to be used as input
        for (int i=0; i<dValues.size(); i++){
            System.out.println(dValues.get(i));
           for (int n=0; n<from.denomIDs.size(); n++){
               System.out.println(from.denomIDs.get(n));
               if (from.denomIDs.get(n).intValue()==dValues.get(i).intValue() && !storageLocations.contains(n)){
                   storageLocations.add(n);
                   RValuesBeingUsed.add(from.RValues.get(n));
               }
           }
        }
        System.out.println(RValuesBeingUsed);
        System.out.println(to.R2PrimeValues);
        System.out.println(RPrimeValues);
        System.out.println(BeingSent);
        System.out.println(to.denomIDs);
        System.out.println(BeingSentBack);
        int unpaddedsize=RValuesBeingUsed.size()*129+to.R2PrimeValues.size()*129+RPrimeValues.size()*129+2;//129 because dValue is 1 byte
        //+2 because 1 byte for # of inputs, 1 byte for # of outputs
        int paddedsize=unpaddedsize+10;//Ten bytes of padding
        System.out.println(unpaddedsize);
        byte[] notencnotpad=new byte[unpaddedsize];
        System.out.println(notencnotpad.length);
        notencnotpad[0]=(byte) RValuesBeingUsed.size();
        for (int i=0; i<RValuesBeingUsed.size(); i++){
            notencnotpad[i*129+1]=dValues.get(i).byteValue();
            byte[] c=DepositRequest.to128(RValuesBeingUsed.get(i).toByteArray());
            for (int n=0; n<128; n++){
                notencnotpad[i*129+2+n]=c[n];
            }
        }
        int outputindex=RValuesBeingUsed.size()*129+1;
        notencnotpad[outputindex]=(byte) (to.R2PrimeValues.size()+RPrimeValues.size());
        for (int i=0; i<to.R2PrimeValues.size(); i++){
            notencnotpad[i*129+1+outputindex]=to.denomIDs.get(i).byteValue();
            byte[] c=DepositRequest.to128(to.R2PrimeValues.get(i).toByteArray());
            for (int n=0; n<128; n++){
                notencnotpad[i*129+2+outputindex+n]=c[n];
            }
        }
        int outputpart2index=outputindex+to.R2PrimeValues.size()*129;
        for (int i=0; i<RPrimeValues.size(); i++){
            notencnotpad[i*129+outputpart2index+1]=BeingSentBack.get(i).byteValue();
            byte[] c=DepositRequest.to128(RPrimeValues.get(i).toByteArray());
            for (int n=0; n<128; n++){
                notencnotpad[i*129+2+outputpart2index]=c[n];
            }
        }
    }
    public static ArrayList<BigInteger> requestRPrimeValues(ArrayList<Integer> dValues){
        RSAKeyPair R=new RSAKeyPair();
            R.generate(new BigInteger(500,100,new SecureRandom()),new BigInteger(500,100,new SecureRandom()),AIB_Client.e,false);
            String S="";
            
                    S=S+Hex.encodeHexString(new byte[] {new Integer(dValues.size()).byteValue()});
            for (int i=0; i<dValues.size(); i++){
                S=S+Hex.encodeHexString(new byte[] {dValues.get(i).byteValue()});
            }
            //System.out.println(S.length()/2);
            S=S+Hex.encodeHexString(DepositRequest.to128(R.modulus.toByteArray()));
            S=AIB_Client.webpage+"getRPrime/"+S;
            String r="";
            try {
                r=AIB_Client.load(S);
            } catch (Exception ex) {
                Logger.getLogger(DepositRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<BigInteger> rPrimeValues=new ArrayList<BigInteger>();
            byte[] WWW=Hex.decodeHex(r.toCharArray());
            for (int i=0; i<WWW.length/128; i++){
                byte[] X=new byte[128];
                for (int n=0; n<128; n++){
                    X[n]=WWW[n+i*128];
                }
                rPrimeValues.add(R.decode(new BigInteger(X)));
            }
            return rPrimeValues;
    }
    public static String fix(BigInteger dif){
        return new BigDecimal(dif).divide(new BigDecimal("100000000")).toString();
    }
    public static ArrayList<BigDecimal> FIX(ArrayList<Integer> w){
        ArrayList<BigDecimal> totalValue=new ArrayList<BigDecimal>();
        for (int i=0; i<w.size(); i++){
            BigInteger q=BigInteger.TEN.pow(w.get(i));
            totalValue.add(new BigDecimal(fix(q)));
        }
        return totalValue;
    }
}
