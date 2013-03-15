/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;

/**
 *
 * @author leif
 */
public class SimpleTest{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(5021);
        boolean x = true;
        while (x) {

            Socket s = ss.accept();
            //x=false;
            System.out.println("got connection");
            try {
                // Create file 
                FileWriter fstream = new FileWriter("out.sh");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write("osascript -e " + '"' + "set volume 7" + '"');
                //Close the output stream
                out.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
            // TODO code application logic here
            long l = System.currentTimeMillis();
            while (System.currentTimeMillis() < (l + 20000)) {
                Runtime.getRuntime().exec("sh out.sh");
                Thread.sleep(100);
            }

        }
    }
}
