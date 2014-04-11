/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kraken;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author leijurv
 */
public class Kraken {
public static String connect(String params) throws Exception{
   String KURL="https://api.kraken.com/0/private/Balance";
        params=params+"nonce="+new Date().getTime()+"&otp=ccccccdcdvnirblrnktklghgbbrtvthfiuvjedjcdlch";
        String datatohash=KURL+(char)0+params;
SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64("vjItze7XZf43UHucTyiiNK+y/sGJNwGjdFn03yTzi1DuUJ7w9f89jJoBYxeMUhd+OCAUJVGdNPQL8ZXe69w5NQ=="),"HmacSHA512");

Mac mac = Mac.getInstance("HmacSHA512");
mac.init(keySpec);
mac.update(params.getBytes());
byte[] result = mac.doFinal();
        URL url = new URL(KURL); 
        System.out.println(params);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();           
con.setDoOutput(true);
con.setDoInput(true);
con.setInstanceFollowRedirects(false); 
con.setRequestMethod("POST"); 
con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 

System.out.println(Base64.encodeBase64String(Base64.decodeBase64("oVfraL/ShsnU7s73tlbEPxMV1azpwDbOul/IKBfnkZLHC+S9I/UILx6n")));
con.setRequestProperty("API-Key","Nh/fOZu6Rn9eailO0BUeEGyJ7neo5kKAgsmCmM4lCjJsE0pROjGKNVAs");
con.setRequestProperty("API-Sign",Base64.encodeBase64String(result));

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
return buf.toString();
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        connect("");
        // TODO code application logic here
    }
    
}
