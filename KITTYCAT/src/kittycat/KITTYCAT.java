/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kittycat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author leijurv
 */
public class KITTYCAT {
public static String load(String webpage, String id) throws Exception{
        URL url;
    url = new URL(webpage);
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setDoOutput(true);
con.setDoInput(true);
//con.setInstanceFollowRedirects(false); 
con.setRequestMethod("POST");
con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
DataOutputStream wr = new DataOutputStream(con.getOutputStream ());
wr.writeBytes("kennitala="+id);
wr.flush();
wr.close();
Reader r = new InputStreamReader(con.getInputStream());

StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}

r.close();
return buf.toString();
    }
public static boolean check(String id){
    try{
        load("http://claim.auroracoin.org/set.php",id);
    }catch(Exception e){
        if (e.toString().contains("fannst")){
            return false;
        }else{
            return true;
        }
    }
    return false;
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        int n=0;
        int[] data={2,8,0,1,6,1,0,0,0,9};
        for (int a=0; a<10; a++){
            for (int b=0; b<10; b++){
                data[6]=a;
                data[7]=b;
                data[8]=(22-(3*data[0]+2*data[1]+7*data[2]+6*data[3]+5*data[4]+4*data[5]+3*data[6]+2*data[7])%11)%11;
                if (n++>10){
                    break;
                }
                    
                for (int i : data){
                    System.out.print(i);
                }
                System.out.println();
            }
        }
        System.out.println();
        System.out.println();
        System.out.println();
        FileReader file=new FileReader("/Users/leijurv/Desktop/ice.txt");
        BufferedReader br=new BufferedReader(file);
        String line;
        int i=0;
        while((line=br.readLine())!=null){
            if (i++<10){
            System.out.println(line/*+":"+check(line)*/);
            }
        }
        
    }
    
}
