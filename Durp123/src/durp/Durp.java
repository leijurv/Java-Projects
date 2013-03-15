/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package durp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author leijurv
 */
public class Durp {

    /**
     * @param args the command line arguments
     */
    public static void d(Process p) throws IOException{
        String line;
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
  while ((line = input.readLine()) != null) {
    System.out.println(line);
  }
  input.close();
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        String line;
  Process p = Runtime.getRuntime().exec("login leijurv");
  //Thread.sleep(10000);
  d(p);
  //d(p);
  //p.getOutputStream().write(("5021".getBytes()));
        // TODO code application logic here
    }
}
