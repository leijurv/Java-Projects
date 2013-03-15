/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cracker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author leijurv
 */
public class Cracker {

    /**
     * @param args the command line arguments
     */
    static final String alphabet="abcdefghijklmnopqrstuvwxyz";
    static final String numbers="0123456789";
    static final String others="`~-_=+[{]}\\|;:'"+'"'+",<.>/?";
    static final String Alphabet=alphabet.toUpperCase();
    static final String Numbers="!@#$%^&*()";
    static final String Hard=alphabet+numbers+Alphabet+Numbers+others;
    static final String Hash="5021";
    public static String[] getFromPos(int amount, BigInteger offset){
        String[] result=new String[amount];
        int[] currentStart=toIntArray(offset);
        for (int i=0; i<amount; i++){
            result[i]=getString(currentStart);
            currentStart=next(currentStart);
        }
        return result;
    }
    public static String getString(int[] x){
        String result="";
        for (int i=0; i<x.length; i++){
            result=result+Hard.substring(x[i],x[i]+1);
        }
        return result;
    }
    public static String getHash(String pwd){
        return pwd; //TROLOLOL implement it yourself, lazy
    }
    public static int[] next(int[] y){
        int[] x=new int[y.length];
        System.arraycopy(y, 0, x, 0, y.length);
        int pos=x.length-1;
        boolean done=false;
        while(!done){
            if (pos==-1){
                int[] result=new int[x.length+1];
                for (int i=1; i<result.length; i++){
                    result[i]=0;
                }
                
                result[0]=0;
                return result;
            }
            x[pos]++;
            if (x[pos]==Hard.length()){
                x[pos]=0;
                pos--;
                
            }else{
                done=true;
            }
        }
        return x;
    }
    public static int[] toIntArray(BigInteger ofset){
        BigInteger offset=new BigInteger(ofset.toString());
        ArrayList<Integer> result=new ArrayList<Integer>();
        BigInteger len=new BigInteger(Integer.toString(Hard.length()));
        //System.out.println(offset.mod(len));
        //
        while(offset.compareTo(len)!=-1){
            result.add(offset.mod(len).intValue());
            offset=(offset.subtract(offset.mod(len))).divide(len);
            
        }
        result.add(0,offset.intValue());
        int[] result1=new int[result.size()];
        int i=0;
        for (int x : result){
            result1[i]=x;
            i++;
        }
        return result1;
    }
    public static BigInteger fromIntArray(int[] a){
        BigInteger result=BigInteger.ZERO;
        BigInteger len=new BigInteger(Integer.toString(Hard.length()));
        System.out.println(len);
        for (int i=a.length-1; i>=0; i--){
            result=result.multiply(len).add(new BigInteger(Integer.toString(a[i])));
        }
        return result;
    }
    public static void main(String[] args) {
        BigInteger position=BigInteger.ONE;
        int increment=Hard.length()*100;
        
        int len=4;
        BigInteger total=new BigInteger(Long.toString((long)Math.pow(Hard.length(),len)));
        while(toIntArray(position).length!=len+1){
            String[] d=getFromPos(increment,position);
            position=position.add(new BigInteger(Integer.toString(increment)));
            
             for (String x : d){
                 if (getHash(x).startsWith("abcd") ){
                     System.out.println("SUCCESS!");
                     System.out.println(x);
                     return;
                 }
             }
             System.out.print(d[1]+":");
             System.out.println((float)(position.multiply(new BigInteger("1000")).divide(total).intValue()) /10+"%");
            //D is the array of the current passwords (9400 of 94000 length, I forget which)
        }
    }
}
