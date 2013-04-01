/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mypwnagewebserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author leijurv
 */
public class MyPwnageWebServer {

    /**
     * @param args the command line arguments
     */
    public static  void listDirectory(File dir, PrintStream ps) throws IOException {
        ps.println("<HTML><HEAD>\n");
        ps.println("<TITLE>Directory listing</TITLE><P>\n");
        ps.println("</HEAD><BODY>");
        ps.println("<A HREF=\"..\">Parent Directory</A><BR>\n");
        String[] list = dir.list();
        for (int i = 0; list != null && i < list.length; i++) {
            File f = new File(dir, list[i]);
            if (f.isDirectory()) {
                ps.println("<A HREF=\""+list[i]+"/\">"+list[i]+"/</A><BR>");
            } else {
                ps.println("<A HREF=\""+list[i]+"\">"+list[i]+"</A><BR>");
            }
        }
        ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
        ps.println("</BODY></HTML>");
    }
    public static void sendFile(File targ, PrintStream ps,Socket s) throws IOException {
        InputStream is = null;
        ps.write('\n');
        if (targ.isDirectory()) {
            System.out.println("Listing directory: "+targ.getAbsolutePath());
            listDirectory(targ, ps);
            
            
            
            return;
        } else {
            
            if (!targ.exists()){
                if (targ.getPath().contains("favicon.ico")){
                    return;
                }
                //System.out.println("NO EXIST: "+targ.getAbsolutePath());
                   ps.println("Error 404. "+targ.getPath()+" not found");     
                return;
            }
            System.out.println("Showing file: "+targ.getAbsolutePath());
            is = new FileInputStream(targ.getPath());
        }

        try {
            
            
            int n;
            byte[] buf=new byte[is.available()];
            is.read(buf);
            ps.write(buf);
            /*
            while ((n = is.read(buf)) > 0) {
                System.out.println(n);
                ps.write(buf, 0, n);
            }*/
        } finally {
            is.close();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket S=new ServerSocket(5020);
        boolean running=true;
        while(running){
            Socket s=S.accept();
            PrintStream ps = new PrintStream(s.getOutputStream());
            InputStream is = new BufferedInputStream(s.getInputStream());
            byte[] b=new byte[is.available()];
            is.read(b);
            
            /*
            String r="";
            int i=4;
            while(i<b.length && (char)b[i]!='\n'){
                r=r+(char)b[i++];
            }
            int q=0;
            String x="";
            while(q<b.length-3){
                x=x+(char)b[q++];
            }
            String t="";
            int index=0;
            while(index<r.length() && r.toCharArray()[index]!=' '){
                t=t+r.toCharArray()[index++];
            }*/
            if (b.length>20){
            String x=new String(b);
            System.out.println("MEOW"+b.length+x);
            String t="";
            for (int i=4; x.charAt(i)!=' '; i++){
                t=t+x.charAt(i);
            }
            String fname = t.replace('/', File.separatorChar);
            fname=fname.replace("%20"," ");
            File targ = new File(fname);
            if (targ.isDirectory()) {
                File ind = new File(targ, "index.html");
                if (ind.exists()) {
                    targ = ind;
                }
            }
            sendFile(targ,ps,s);
            InetAddress ia=s.getInetAddress();
            String Ss="Request from: "+ia+"  "+ia.getHostName()+","+ia.getHostAddress()+" Local: "+ia.isSiteLocalAddress()+fname;
            
            ps.print(Ss);
            /*
            if (!x.contains("favicon.ico") && !x.equals("")){
            System.out.println(Ss);
            System.out.println();
            System.out.println();
            }*/
            }else{
                System.out.println("OH NO"+new String(b));
            }
            ps.write(("n").getBytes());
            ps.close();
            is.close();
            
            //running=false;
            
        }
        
    }
}
