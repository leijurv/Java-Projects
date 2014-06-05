/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package goinham;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author leijurv
 */
public class GOINHAM {
public static String load(String webpage) throws Exception{//I copy and paste this code between projects a ton, don't even remember where I originally got it
        URL url = new URL(webpage);
URLConnection con = url.openConnection();
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
public static boolean test(String callsign) throws Exception{
    String r=load("http://www.hamdata.com/getcall.html?callsign="+callsign);
    return r.contains("is not in the Hamdata database");//Just searches the source code for that
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        String a="abcdefghijklmnopqrstuvwxyz".toUpperCase();
        String b="NWK";
        for (int k=0; k<2; k++){
        for (int i=0; i<26; i++){
            for (int j=0; j<26; j++){
                String callsign=b.charAt(k)+(a.charAt(i)+("6"+a.charAt(j)));
                if (test(callsign)){
                    System.out.println(callsign);
                }
            }
            System.out.println(("Finished with "+b.charAt(k))+(a.charAt(i)+"6*"));
        }
        }
    }
    
}
