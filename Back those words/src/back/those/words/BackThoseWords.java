/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package back.those.words;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author leif
 */
public class BackThoseWords {

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
                System.out.println(strLine);
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }
    public static String backwards(String a){
        String b="";
        for (int i=0; i<a.length(); i++){
            b=a.substring(i,i+1)+b;
        }
        return b;
    }
    public static void main(String[] args) {
        String[] thing=readfile("/Users/leif/Desktop/thing.txt");
        String[] b=new String[thing.length];
        for (int i=0; i<b.length; i++){
            b[i]=backwards(thing[i]);
        }
        for (int i=0; i<b.length; i++){
            System.out.println(b[b.length-i-1]);
        }
        
    }
}
