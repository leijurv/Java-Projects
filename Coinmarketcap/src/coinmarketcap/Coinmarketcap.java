/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coinmarketcap;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author leijurv
 */
public class Coinmarketcap extends JComponent{
    static JFrame frame;
    public static Coinmarketcap M=new Coinmarketcap();
    static JSONArray markets;
    static HashMap<String,BigInteger> premine=new HashMap<String,BigInteger>();
    public void paintComponent(Graphics g){
        int offset=25;
        g.drawString("#",10,10);
        g.drawString("Name",30,10);
        g.drawString("Market Cap",80,10);
        g.drawString("Total Supply",200,10);
        g.drawString("Real Market Cap",350,10);
        g.drawString("Real Total Supply",470,10);
        for (int i=0; i<markets.length(); i++){
            JSONObject coin=markets.getJSONObject(i);
            g.drawString(coin.getString("position"),10,offset+15*i);
            g.drawString(coin.getString("id").toUpperCase(),30,offset+15*i);
            g.drawString("$"+coin.getString("marketCap"),80,offset+15*i);
            g.drawString(coin.getString("totalSupply"),200,offset+15*i);
            BigInteger Premine=BigInteger.ZERO;
            if (premine.get(coin.getString("id"))!=null){
                Premine=premine.get(coin.getString("id"));
            }
                
                BigDecimal supply=new BigDecimal(coin.getString("totalSupply").split(" ")[0].replace(",","")).subtract(new BigDecimal(Premine));
            BigDecimal price=new BigDecimal(coin.getString("price").replace(",",""));
            String s=price.multiply(supply).toString();
            BigInteger marketCap=new BigInteger(s.split("\\.")[0]);
            
            g.drawString("$"+marketCap.toString(),350,offset+15*i);
            g.drawString(supply.toString(),470,offset+15*i);
            if (premine.get(coin.getString("id"))!=null){
            g.setColor(Color.RED);
                g.drawLine(580,offset+15*i-6,630,offset+15*i-6);
                int pos=getPos(marketCap);
                System.out.println(pos);
                
                int y=offset+15*pos+3;
                int x=533;
                g.drawLine(x,y,630,y);
                g.drawLine(630,y,630,offset+15*i-6);
                g.drawLine(x,y,x+5,y-5);
                g.drawLine(x,y,x+5,y+5);
                g.setColor(Color.BLACK);
                        }
        }
    }
    public static int getPos(BigInteger mc){
        for (int i=0; i<markets.length()-1; i++){
            BigInteger pos=new BigInteger(markets.getJSONObject(i).getString("marketCap").replace(",",""));
            BigInteger next=new BigInteger(markets.getJSONObject(i+1).getString("marketCap").replace(",",""));
            if (next.compareTo(mc)==-1 && mc.compareTo(pos)==-1){
                return i;
            }
        }
        return 0;
    }
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

r.close();
return buf.toString();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        premine.put("aur",new BigInteger("10500000"));
        JSONTokener t=new JSONTokener(load("http://coinmarketcap.northpole.ro/api/all.json"));
        JSONObject j=(JSONObject)(t.nextValue());
        markets=j.getJSONArray("markets");
        frame=new JFrame("Market Caps");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setContentPane(M);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        System.out.println("DONE");
    }
    
}
