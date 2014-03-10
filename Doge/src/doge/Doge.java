/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doge;

import java.awt.Color;
import java.awt.Graphics;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author leijurv
 */
public class Doge extends JComponent {

    static Doge M = new Doge();
    static JSONArray sells;
    static JSONArray buys; 
    static Date lastSell;
    static Date lastBuy;
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        
        if (sells != null) {
            double totalSell=0;
            g.drawString("Latest Sell Update: "+lastSell,10,10);
            for (int i = 0; i < 5; i++) {
                JSONArray j = sells.getJSONArray(i);
                g.drawString(j.getString(0), 10, i * 15 + 25);
                g.drawString(j.getString(2), 130, i * 15 + 25);
            }
            for (int i=0; i<sells.length(); i++){
                JSONArray j = sells.getJSONArray(i);
                totalSell+=Double.parseDouble(j.getString(2));
            }
            g.drawString("Total: "+totalSell,10,5*15+25);
        }
        if (buys != null) {
            double totalSell=0;
            g.drawString("Latest Buy Update: "+lastBuy,400,10);
            for (int i = 0; i < 5; i++) {
                JSONArray j = buys.getJSONArray(i);
                g.drawString(j.getString(0), 400, i * 15 + 25);
                g.drawString(j.getString(2), 520, i * 15 + 25);
            }
            for (int i=0; i<buys.length(); i++){
                JSONArray j = buys.getJSONArray(i);
                totalSell+=Double.parseDouble(j.getString(2));
            }
            g.drawString("Total: "+totalSell,400,5*15+25);
        }
        g.drawString("Current time: "+new Date(),600,25);
        g.setColor(Color.GREEN);
        g.drawLine(0,600,1000,600);
        g.drawLine(500,0,500,1000);
        g.setColor(Color.BLUE);
        if (buys!=null){
            int prevX=0;
            int prevY=0;
            double total=0;
            for (int i=0; i<10; i++){
                JSONArray j = buys.getJSONArray(i);
                int X=490-i*20;
                total+=Double.parseDouble(j.getString(2));
                int Y=(int)(600-total*5);
                if (i!=0){
                    g.drawLine(prevX,prevY,X,Y);
                }
                g.drawString(j.getString(0)+","+j.getString(2)+","+total,X-250,Y);
                prevX=X;
                prevY=Y;
            }
        }
        g.setColor(Color.RED);
        if (sells!=null){
            int prevX=0;
            int prevY=0;
            double total=0;
            for (int i=0; i<10; i++){
                JSONArray j = sells.getJSONArray(i);
                int X=510+i*20;
                total+=Double.parseDouble(j.getString(2));
                int Y=(int)(600-total*5);
                if (i!=0){
                    g.drawLine(prevX,prevY,X,Y);
                }
                g.drawString(j.getString(0)+","+j.getString(2)+","+total,X,Y);
                prevX=X;
                prevY=Y;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        String page = "https://www.cryptsy.com/json.php?file=ajaxsellorderslistv2_132.json";
                        URL url = new URL(page);
                        URLConnection con = url.openConnection();
                        InputStreamReader r = new InputStreamReader(con.getInputStream());
                        JSONTokener t = new JSONTokener(r);
                        JSONObject main = (JSONObject) t.nextValue();
                        sells = main.getJSONArray("aaData");
                        lastSell=new Date();
                        M.repaint();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        String page = "https://www.cryptsy.com/json.php?file=ajaxbuyorderslistv2_132.json";
                        URL url = new URL(page);
                        URLConnection con = url.openConnection();
                        InputStreamReader r = new InputStreamReader(con.getInputStream());
                        JSONTokener t = new JSONTokener(r);
                        JSONObject main = (JSONObject) t.nextValue();
                        buys = main.getJSONArray("aaData");
                        lastBuy=new Date();
                        M.repaint();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }.start();
        new Thread(){
            public void run(){
                while(true){
                    try{
                        Thread.sleep(100);
                    }catch(Exception yourmom){}
                    M.repaint();
                }
            }
        }.start();
        System.out.println(new Date());
        JFrame frame = new JFrame("Doge");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(M);
        frame.setVisible(true);
    }

}
