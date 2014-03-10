/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cryptsy;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author leijurv
 */
public class Cryptsy extends JComponent{
    
    //A 3 BTC LTC
    //B 135 LTC DOGE
    //C 132 DOGE BTC
    static Cryptsy M=new Cryptsy();
    static JSONArray[] sells=new JSONArray[3];
    static JSONArray[] buys=new JSONArray[3];
    static boolean[] stable=new boolean[3];
    static String[][] names={{"LTC","BTC"},{"DOGE","LTC"},{"DOGE","BTC"}};
    static double ratio=0;
    public void paintComponent(Graphics g){
        g.drawString("Ratio: "+ratio,10,500);
        synchronized(sells){
        for (int i=0; i<sells.length; i++){
            if (sells[i]!=null){
                if (!stable[i]){
                    g.setColor(Color.RED);
                    g.fillRect(10,i*60+15,150,50);
                    g.setColor(Color.BLACK);
                }
                g.drawString("Buy 1 "+names[i][0]+" for "+sells[i].getJSONArray(0).getString(0)+" "+names[i][1],10,i*60+15);
                for (int j=0; j<2; j++){
                    g.drawString(sells[i].getJSONArray(j).getString(0),10,i*60+j*15+30);
                    g.drawString(sells[i].getJSONArray(j).getString(2),130,i*60+j*15+30);
                }
            }
        }
        }
        synchronized(buys){
        for (int i=0; i<buys.length; i++){
            if (buys[i]!=null){
                g.drawString("Sell 1 "+names[i][0]+" for "+buys[i].getJSONArray(0).getString(0)+" "+names[i][1],400,i*60+15);
                for (int j=0; j<2; j++){
                    g.drawString(buys[i].getJSONArray(j).getString(0),400,i*60+j*15+30);
                    g.drawString(buys[i].getJSONArray(j).getString(2),520,i*60+j*15+30);
                }
            }
        }
        }
        
    }
    public static double update(int market, boolean buy){
        try {
                        String page = "https://www.cryptsy.com/json.php?file=ajax"+(buy?"buy":"sell")+"orderslistv2_"+market+".json";
                        System.out.println(page);
                        URL url = new URL(page);
                        URLConnection con = url.openConnection();
                        InputStreamReader r = new InputStreamReader(con.getInputStream());
                        JSONTokener t = new JSONTokener(r);
                        JSONObject main = (JSONObject) t.nextValue();
                        
                        JSONArray sell = main.getJSONArray("aaData");
                        int i=0;
                        if (market==132){
                            i=2;
                        }
                        if (market==135){
                            i=1;
                        }
                        if (buy){
                            buys[i]=sell;
                        }else{
                            sells[i]=sell;
                        }
                        M.repaint();
                        System.out.println("Depth "+sell.getJSONArray(0).getString(2));
                        //TODO: Make sure that there's enough depth to execute orderz!
                        return Double.parseDouble(sell.getJSONArray(0).getString(0));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
        return 0;
    }
    public static String connect(String params) throws Exception{
        params="nonce="+new Date().getTime()+"&"+params;
SecretKeySpec keySpec = new SecretKeySpec("c2b27fd4c1897814288617dddb0eed58511739fc0a1c2a03b37604753c277c74993e54e6cf16f855".getBytes(),"HmacSHA512");

Mac mac = Mac.getInstance("HmacSHA512");
mac.init(keySpec);
mac.update(params.getBytes());
byte[] result = mac.doFinal();
        URL url = new URL("https://api.cryptsy.com/api"); 
        System.out.println(params);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();           
con.setDoOutput(true);
con.setDoInput(true);
con.setInstanceFollowRedirects(false); 
con.setRequestMethod("POST"); 

con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
con.setRequestProperty("Key","151b77b004fbe86bd55142ce2320187ececb73ac");
con.setRequestProperty("Sign",Hex.encodeHexString(result));

//con.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
//^^ I don't know if you have to use that one.


con.setUseCaches (false);

DataOutputStream wr = new DataOutputStream(con.getOutputStream ());
wr.writeBytes(params);
wr.flush();
wr.close();
InputStreamReader r = new InputStreamReader(con.getInputStream());
//con.disconnect();
StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}

r.close();
System.out.println(buf.toString());
JSONTokener t=new JSONTokener(buf.toString());
JSONObject o=(JSONObject)t.nextValue();
if (!o.getString("success").equals("1")){
    System.out.println("Order ERROR");
    Runtime.getRuntime().exec("say 'order error, shutting down'");
    new Thread(){
        public void run(){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cryptsy.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);//Make sure it shuts down. *shudder* Once it didn't. I thought it had.
        }
    }.start();
    throw new RuntimeException("Order error");
}
return buf.toString();
    }
    public static boolean checkOrders(int marketid)throws Exception{
        //Check if there are any open orders ona  certain market
        String r=connect("method=myorders&marketid="+marketid);
        JSONTokener t=new JSONTokener(r);
        JSONObject o=(JSONObject)t.nextValue();
        JSONArray retu=o.getJSONArray("return");
        return retu.length()==0;
    }
    public static void wait(int marketid)throws Exception{
        //Wait until order is fufulled
        Thread.sleep(2000);
        long time=System.currentTimeMillis();
        while(!checkOrders(marketid) && System.currentTimeMillis()<time+20000){
            Thread.sleep(2000);
        }
        if (checkOrders(marketid)){
            return;
        }
        Runtime.getRuntime().exec("say 'persisting open order on market "+marketid+", shutting down'");
        //This happens when the order that this one was planning to fufill was cancelled or didn't have enough depth
        throw new RuntimeException("Open order");
    }
    public static double getBal(String curID) throws Exception{
        String r=connect("method=getinfo");
        JSONObject main=(JSONObject)(new JSONTokener(r).nextValue());
        JSONObject ret=main.getJSONObject("return");
        JSONObject bal=ret.getJSONObject("balances_available");
        return Double.parseDouble(bal.getString(curID));
    }
    public static void main(String[] args) throws Exception{
JFrame frame=new JFrame("Cryptsy Arbitrage");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(M);
        frame.setVisible(true);
        for (int i=0; i<3; i++){
                stable[i]=true;
            }
        Scanner scan=new Scanner(System.in);
        while ( true){
        double Buy132=update(132,false);
        System.out.println("Buy 1 Doge for "+Buy132+" BTC");
        double Sell132=update(132,true);
        System.out.println("Sell 1 Doge for "+Sell132+" BTC");
        double Buy3=update(3,false);
        System.out.println("Buy 1 LTC for "+Buy3+" BTC");
        double Sell3=update(3,true);
        System.out.println("Sell 1 LTC for "+Sell3+" BTC");
        double Buy135=update(135,false);
        System.out.println("Buy 1 Doge for "+Buy135+" LTC");
        double Sell135=update(135,true);
        System.out.println("Sell 1 Doge for "+Sell135+" LTC");

        if (Buy132<=Sell132 || Buy3<=Sell3 || Buy135<=Sell135){
            
            if (Buy132<=Sell132){
                if (stable[2]){
                    Runtime.getRuntime().exec("say 'unstable market state 132'");
                }
                stable[2]=false;
            }
            if (Buy3<=Sell3){
                if (stable[0]){
                    Runtime.getRuntime().exec("say 'unstable market state 3'");
                }
                stable[0]=false;
            }
            if (Buy135<=Sell135){
                if (stable[1]){
                    Runtime.getRuntime().exec("say 'unstable market state 135'");
                }
                stable[1]=false;
            }
            for (int i=0; i<3; i++){
                stable[i]=true;
            }
            if (Buy132<=Sell132){
                stable[2]=false;
            }
            if (Buy3<=Sell3){
                stable[0]=false;
            }
            if (Buy135<=Sell135){
                stable[1]=false;
            }
            
            M.repaint();
            
        }else{
            for (int i=0; i<3; i++){
                stable[i]=true;
            }
        //System.out.println(1+"BTC "+1/(Buy132*1.002)+" DOGE "+1/(Buy132*1.002*1.002)*Sell135+" LTC "+1/(Buy132*1.002*1.002*1.002)*Sell135*Sell3+" BTC");
        System.out.println(1+"BTC "+1/(Buy3*1.002)+" LTC "+1/(Buy3*Buy135*1.002*1.002)+" DOGE "+1/(Buy3*Buy135*1.002*1.002*1.002)*Sell132+" BTC");
        ratio=1/(Buy3*Buy135*1.002*1.002*1.002)*Sell132;
        //System.out.println(1+"BTC "+1/(Buy3)+" LTC "+1/(Buy3*Buy135)+" DOGE "+1/(Buy3*Buy135)*Sell132+" BTC");
        if (1/(Buy3*Buy135*1.002*1.002*1.002)*Sell132>1.001){
            Runtime.getRuntime().exec("say 'arbitrage opportunity'");
            scan.nextLine();
            arb(Buy135,Sell132,Buy3);
        }
        }
        Thread.sleep(2000);
        }
    }
    public static void arb(double Buy135, double Sell132,double Buy3)throws Exception{
        connect("method=createorder&marketid=3&ordertype=Buy&quantity=0.01&price="+new BigDecimal(Buy3));
            
wait(3);
Thread.sleep(500);
double LTCBal=getBal("LTC");//This really should be 0.01
double amt=LTCBal/(1.002*Buy135);//Amount of Doge
            
System.out.println(amt);
System.out.println(amt*Sell132/1.002);
System.out.println(0.01*Buy3*1.002);
//scan.nextLine();
connect("method=createorder&marketid=135&ordertype=Buy&quantity="+amt+"&price="+new BigDecimal(Buy135));
wait(135);
Thread.sleep(500);
//scan.nextLine();
connect("method=createorder&marketid=132&ordertype=Sell&quantity="+amt+"&price="+new BigDecimal(Sell132));
    }
            
}
