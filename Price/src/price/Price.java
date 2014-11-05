/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package price;

import java.awt.Graphics;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.json.*;

/**
 *
 * @author leijurv
 */
public class Price {
    static final String bitstamp="https://www.bitstamp.net/api/order_book/";
    static final String bitfinex="https://api.bitfinex.com/v1/book/BTCUSD";
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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        System.out.println("Lol");
        JSONObject a=meow();
        System.out.println(System.currentTimeMillis()/1000);
        JSONArray bids=a.getJSONArray("bids");
        JSONArray asks=a.getJSONArray("asks");
        for (int i=0; i<10; i++){
            System.out.print(getPrice(getPrice(i,bids)+""));
            System.out.print("   ");
            System.out.println(getPrice(getPrice(i,asks)+""));
        }
        JFrame frame=new JFrame("order book");
        frame.setSize(2000,2000);
        frame.setContentPane(new JComponent(){
            public void paintComponent(Graphics g){
                int centX=frame.getWidth()/2;
                int centY=frame.getHeight()-100;
                
                
                float center=getPrice(0,bids)+getPrice(0,asks);
                center/=2;
                
                float mult=10;
                draw(g,bids,centX,centY,center,mult);
                draw(g,asks,centX,centY,center,mult);
                for (int i=-10; i<10; i++){
                    g.drawString((center+i)+"",(int) (centX+i*mult),centY);
                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }
    static final boolean BitFin=false;
    public static void draw(Graphics g,JSONArray bids,int centX,int centY,float center,float mult){
        int prevX=-1;
                int prevY=-1;
                double amount=0;
                
                for (int i=0; i<bids.length(); i++){
                    float price=getPrice(i,bids);
                    double amt=getAmount(i,bids);
                    amount+=amt;
                    //System.out.println(price+","+amount);
                    int x=centX-(int)((center-price)*mult);
                    int y=centY-(int)amount/10;
                    if (prevX!=-1){
                        g.drawLine(prevX,prevY,x,y);
                    }
                    prevX=x;
                    prevY=y;
                }
    }
    public static float getPrice(int ind, JSONArray bids){
        if (BitFin){
            return Float.parseFloat(bids.getJSONObject(ind).getString("price"));
        }
        return Float.parseFloat(bids.getJSONArray(ind).getString(0));
    }
    public static double getAmount(int ind, JSONArray bids){
        if (BitFin){
            return Double.parseDouble(bids.getJSONObject(ind).getString("amount"));
        }
        return Double.parseDouble(bids.getJSONArray(ind).getString(1));
    }
    public static String getPrice(String res){
        if (res.length()==3){
            res=res+".00";
        }
        if (res.length()==5){
            res=res+"0";
        }
        return res;
    }
    public static JSONObject meow() throws Exception{
        //URL url = new URL(bitfinex);
       // URLConnection con = url.openConnection();
//Reader r = new InputStreamReader(con.getInputStream());
        String s=load(BitFin?bitfinex:bitstamp);
        System.out.println(s);
        JSONObject a=new JSONObject(new JSONTokener(s));
        System.out.println(a);
        return a;
    }
    
}
