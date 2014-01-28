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
            if (request.startsWith("/getbal/")){
                //try{
                ps.write(AIB_Server.processGetBal(request.substring(8,request.length())).getBytes());
                //}catch(Exception e){
                 //   ps.write(("0").getBytes());
                //}
                }
            if (request.startsWith("/getaddr/")){
                ps.write(AIB_Server.processGetAddr(request.substring(9,request.length())).getBytes());
            }
            if (request.startsWith("/sendtx/")){
                ps.write(AIB_Server.processTx(request.substring(8,request.length())).getBytes());
            }
            //TODO: GetTX, Withdraw, List
            System.out.println("R"+request);
            ps.close();
            is.close();
        } catch (Exception ex) {
            Logger.getLogger(RequestThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
    }
    
}
