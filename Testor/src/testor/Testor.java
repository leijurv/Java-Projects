/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Testor {
public static void get(String s){
    ArrayList<String> x=new ArrayList<String>();
        try{
        System.out.println("asdf");
        URL url = new URL("http://ebony.extra.hu/moira/"+s+".html");
        InputStream is = url.openConnection().getInputStream();
System.out.println("asdf");
        BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );

        String line = null;
        int i=0;
        System.out.println("asdf");
        while( ( line = reader.readLine() ) != null )  {
                System.out.println(i+":"+line);
                i++;
                
        }
        reader.close();
        }catch(Exception e){
            System.out.println(e);
        }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        get("asdf");
        // TODO code application logic here
    }
}
