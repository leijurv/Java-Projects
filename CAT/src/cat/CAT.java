/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cat;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class CAT {
    byte[] state;
    int i;
    int n;
    MessageDigest md;
    byte[] origState;
    public CAT(byte[] pwd, boolean streaming, MessageDigest M, int N){
        md=M;
        md.reset();
        state=md.digest(pwd);
        md.reset();
        if (!streaming){
            origState=state;
        }
        i=0;
        n=N;
    }
    public CAT(byte[] pwd, boolean streaming, MessageDigest M){
        this(pwd,streaming,M,M.getDigestLength()/2);
    }
    public CAT(byte[] pwd, boolean streaming){
        this(pwd,streaming,getDefaultMessageDigest());
    }
    public CAT(byte[] pwd){
        this(pwd,false);
    }
    public static MessageDigest getDefaultMessageDigest(){
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException ex) {
        }
        return null;
    }
    private byte encode(byte input){
        byte result=(byte) (input^state[i]);
        state[i]=input;
        if (i==n-1){
            i=0;
            state=md.digest(state);
            md.reset();
        }else{
            i++;
        }
        return result;
    }
    private byte decode(byte input){
        byte result=(byte) (input^state[i]);
        state[i]=result;
        if (i==n-1){
            i=0;
            state=md.digest(state);
            md.reset();
        }else{
            i++;
        }
        return result;
    }
    private byte[] encode1(byte[] input){
        if (origState!=null){
            resetState();
        }
        byte[] result=new byte[input.length];
        int offset=0;
        while(i!=0){
            result[offset]=encode(input[offset]);
            offset++;
        }
        for (int I=0; I<result.length; I+=n){
            int x=I+offset;
            for (int II=0; II<n && II+x<result.length; II++){
                result[II+x]=(byte) (input[x+II]^state[II]);
                state[II]=input[II+x];
            }
            if (n+x>result.length){
                i=result.length-x;
            }else{
                state=md.digest(state);
                md.reset();
            }
        }
        return result;
    }
    private byte[] decode1(byte[] input){
        if (origState!=null){
            resetState();
        }
        byte[] result=new byte[input.length];
        int offset=0;
        while(i!=0){
            result[offset]=encode(input[offset]);
            offset++;
        }
        for (int I=0; I<result.length; I+=n){
            int x=I+offset;
            for (int II=0; II<n && II+x<result.length; II++){
                result[II+x]=(byte) (input[x+II]^state[II]);
                state[II]=result[II+x];
            }
            if (n+x>result.length){
                i=result.length-x;
            }else{
                state=md.digest(state);
                md.reset();
            }
        }
        return result;
    }
    private byte[] encode2(byte[] input){
        if (origState!=null){
            resetState();
        }
        byte[] result=new byte[input.length];
        for (int i=0; i<result.length; i++){
            result[i]=encode(input[i]);
        }
        return result;
    }
    private byte[] decode2(byte[] input){
        if (origState!=null){
            resetState();
        }
        byte[] result=new byte[input.length];
        for (int i=0; i<result.length; i++){
            result[i]=decode(input[i]);
        }
        return result;
    }
    public byte[] encode(byte[] input){
        if (input.length>n*3){
            return encode1(input);
        }
        return encode2(input);
    }
    public byte[] decode(byte[] input){
        if (input.length>n*3){
            return decode1(input);
        }
        return decode2(input);
    }
    private static InputStream get(InputStream input, CAT c, boolean enc){
        InputStream i=new InputStream() {
            @Override
            public int read() throws IOException {
                int a=input.read();
                if (a==-1){
                    return a;
                }else{
                    byte b=(byte)a;
                    if (enc){
                        b=c.encode(b);
                    }else{
                        b=c.decode(b);
                    }
                    if (b<0){
                        return 256+b;
                    }
                    return b;
                }
            }
            @Override
            public int read(byte[] b, int off, int len) throws IOException{
                int a=input.read(b,off,len);
                for (int i=off; i<off+a; i++){
                    if (enc){
                        b[i]=c.encode(b[i]);
                    }else{
                        b[i]=c.decode(b[i]);
                    }
                }
                return a;
            }
            @Override
            public int available() throws IOException{
                return input.available();
            }
            @Override
            public void close() throws IOException{
                input.close();
            }
            public void reset() throws IOException{
                input.reset();
            }
        };
        return i;
    }
    public static InputStream encode(InputStream input, byte[] pwd, MessageDigest md, int N){
        CAT c=new CAT(pwd,true,md,N);
        return get(input,c,true);
    }
    public static InputStream decode(InputStream input, byte[] pwd, MessageDigest md, int N){
        CAT c=new CAT(pwd,true,md,N);
        return get(input,c,false);
    }
    public static InputStream encode(InputStream input, byte[] pwd){
        CAT c=new CAT(pwd,true);
        return get(input,c,true);
    }
    public static InputStream decode(InputStream input, byte[] pwd){
        CAT c=new CAT(pwd,true);
        return get(input,c,false);
    }
    private void resetState(){
        state=new byte[origState.length];
        for (int I=0; I<state.length; I++){
            state[I]=origState[I];
        }
        i=0;
    }
    public static void print(byte[] b){
        for (byte B : b){
            System.out.print(B+",");
        }
        System.out.println("lol");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        int A=0;
        int B=0;
        for (int i=0; i<10; i++){
            Random r=new Random();
            byte[] C=new byte[10];
            r.nextBytes(C);
        CAT c=new CAT(C);
        InputStream f=encode(new FileInputStream(new File("/Users/leijurv/Desktop/lol.log")),C);
        byte[] b=new byte[f.available()];
        long t=System.currentTimeMillis();
        f.read(b);
        long q=System.currentTimeMillis();
        System.out.println(q-t);
        A+=q-t;
        b=c.decode(b);
        long rr=System.currentTimeMillis();
        System.out.println(rr-q);
        B+=rr-q;
        byte[] d=new byte[100];
        for (int I=0; I<100; I++){
            d[I]=b[I+100000];
        }
        System.out.println(new String(d));
        }
        System.out.println(A/10);
        System.out.println(B/10);
    }
    }
