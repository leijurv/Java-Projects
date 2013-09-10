/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leif
 */
public class RequestThread extends Thread{
    Socket s;
    public RequestThread(Socket S){
        s=S;
        
    }
    public void run(){
        PrintStream ps = null;
        try {
            ps = new PrintStream(s.getOutputStream());
            BufferedInputStream is = new BufferedInputStream(s.getInputStream());
            //Thread.sleep(2000L);
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            if (b.length==0){
                ps.append("Airor");
                ps.close();
                return;
            }
            //ps.append("HELLO");
            String request=new String(b);
            request=request.split("\n")[0];
            request=request.substring(4,request.length()-10);
            if (request.startsWith("/getRPrime/")){
                ps.append(AIB_Server.processRPrime(request.substring(11,request.length())));
            }
            if (request.startsWith("/getPubKeys")){
                System.out.println("Public Keys Requests");
                for (int i=0; i<AIB_Server.KeyPairs.size(); i++){
                    ps.append(AIB_Server.KeyPairs.get(i).modulus.toString(16));
                    if (i!=AIB_Server.KeyPairs.size()-1){
                        ps.append(",");
                    }
                }
            }
            if (request.startsWith("/getComPubKey")){
                ps.append(AIB_Server.comKeyPair.modulus.toString(16));
            }
            if (request.startsWith("/tx")){
                ps.append(AIB_Server.processTx(request.substring(3,request.length())));
            }
            System.out.println(request);
            ps.close();
            is.close();
        } catch (Exception ex) {
            Logger.getLogger(RequestThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ps.close();
        }
    }
    
}
