/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package getter;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author leif
 */
public class Getter extends Thread{

    /**
     * @param args the command line arguments
     */
    static ArrayList<String> news=new ArrayList<String>();
    static ArrayList<String> already=new ArrayList<String>();
    static ArrayList<Getter> Threads=new ArrayList<Getter>();
    static int i=0;
    static final Object iSync=new Object();
    static screenUpdater su=new screenUpdater();
    public static void main(String[] args){
        news.add(JOptionPane.showInputDialog("What is the starting article?"));
        System.out.println(i+":"+news.get(0)+","+news.size());
        i++;
        loop();
        startThreads();
        
        JFrame frame=new JFrame("Wikipedia Crawler");
	  s.setFocusable(true);
	  (frame).setContentPane(s);
	  frame.setLayout(new FlowLayout());
	  frame.setSize(1000,700);
	  //frame.setUndecorated(true);
     frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	  frame.setVisible(true);
	  frame.addWindowListener(new WindowAdapter(){
	  public void windowClosing(WindowEvent e){
		System.exit(0);
	  }
          
          
	  });
          su.start();
    }
    public static class screenUpdater extends Thread{
        boolean running1=true;
        public void run(){
            while(running1){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    
                }
                s.repaint();
            }
        }
    }
    public static void startThreads(){
        threads+=16;
        (new updateThread()).start();
    }
    static String recent="";
    static screen s=new screen();
    static int threads=0;
    public static class screen extends JComponent implements ActionListener{
        public screen(){
            //Add some buttons, like "add thread" "remove thread" "save progress"(Like the already list and news even if not > 20000) "stop all threads" "start all threads" and stuff like that
            createButtons();
        }
        public void createButtons(){
		JButton b1=new JButton("Start");
		JButton b2=new JButton("Stop");
		JButton b3=new JButton("Add Thread");
		JButton b4=new JButton("Remove Thread");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
	    b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
	    b2.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b2.setHorizontalTextPosition(AbstractButton.CENTER);
	    b3.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b3.setHorizontalTextPosition(AbstractButton.CENTER);
	    b4.setVerticalTextPosition(AbstractButton.BOTTOM);
	    b4.setHorizontalTextPosition(AbstractButton.CENTER);
	    b1.setActionCommand("start");
	    b2.setActionCommand("stop");
	    b2.addActionListener(this);
	    b3.setActionCommand("at");
	    b3.addActionListener(this);
	    b1.addActionListener(this);
	    b4.setActionCommand("rt");
	    b4.addActionListener(this);
	    add(b1);
	    add(b2);
	    add(b3);
	    add(b4);
	}
        public void paintComponent(Graphics g){
            synchronized(iSync){
                g.drawString("So far:"+i,10,10);
            }
            synchronized(news){
                g.drawString("Current article:"+news.get(0),10,25);
                g.drawString("Current loaded links:"+news.size(), 10, 40);
            }
            g.drawString("Threads:"+threads,10,55);
                if (saving){
                    g.drawString("Saving last 10000 links...", 10, 70);
                }
            
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if ("start".equals(e.getActionCommand())){
                threads=16;
                
            }
            if ("stop".equals(e.getActionCommand())){
                stopThreads();
            }
            if ("at".equals(e.getActionCommand())){
                threads++;
                
            }
                if ("rt".equals(e.getActionCommand())){
                    threads--;
            }
                (new updateThread()).start();
        }
    }
    static final int maxThreads=20;
    static final Object saveObject=new Object();
    public static class updateThread extends Thread{
        public void run(){
            if (threads>maxThreads){
                    threads=maxThreads;
                }
            for (int i=0; i<Threads.size(); i++){
                if (!Threads.get(i).running){
                    Threads.remove(i);
                }
            }
            for (int i=0; i<Threads.size(); i++){
                Threads.get(i).id=i;
            }
            while (Threads.size()<threads){
                addThread();
            }
            int a=Threads.size();
            boolean x=(Threads.size()>threads);
            while(a>threads){
                Threads.get(0).running=false;
                a--;
            }
            if (x){
                (new updateThread()).start();
            }
        }
    }
    public static void addThread(){
        Getter g=new Getter(Threads.size());
        g.start();
        Threads.add(g);
    }
    boolean running=true;
    static int delay=500;
    public void halt(){
        running=false;
    }
    public static boolean isE(){
        synchronized(news){
            return news.isEmpty();
        }
    }
    public static void read(){
        int i=0;
        File f;
        do{
            f=new File("/Users/leif/Desktop/Wikipedia/save"+i+".txt");
            i++;
        }while(f.exists());
        String name="/Users/leif/Desktop/Wikipedia/save"+(i-2)+".txt";
        ArrayList<String> newq=gets(name);
        for (String n : newq){
            news.add(n);
        }
    }
    public static void save(){
        int i=0;
        File f;
        do{
            f=new File("/Users/leif/Desktop/Wikipedia/save"+i+".txt");
            i++;
        }while(f.exists());
        String name="/Users/leif/Desktop/Wikipedia/save"+(i-1)+".txt";
        ArrayList<String> subset=new ArrayList<String>();
        for (i=0; i<10000; i++){
            subset.add(new String(news.remove(news.size()-1)));
            
        }
        write(name,subset);
    }
    public Getter(int ID){
        id=ID;
    }
    public static void stopThreads(){
        
        threads=0;
        synchronized(Threads){
                        for (Getter q : Threads){
                            q.running=false;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
        }
        Threads.clear();
        
    }
    int id;
    static boolean abo=false;
    static boolean saving=false;
    public void run(){
        while(running){
            
            synchronized(news){
                if (news.size()>20000){
                    
                    synchronized(saveObject){
                        saving=true;
                    }
                    s.repaint();
                    //Move last 10000 into file on hard drive
                    stopThreads();
                        save();
                        abo=true;
                    startThreads();
                    synchronized(saveObject){
                        saving=false;
                    }
                    
                }
                if (news.size()<500 && abo){
                    synchronized(saveObject){
                        saving=true;
                    }
                    s.repaint();
                    stopThreads();
                    read();
                    startThreads();
                    synchronized(saveObject){
                        saving=false;
                    }
                }
                if (news.isEmpty()){
                    return;
                }
                news.set(0,news.get(0).replace("&amp;","%26"));
            }
            synchronized(iSync){
                recent=i+":"+news.get(0)+","+news.size();
                System.out.println(recent);
                i++;
                
         
        }
            loop();
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
               running=false;
            }
        }
    }
    public static void loop(){
        String current="";
        synchronized(news){
            current=news.remove(0);
        }
                
        synchronized(already){
            if (already.contains(current.toLowerCase())){
                return;
            }
            already.add(current.toLowerCase());
        }
        String content=get(current);
                
        ArrayList<String> newS=process(content);
                
        for (String n: newS){
            synchronized(news){
                if (!news.contains(n)){
                    news.add(n);
                }
            }
        }
        write("/Users/leif/Desktop/Wikipedia/"+current+".txt",gets(current));
    }
    public static String read(String filename){
        try{
  // Open the file that is the first 
  // command line parameter
  FileInputStream fstream = new FileInputStream(filename);
  // Get the object of DataInputStream
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  //Read File Line By Line
  while ((strLine = br.readLine()) != null)   {
  // Print the content on the console
  return strLine;
  }
  //Close the input stream
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
        return "";
    }
    public static void write(String filemane, String content){
        try{
    FileWriter fstream = new FileWriter(filemane);
  BufferedWriter out = new BufferedWriter(fstream);
  out.write(content);
  //Close the output stream
  out.close();
  }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
}
    public static void write(String filemane, ArrayList<String> content){
        try{
    FileWriter fstream = new FileWriter(filemane);
  BufferedWriter out = new BufferedWriter(fstream);
  for (String c : content){
      out.write(c);
      out.write("\n");
  }
  //Close the output stream
  out.close();
  }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
}
    public static ArrayList<String> process(String content){
        ArrayList<String> res=new ArrayList<String>();
        String n=new String(content);
        int q=0;
        do{
            if (n.indexOf("[[")>n.indexOf("]]")){
            }else{
                String t="";
                try{
            t=n.substring(n.indexOf("[[")+2,n.indexOf("]]"));
                }catch (Exception e){
                }
                boolean add=true;
            if (t.indexOf(":")!=-1){
                String a=t.split(":")[0];
                if (a.equals("File") || a.equals("Category") || a.equals("Photo") || a.equals("Talk") || a.equals("Wikipedia") || a.equals("Image")){
                    add=false;
                }
                if (a.length()<4){
                    add=false;
                }
                
                
            }
            if (add){
            res.add(t.split("\\|")[0]);
            }
            }
            try{
            n=n.substring(n.indexOf("]]")+2,n.length());
            }catch(Exception e){
            }

        }while(n.indexOf("[[")!=-1 && n.indexOf("]]")!=-1 && n.length()>5);
        return res;
    }
    static final String qq="<rev xml:space=" +'"'+"preserve"+'"'+">";
    public static ArrayList<String> gets(String name){
        ArrayList<String> x=new ArrayList<String>();
        try{
        
        URL url = new URL( "http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=xml&titles="
                + name.replace(' ', '+') );
        InputStream is = url.openConnection().getInputStream();

        BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );

        String line = null;
        while( ( line = reader.readLine() ) != null )  {
                x.add(line);
                
        }
        String a=x.remove(0);
        
        int b=a.indexOf(qq);
        x.add(a.substring(b+qq.length(),a.length()));
        reader.close();
        }catch(Exception e){
            
        }
        return x;
    }
    public static String get(String name){
        ArrayList<String> x=gets(name);
        String res="";
        for (String n : x){
            res=res+n;
        }
        return res;
    }
}
