/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aib_server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
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
      private static void sendHeader(BufferedOutputStream paramBufferedOutputStream, int paramInt, String paramString, long paramLong1, long paramLong2)
    throws IOException
  {
    paramBufferedOutputStream.write(("HTTP/1.0 " + paramInt + " OK\r\n" + "Date: " + new Date().toString() + "\r\n" + "Server: JibbleWebServer/1.0\r\n" + "Content-Type: " + paramString + "\r\n" + "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" + (paramLong1 != -1L ? "Content-Length: " + paramLong1 + "\r\n" : "") + "Last-modified: " + new Date(paramLong2).toString() + "\r\n" + "\r\n").getBytes());
  }
    public void run(){
        BufferedOutputStream ps = null;
        try {
            ps = new BufferedOutputStream(s.getOutputStream());
            BufferedInputStream is = new BufferedInputStream(s.getInputStream());
            //Thread.sleep(2000L);
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            if (b.length==0){
                ps.write("Airor".getBytes());
                
                ps.close();
                return;
            }
            sendHeader(ps, 200, "text/html", -1L, System.currentTimeMillis());
            //ps.append("HELLO");
            String request=new String(b);
            request=request.split("\n")[0];
            request=request.substring(4,request.length()-10);
            if (request.startsWith("/getRPrime/")){
                ps.write(AIB_Server.processRPrime(request.substring(11,request.length())).getBytes());
            }
            if (request.startsWith("/getPubKeys")){
                System.out.println("Public Keys Requests");
                for (int i=0; i<AIB_Server.KeyPairs.size(); i++){
                    ps.write(AIB_Server.KeyPairs.get(i).modulus.toString(16).getBytes());
                    if (i!=AIB_Server.KeyPairs.size()-1){
                        ps.write(",".getBytes());
                    }
                }
            }
            if (request.startsWith("/getComPubKey")){
                ps.write(AIB_Server.comKeyPair.modulus.toString(16).getBytes());
            }
            if (request.startsWith("/tx")){
                ps.write(AIB_Server.processTx(request.substring(3,request.length())).getBytes());
            }
            System.out.println(request);
            ps.close();
            is.close();
        } catch (Exception ex) {
            Logger.getLogger(RequestThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
    }
    
}
