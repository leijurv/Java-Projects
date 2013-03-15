/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openports;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class OpenPorts extends Thread{

    /**
     * @param args the command line arguments
     */
    public void checkPort(int i) {
        Socket socket = null;
        try {
            socket = new Socket("nuevaschool.org", i);
        } catch (Exception e) {
            //System.out.println(i);
            return;
        } 
System.out.println();
            System.out.println("OPEN"+i);
            System.out.println();
        
    }
    int port;
    public OpenPorts(int i){
        port=i;
    }
    killer k;
    public void run(){
        k=new killer(this);
        k.start();
        checkPort(port);
    }
    
    public static void portOpen(int i){
        (new OpenPorts(i)).start();
    }
    public static class killer extends Thread{
        OpenPorts op;
        public killer(OpenPorts o){
            op=o;
        }
        
        public void run(){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            if (op.isAlive()){
                System.out.println("attemping to stop..."+op.port);
                op.stop();
            }
        }
    }
    public static void main(String[] args) throws Exception{
        for (int i=0; i<1000; i++){
            Thread.sleep(10);
            portOpen(i);
        }
    }
}
