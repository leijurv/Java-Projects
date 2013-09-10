/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_client;

import cryptolib.Hex;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author leif
 */
public class OtherDepositRequest {
    ArrayList<BigInteger> R2PrimeValues;
    ArrayList<Integer> denomIDs;
    BigInteger totalValue;
    public OtherDepositRequest(String value){
        byte[] b=Hex.decodeHex(value.toCharArray());
        if (b.length%129!=0){
            throw new IllegalArgumentException("Must be divisible by 129");
        }
        int x=b.length/129;
        denomIDs=new ArrayList<Integer>();
        R2PrimeValues=new ArrayList<BigInteger>();
        for (int i=0; i<x; i++){
            denomIDs.add(new Integer(b[i*129]));
            byte[] W=new byte[128];
            for (int n=0; n<128; n++){
                W[n]=b[n+129*i+1];
            }
            R2PrimeValues.add(new BigInteger(W));
        }
        totalValue=new BigInteger("0");
        for (int i=0; i<denomIDs.size(); i++){
            totalValue=totalValue.add(BigInteger.TEN.pow(denomIDs.get(i)));
        }
    }
}
