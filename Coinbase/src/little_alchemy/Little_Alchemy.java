/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package little_alchemy;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author leijurv
 */
public class Little_Alchemy{
public static String load(String webpage) throws Exception{
        Reader r=load1(webpage);
StringBuilder buf = new StringBuilder();
while (true) {
  int ch = r.read();
  if (ch < 0)
    break;
  buf.append((char) ch);
}
return buf.toString();
    }
public static Reader load1(String webpage) throws Exception{
    URL url = new URL(webpage);
URLConnection con = url.openConnection();
Reader r = new InputStreamReader(con.getInputStream());
return r;
}
public static void update() throws Exception{
    JSONObject o=new JSONObject(load("https://www.bitstamp.net/api/ticker/"));
    float Bbuy=Float.parseFloat(o.getString("ask"));
    float Bsell=Float.parseFloat(o.getString("bid"));
    float Cbuy=Float.parseFloat(new JSONObject(load("https://coinbase.com/api/v1/prices/buy")).getJSONObject("subtotal").getString("amount"));
    float Csell=Float.parseFloat(new JSONObject(load("https://coinbase.com/api/v1/prices/sell")).getJSONObject("subtotal").getString("amount"));
    System.out.println("Bitstamp: can Buy at "+Bbuy+"   can Sell at "+Bsell);
    System.out.println("Coinbase: can Buy at "+Cbuy+"   can Sell at "+Csell);
    float amt=Float.parseFloat(new JSONObject(load("https://coinbase.com/api/v1/prices/sell")).getString("amount"));
    System.out.println("Selling 1 BTC on Coinbase would give you "+amt);
}
public static String getW(int I){
    String r="";
    for (int i=4; i<base.length(); i++){
    JSONArray p=base.getJSONArray(i);
    
            //System.out.println(p);
            for (int n=0; n<p.length(); n++){
                int a=Integer.parseInt(p.getJSONArray(n).getString(0));
                int b=Integer.parseInt(p.getJSONArray(n).getString(1));
                if (a==I || b==I){
                    r=r+(names.getString(i)+" can be created by combining "+names.getString(a)+" and "+names.getString(b));
                    r=r+"\n";
                }
            }
    }
            return r;
}
public static String getPos(int i){
    JSONArray p=base.getJSONArray(i);
    String r="";
            //System.out.println(p);
            for (int n=0; n<p.length(); n++){
                int a=Integer.parseInt(p.getJSONArray(n).getString(0));
                int b=Integer.parseInt(p.getJSONArray(n).getString(1));
                if (a!=999 && b!=999){
                    r=r+(names.getString(i)+" can be created by combining "+names.getString(a)+" and "+names.getString(b));
                    r=r+"\n";
                }else{
                    r=r+(names.getString(i)+" cannot be created");
                    r=r+"\n";
                }
            }
            return r;
}
public static void printPos(int i){
    System.out.println(getPos(i));
}
public static int getElement(){
    String s=JOptionPane.showInputDialog("What element?");
                int n=-1;
                for (int i=0; i<names.length(); i++){
                    if (names.getString(i).equalsIgnoreCase(s)){
                        n=i;
                    }
                }
                return n;
}
static JSONArray names;
static JSONArray base;
static JComponent M;
static String message;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        names=new JSONObject(new JSONTokener(load1("http://littlealchemy.com/base/names.json"))).getJSONArray("lang");
        base=new JSONObject(load("http://littlealchemy.com/base/base.json").replace("false","0")).getJSONArray("base");
       //System.out.println(names);
       //System.out.println(base);
        //for (int i=4; i<base.length(); i++){
         //   printPos(i);
        //}
        JFrame frame=new JFrame("Little Alchemy");
        
        message="";
        M=new JComponent(){
            public void paintComponent(Graphics g){
                String[] r=message.split("\n");
                for (int i=0; i<r.length; i++){
                    g.drawString(r[i],10,10+i*15);
                }
            }
        };
        frame.setContentPane(M);
        frame.setLayout(new FlowLayout());
        JButton how=new JButton("How do you make?");
        how.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int n=getElement();
                if (n==-1){
                    message="Element not found";
                }else{
                    message=getPos(n);
                }
                M.repaint();
            }
            
        });
        frame.add(how);
        JButton what=new JButton("What can you make with?");
        what.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int n=getElement();
                if (n==-1){
                    message="Element not found";
                }else{
                    message=getW(n);
                }
                M.repaint();
            }
            
        });
        frame.add(what);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000,2000);
	frame.setVisible(true);
        //update();
        // TODO code application logic here
    }
    
}
