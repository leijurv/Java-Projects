/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package firstbits;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author leif
 */
public class Firstbits{
    public static void main(String[] args) {
String pos="159DHMRVZdhmquy26AEJNSWaeinrvz37BFKPTXbfjosw48CGLQUYcgkptx";

        for (int i=0; i<58; i++){
            System.out.println(i);
            for (int j=0; j<58; j++){
                System.out.println(j);
                for (int k=0; k<58; k++){
                    System.out.println(k);
                    if (!works("1"+pos.charAt(i)+pos.charAt(j)+pos.charAt(k))){
                        System.out.println("1"+pos.charAt(i)+pos.charAt(j)+pos.charAt(k));
                    }
                }
            }
        }

    }
    public static boolean works(String s){
        URL url;

        try {
            // get URL content

            String a="http://blockchain.info/q/resolvefirstbits/"+s;
            url = new URL(a);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                return true;
            }
            br.close();


        } catch (Exception e){}
        return false;
    }
}