/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sorter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leijurv
 */
public class Sorter extends Thread{
    String origPath="/Users/leijurv/Documents/Rainbow/";
    String path="/Users/leijurv/Documents/Rainbow_Sorted/";
    int id;
    int end;
    public Sorter(int start,int End){
        id=start;
        end=End;
    }
    HashMap<String,FileWriter> map=new HashMap<String,FileWriter>();
    public void run(){
        while(id<end){
        try{
            FileInputStream fstream = new FileInputStream(origPath+"rainbow"+id+".txt");
            /*
            byte[] Hash=new byte[16];
            int i=0;
            while(fstream.available()>16){
                fstream.read(Hash);
                String c="";
                byte[] rest=new byte[1];
                fstream.skip(1);
                fstream.read(rest);
                char current=(char)rest[0];
                while(current!='\n'){
                    fstream.read(rest);
                    current=(char)rest[0];
                    c=c+current;
                }
                String hash=Hex.encodeHexString(Hash).substring(0,3);
                FileWriter f=map.get(hash);
                    if (f==null){
                        f=new FileWriter(path+hash+".txt");
                        map.put(hash,f);
                    }
                    f.write(new String(Hash)+":"+c);
                    i++;
                    if (i%10000==0){
                        System.out.println(i);
                    }
            }*/
            
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                String hash=strLine.substring(0,3);
                    FileWriter f=map.get(hash);
                    if (f==null){
                        f=new FileWriter(path+hash+".txt");
                        map.put(hash,f);
                    }
                    f.write(strLine+"\n");
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        id++;
        System.out.println(id);
        }
        String[] n={"a","b","c","d","e","f","0","1","2","3","4","5","6","7","8","9"};
        for (String N : n){
        for (String NN : n){
            for (String NNN : n){
                try {
                    map.remove(N+NN+NNN).close();
                } catch (IOException ex) {
                    Logger.getLogger(Sorter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        (new File(new Sorter(0,0).path)).mkdirs();
        (new Sorter(0,1)).start();
        // TODO code application logic here
    }
}
