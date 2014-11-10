/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package block;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Random;
/**
 *
 * @author leijurv
 */
public class Block {
    public static int[][] SBoxes={{15,1,7,0,9,6,2,14,11,8,5,3,12,13,4,10},{3,7,8,9,11,0,15,13,4,1,10,2,14,6,12,5},{4,12,9,8,5,13,11,7,6,3,10,14,15,1,2,0},{2,4,10,5,7,13,1,15,0,11,3,12,14,9,8,6},{3,8,0,2,13,14,5,11,9,1,7,12,4,6,10,15},{14,12,7,0,11,4,13,15,10,3,8,9,2,6,1,5}};
    public static int[][] SInvBoxes={{3,1,6,11,14,10,5,2,9,4,15,8,12,13,7,0},{5,9,11,0,8,15,13,1,2,3,10,4,14,7,12,6},{15,13,14,9,0,4,8,7,3,2,10,6,1,5,11,12},{8,6,0,10,1,3,15,4,14,13,2,9,11,5,12,7},{2,9,3,0,12,6,13,10,1,8,14,7,11,4,5,15},{3,14,12,9,5,15,13,2,10,11,8,4,1,6,0,7}};
    public static int[] PBox={13,3,15,23,6,5,22,21,19,1,18,17,20,10,7,8,12,2,16,9,14,0,11,4};
    public static int[] PInvBox={21,9,17,1,23,5,4,14,15,19,13,22,16,0,20,2,18,11,10,8,12,7,6,3};
    public static int S(int block,int[][] Sb){
        int output=0;
        int n=0;
        for (int[] Sb1 : Sb){
            output|=Sb1[(block>>n)&15]<<n;
            n+=4;
        }
        return output;
    }
    public static int permute(int block,int[] pb){
        int output=0;
        for (int i=0; i<24; i++){
            int bit=(block>>pb[i])&1;
            output|=(bit<<i);
        }
        return output;
    }
    public static byte[] test(byte[] data,int key){
        for (int i=0; i+2<data.length; i+=3){
            int block=data[i+2]&0xFF|(data[i+1]&0xFF)<<8|(data[i]&0xFF)<<16;
            block^=key;
            block=permute(block,PInvBox);
            block=S(block,SInvBoxes);
            block^=key;
            block=permute(block,PInvBox);
            block=S(block,SInvBoxes);
            block^=key;
            block=permute(block,PInvBox);
            block=S(block,SInvBoxes);
            block^=key;
            if ((((byte) (block>>16))<0||((byte) (block>>8))<0||((byte) (block /*>> 0*/))<0)){
                return null;
            }
        }
        //System.out.println(new String(res));
        return data;
    }
    public static byte[] decrypt_data(byte[] data,int key,boolean k){
        byte[] res=new byte[data.length];
        for (int i=0; i+2<data.length; i+=3){
            int block=data[i+2]&0xFF|(data[i+1]&0xFF)<<8|(data[i]&0xFF)<<16;
            block^=key;
            for (int j=0; j<3; j++){
                block=permute(block,PInvBox);
                block=S(block,SInvBoxes);
                block^=key;
            }
            res[i]=(byte) (block>>16);
            res[i+1]=(byte) (block>>8);
            res[i+2]=(byte) (block /*>> 0*/);
            if (k&&(res[i]<0||res[i+1]<0||res[i+2]<0)){
                return null;
            }
        }
        return res;
    }
    public static byte[] decrypt_data(byte[] data,int key1,int key2,boolean k){
        byte[] res=new byte[data.length];
        for (int i=0; i+2<data.length; i+=3){
            int block=data[i+2]&0xFF|(data[i+1]&0xFF)<<8|(data[i]&0xFF)<<16;
            block^=key2;
            for (int j=0; j<3; j++){
                block=permute(block,PInvBox);
                block=S(block,SInvBoxes);
                block^=key2;
            }
            block^=key1;
            for (int j=0; j<3; j++){
                block=permute(block,PInvBox);
                block=S(block,SInvBoxes);
                block^=key1;
            }
            res[i]=(byte) (block>>16);
            res[i+1]=(byte) (block>>8);
            res[i+2]=(byte) (block /*>> 0*/);
            if (k&&(res[i]<0||res[i+1]<0||res[i+2]<0)){
                return null;
            }
        }
        return res;
    }
    public static byte[] decrypt(byte[] data,int key1,int key2){
        byte[] d=decrypt_data(data,key2,false);
        return decrypt_data(d,key1,true);
    }
    public static int encodeBlock(int block,int key1){
        block^=key1;
        block=S(block,SBoxes);
        block=permute(block,PBox);
        block^=key1;
        block=S(block,SBoxes);
        block=permute(block,PBox);
        block^=key1;
        block=S(block,SBoxes);
        block=permute(block,PBox);
        block^=key1;
        return block;
    }
    public static int decodeBlock(int block,int key2){
        block^=key2;
        block=permute(block,PInvBox);
        block=S(block,SInvBoxes);
        block^=key2;
        block=permute(block,PInvBox);
        block=S(block,SInvBoxes);
        block^=key2;
        block=permute(block,PInvBox);
        block=S(block,SInvBoxes);
        block^=key2;
        return block;
    }
    static int[] vals11;
    static int[] vals12;
    //static int[] vals21;
    //static int[] vals22;
    static final int numThr=5;
    public static void main(String[] args) throws Exception{
        FileInputStream a=new FileInputStream(new File("/Users/leijurv/Downloads/encrypted"));
        byte[] x=new byte[a.available()];
        a.read(x);
        //System.out.println(new String(x));
        //byte[] dd=decrypt_data(x,5,false);
        //int tar=dd[2]&0xFF|(dd[1]&0xFF)<<8|(dd[0]&0xFF)<<16;
        byte A='m';
        byte B='e';
        byte C='s';
        int b=C&0xFF|(B&0xFF)<<8|(A&0xFF)<<16;
        byte A1='s';
        byte B1='a';
        byte C1='g';
        int b1=C1&0xFF|(B1&0xFF)<<8|(A1&0xFF)<<16;
        System.out.println("Got values");
        vals11=new int[16711423];
        vals12=new int[16711423];
        System.out.println("Allocated vals1");
        for (int key1=-8355712; key1<8355711; key1++){
            vals11[key1+8355712]=encodeBlock(b,key1);
            vals12[key1+8355712]=encodeBlock(b1,key1);
            /*
             if (block==tar){
             System.out.println(key1+8355712);
             byte[] xx=decrypt_data(x,key1,5,true);
             if (xx!=null){
             System.out.println(new String(xx));
             }
             }*/
        }
        /*
         System.out.println("Calculated vals1");
        
         vals21=new int[16711423];
         vals22=new int[16711423];
         System.out.println("Allocated vals2");
         for (int key2=-8355712; key2<8355711; key2++){
         vals21[key2+8355712]=decodeBlock(tar,key2);
         vals22[key2+8355712]=decodeBlock(tar2,key2);
         }
         System.out.println("Calculated vals2");
         */
        int start=-(new Random().nextInt(8355711));
        //int start=8355711;
        for (int i=0; i<numThr; i++){
            final int s=start+i;
            new Thread() {
                public void run(){
                    search(x,s);
                }
            }.start();
        }
    }
    /*
     new Thread(){
     public void run(){
     search(x,start);
     }
     }.start();
     new Thread(){
     public void run(){
     search(x,start+1);
     }
     }.start();*/
    /*
     long A=System.currentTimeMillis();
     byte[] dd=decrypt_data(x,5,false);
     for (int key1=-8355712; key1<8355711; key1++){
     byte[] d=test(dd,key1);
     if (d!=null){
     System.out.println(new String(decrypt_data(x,key1,5,true)));
     }
     }
     System.out.println(System.currentTimeMillis()-A);*/
    public static void search(byte[] x,int start){
        int tar=x[2]&0xFF|(x[1]&0xFF)<<8|(x[0]&0xFF)<<16;
        int tar2=x[5]&0xFF|(x[4]&0xFF)<<8|(x[3]&0xFF)<<16;
        byte[] d=null;
        long t=System.currentTimeMillis();
        Random r=new Random();
        for (int k2=start; k2<vals11.length; k2=r.nextInt(vals11.length)){
            if (Math.abs(k2)%100<numThr){
                System.out.println("asdf"+k2+","+(System.currentTimeMillis()-t));
                t=System.currentTimeMillis();
            }
            d=null;
            int v1=decodeBlock(tar,k2-8355712);
            int v2=decodeBlock(tar2,k2-8355712);
            for (int k1=0; k1<vals11.length; k1++){
                if (vals11[k1]==v1&&vals12[k1]==v2){
                    if (d==null){
                        d=decrypt_data(x,k2-8355712,false);
                    }
                    System.out.println("fa");
                    byte[] xx=decrypt_data(d,k1-8355712,true);
                    if (xx!=null){
                        System.out.println(new String(xx));
                        System.out.println();
                        System.out.println(vals12[k1]+","+v2);
                    }
                }
            }
        }
    }
}
