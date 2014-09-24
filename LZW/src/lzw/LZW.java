/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lzw;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author leijurv
 */
public class LZW {
    static ArrayList<byte[]> dictionary=new ArrayList<byte[]>();
    static int numBits;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        FileInputStream Aa=new FileInputStream("/Users/leijurv/Desktop/derp.log");
        
        //FileInputStream Aa=new FileInputStream("/Users/leijurv/Documents/adsf.txt");
        //FileInputStream Aa=new FileInputStream("/Users/leijurv/Downloads/hand1.png");
        byte[] x=new byte[100];
        System.out.println(Aa.read(x)+","+x.length);
        Aa.close();
        /*
        byte[] b=new byte[5001];
        for (int i=0; i<5000; i++){
            b[i]=x[i];
        }
        b[5000]=-128;*/
        byte[] b=x;
        b[b.length-1]="#".getBytes()[0];
        System.out.println(new String(b));
        d();
        //String m="TOBEORNOTTOBEORTOBEORNRNR#";
        String code=encode(b);
        System.out.println(code);
        dictionary=new ArrayList<byte[]>();
        d();
        //String code="101000111100010001010111110010001110001111010100011011011101011111100100011110100000100010000000";
        byte[] res=(decode(code));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(new String(res));
        System.out.println(code.length());
        System.out.println(b.length*8);
        for (int i=0; i<res.length; i++){
            if (res[i]!=b[i]){
                System.out.println("NOP"+i+","+res[i]+","+b[i]);
            }
        }
    }
    public static String encode(byte[] enc){
        int wid=1;
        while(getCode(sub(enc,0,wid))!=-1){
            wid++;
        }
        wid--;
        byte[] w=sub(enc,0,wid);
        String result=(print(toB(getCode(w),numBits)));
        //System.out.println(result);
        if (getCode(sub(enc,wid,wid+1))==0){
            return result+print(toB(0,numBits));
        }
        dictionary.add(sub(enc,0,wid+1));
        if (dictionary.size()>Math.pow(2,numBits)){
            numBits++;
        }
        byte[] next=sub(enc,wid,enc.length);
        return result+encode(next);
    }
    public static byte[] decode(String codes) throws IOException{
        byte[] prev=null;
        ByteArrayOutputStream a=new ByteArrayOutputStream();
        while(codes.length()!=0){
            int pos=fromB(codes.substring(0,numBits));
            byte[] cur=null;
            boolean f=true;
            if (pos==dictionary.size()){
                //cur=(prev+new String(sub(prev,0,1))).getBytes();
                cur=new byte[prev.length+1];
                for (int i=0; i<cur.length-1; i++){
                    cur[i]=prev[i];
                }
                cur[prev.length]=prev[0];
                f=false;
                //System.out.println("MEOWMEOWMEOW");
            }else{
                cur=dictionary.get(pos);
            }
            
            codes=codes.substring(numBits,codes.length());
            a.write(cur);
            System.out.print(new String(cur));
            if (prev!=null && f){
                byte[] n=new byte[prev.length+1];
                for (int i=0; i<prev.length; i++){
                    n[i]=prev[i];
                }
                n[prev.length]=cur[0];
                //String conj=new String(prev)+new String(sub(cur,0,1));
                dictionary.add(n);
            }
            if (dictionary.size()==Math.pow(2,numBits)){
                numBits++;
            }
            prev=cur;
        }
        return a.toByteArray();
    }
    public static int getCode(byte[] a){
        for (int i=0; i<dictionary.size(); i++){
            if (equals(dictionary.get(i),a)){
                return i;
            }
        }
        return -1;
    }
    public static boolean equals(byte[] a, byte[] b){
        if (a.length!=b.length){
            return false;
        }
        for (int i=0; i<a.length; i++){
            if (a[i]!=b[i]){
                return false;
            }
        }
        return true;
    }
    public static int getCode(byte a){
        return getCode(new byte[]{a});
    }
    public static void d(){
        numBits=8;
        byte a="#".getBytes()[0];
        dictionary.add(new byte[] {a});
        //dictionary.add("#".getBytes());
        //dictionary.add(new byte[] {0});
        for (byte i=-128; i<127; i++){
            if (i!=a)
            dictionary.add(new byte[] {i});
        }
        dictionary.add(new byte[] {127});
    }
    public static void D(){
        numBits=5;
        dictionary.add("#".getBytes());
        dictionary.add(" ".getBytes());
        char b='A';
        for (int i=0; i<26; i++){
            byte c=(byte) (b+i);
            dictionary.add(new byte[] {c});
        }
    }
    public static boolean[] toB(String code){
        boolean[] result=new boolean[code.length()];
        for (int i=0; i<result.length; i++){
            result[i]=code.charAt(i)=='1';
        }
        return result;
    }
    public static boolean[] toB(int code, int width){
        if (code==0){
            return new boolean[width];
        }
        if (code%2==0){
            boolean[] r=toB(code/2,width-1);
            boolean[] res=new boolean[width];
            System.arraycopy(r,0,res,0,r.length);
            return res;
        }
        boolean[] r=toB(code-1,width);
        r[r.length-1]=true;
        return r;
    }
    public static byte[] sub(byte[] a, int c, int d){
        byte[] result=new byte[d-c];
        for (int i=c; i<d; i++){
            result[i-c]=a[i];
        }
        return result;
    }
    public static int fromB(String a){
        int total=0;
        for (int i=0; i<a.length(); i++){
            if (a.charAt(a.length()-i-1)=='1'){
                total+=Math.pow(2,i);
            }
        }
        return total;
    }
    public static String print(boolean[] a){
        String res="";
        for (int i=0; i<a.length; i++){
            if (a[i]){
                res=res+"1";
            }else{
                res=res+"0";
            }
        }
        return res;
    }
}
