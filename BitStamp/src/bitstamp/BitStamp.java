/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bitstamp;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author leijurv
 */
public class BitStamp {
public static String load(String webpage) throws Exception{
        URL url = new URL(webpage);
URLConnection con = url.openConnection();
Reader r = new InputStreamReader(con.getInputStream());
StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}
return buf.toString();
    }
static JComponent M;
static JSONObject o;
static Image I;
static boolean updateallowed=false;
public static void update() throws Exception{
    if (updateallowed){
        updateallowed=false;
    URL u=new URL("http://bitcoincharts.com/charts/chart.png?width="+2*M.getWidth()/3+"&height="+2*M.getHeight()/3+"&m=bitstampUSD&SubmitButton=Draw&r=10&i=5-min&t=S");
                    I=ImageIO.read(u);
                    M.repaint();
                    updateallowed=true;
    }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        o=new JSONObject(load("https://www.bitstamp.net/api/order_book/"));
        new Thread(){
            public void run(){
                while (true){
                try{
                    M.repaint();
                    Thread.sleep(100);
                }catch(Exception e){}}
            }
        }.start();
        new Thread(){
            public void run(){
                while (true){
                try{
                    o=new JSONObject(load("ttps://www.bitstamp.net/api/order_book/"));
                    long time=1000L*Long.parseLong(o.getString("timestamp"));
                    long diff=(time+60000)-System.currentTimeMillis();
                    Thread.sleep(diff<1000?1000:diff);
                }catch(Exception e){}}
            }
        }.start();
        new Thread(){
            public void run(){
                while (true){
                try{
                    update();
                    Thread.sleep(30000);
                }catch(Exception e){System.out.println(e);}}
            }
        }.start();
        //
        JFrame frame=new JFrame("BitStamp");
        
        M=new JComponent(){
            public void paintComponent(Graphics g){
                g.setColor(Color.WHITE);
                g.fillRect(0,0,M.getWidth(),M.getHeight());
                g.setColor(Color.BLACK);
                if (I!=null)
                    g.drawImage(I,(M.getWidth()-I.getWidth(null))/2,(M.getHeight()-I.getHeight(null))/2,null);
                JSONArray buys=o.getJSONArray("bids");
                JSONArray sells=o.getJSONArray("asks");
                long time=1000L*Long.parseLong(o.getString("timestamp"));
                long realtime=System.currentTimeMillis();
                long diff=realtime-time;
                Date d=new Date(time);
                g.drawString("Date: "+d,10,10);
                g.drawString(diff/1000+" seconds ago",10,25);
                int x=M.getWidth()/2;
                int y=M.getHeight()-100;
                for (int i=0; i<5; i++){
                    JSONArray buy=buys.getJSONArray(i);
                    g.drawString("$"+buy.getString(0),x-300,y+15*i);
                    g.drawString(buy.getString(1),x-200,y+15*i);
                    g.drawString("$"+((float)((int)(100F*Float.parseFloat(buy.getString(0))*Float.parseFloat(buy.getString(1)))))/100,x-100,y+15*i);
                }
                for (int i=0; i<5; i++){
                    JSONArray buy=sells.getJSONArray(i);
                    g.drawString("$"+buy.getString(0),x+100,y+15*i);
                    g.drawString(buy.getString(1),x+200,y+15*i);
                    g.drawString("$"+((float)((int)(100F*Float.parseFloat(buy.getString(0))*Float.parseFloat(buy.getString(1)))))/100,x+300,y+15*i);
                }
                Font f=new Font("Verdana",Font.PLAIN,100);
                g.setFont(f);
                float p=Float.parseFloat(buys.getJSONArray(0).getString(0))+Float.parseFloat(sells.getJSONArray(0).getString(0));
                p/=2;
                p*=100;
                float price=(float)((int)p);
                price/=100F;
                if (diff>60000){
                    g.setColor(Color.RED);
                }
                g.drawString(""+price,x-130,100);
                
            }
        };
        frame.setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
	frame.setVisible(true);
        Thread.sleep(2000);
        updateallowed=true;
        update();
        // TODO code application logic here
    }
    
}
