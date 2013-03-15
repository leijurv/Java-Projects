/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stage.pkg7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author leif
 */
public class Stage7 {

    /**
     * @param args the command line arguments
     */
    public static String[] addstring(String packet, String[] packets){
            String[] n=new String[packets.length+1];
            System.arraycopy(packets, 0, n, 0, n.length-1);
            n[n.length-1]=packet;
            return n;
        }
    public static String[] readfile(String filename){
        String[] result=new String[0];
        try{
            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                result=addstring(strLine,result);
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static String get(){
        String[] ciphertextArray=readfile("/Users/leif/Desktop/stage7unformatted.txt");
        String a="";
        for (int i=0; i<ciphertextArray.length; i++){
            String q=meow(ciphertextArray[i]);
           a=a+q;
           
        }
        return a;
    }
    
    public static void write(String filename){
        try {
            FileOutputStream a=new FileOutputStream("/Users/leif/Desktop/stage7.txt");
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            c.write(filename);
            c.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
    public static void write(String[] filename){
        try {
            FileOutputStream a=new FileOutputStream("/Users/leif/Desktop/stage7anal.txt");
            DataOutputStream b=new DataOutputStream(a);
            BufferedWriter c=new BufferedWriter(new OutputStreamWriter(b));
            c.flush();
            for (int i=0; i<filename.length; i++){
                c.write(filename[i]);
                c.write("\n");
            }
            c.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
    public static String meow(String a){
        if (a.length()<10){
            return "";
        }
        return a.substring(38,39);
        
    }
    public static void format(){
        write(get());
    }
    public static String read(){
        String[] ciphertextArray=readfile("/Users/leif/Desktop/stage7.txt");
        String a="";
        for (int i=0; i<ciphertextArray.length; i++){
           a=a+ciphertextArray[i];
        }
        return a;
    }
    public static void main(String[] args) {
        //format();
        String ciphertext=read();
        System.out.println(ciphertext);
        System.out.println(ciphertext.length());
        String[] analysis=meowmeow(ciphertext);
        
        write(analysis);
    } 
    public static String[] meowmeow(String ciphertext){
        String[][] a=new String[9][0];
        for (int i=2; i<=10; i++){
            a[i-2]=commonGroup(ciphertext,i);
            write(a[i-2]);
            for (int n=0; n<a[i-2].length; n++){
                System.out.println(a[i-2][n]);
            }
        }
        String[] b=new String[0];
        for (int i=0; i<a.length; i++){
            for (int n=0; n<a[i].length; n++){
                b=add(b,a[i][n]);
            }
        }
        return b;
    }
    public static String[] commonGroup(String a,int group){
            String[] b=new String[a.length()-group];
            String[] e=new String[0];
            int[] ocr=new int[0];
            String[] cc=new String[0];
            String[] common=new String[0];
            for (int i=0; i<b.length; i++){
                String c=a.substring(i,i+group);
                b[i]=c;
                boolean d=false;
                for (int n=0; n<cc.length; n++){
                    if (cc[n].equals(c)){
                        ocr[n]++;
                        d=true;
                    }
                }
                if (!d){
                    cc=add(cc,c);
                    ocr=add(ocr,1);
                }
                if (ocr.length!=cc.length){
                    throw new RuntimeException("MAJOR ERROR");
                }
                System.out.println(i+":"+b.length);
            }
             int[] alr=new int[0];
             int lst=0;
             for (int i=0; i<ocr.length; i++){
                 if (ocr[i]<=ocr[lst]){
                     lst=i;
                 }
             }
             int prev=0;
            for (int n=0; n<cc.length; n++){
               
                
                int ci=lst;
                for (int i=0; i<cc.length; i++){
                    
                    if (ocr[i]>=ocr[ci] && ocr[i]<=((alr.length>0)?ocr[alr[alr.length-1]]:10000000)){
                        ci=i;
                    }
                }
                alr=add(alr,ci);
                common=add(common,cc[ci]+":"+ocr[ci]+":"+ci);
                
                if (n-prev==10){
                    prev=n;
                    System.out.println(n+":"+cc.length);
                }
            }
            
            return common;
        }
    public static String[] add(String[] a, String b){
            String[] c=new String[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
        public static int[] add(int[] a, int b){
            int[] c=new int[a.length+1];
        System.arraycopy(a, 0, c, 0, a.length);
            c[a.length]=b;
            return c;
        }
}
