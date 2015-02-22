/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leijurv
 */
public class Proxy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        ServerSocket server=new ServerSocket(24135);
        String result="198.35.26.96";
        new Thread(){
            public void run(){
                while(true){
                    try{
                        Socket s=server.accept();
                        System.out.println("conn");;
                        InputStream in=s.getInputStream();
                        OutputStream out=s.getOutputStream();
                        new Thread(){
                            public void run(){
                                try{
                                    
                                    Socket S=new Socket(result,80);
                                    InputStream IN=S.getInputStream();
                                    OutputStream OUT=S.getOutputStream();
                                    System.out.println("starting");;
                                    new Thread(){
                                        public void run(){
                                            redirect(in,OUT);
                                        }
                                    }.start();
                                    redirect(IN,out);
                                    System.out.println("DONE");
                                    S.close();
                                    s.close();
                                } catch (IOException ex){
                                    System.out.println(ex);
                                }
                            }
                        }.start();
                        
                    } catch (IOException ex){
                        System.out.println(ex);
                    }
                }
            }
        }.start();
        // TODO code application logic here
    }
    public static void redirect(InputStream in, OutputStream out){
        byte[] b=new byte[65536];
        ByteArrayOutputStream cat=new ByteArrayOutputStream();
        while(true){
            try{
                
                int j=in.read(b);
                System.out.println(j);
                if(j==-1){
                    System.out.println(new String(cat.toByteArray()));
                    return;
                }
                out.write(b,0,j);
                cat.write(b,0,j);
            } catch (IOException ex){
                System.out.println(ex);
                return;
            }
            
        }
    }
    
}
