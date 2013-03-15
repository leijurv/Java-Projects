package flood;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Flood {

    public static void main(String[] args) throws Exception {
        for (int i=1;i<=10;i++){
        try {
            URL url = new URL("http://www.polleverywhere.com/free_text_polls/MTY0MTE5MTc5/capture");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
            String content = "message[content]=Hexagons";
            printout.writeBytes(content);
            printout.flush();
            printout.close();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8192);
            StringBuilder sb = new StringBuilder("");
            String line = "";
            String newLine = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(newLine);
            }
            in.close();
            System.out.println(i);
        } catch (Exception e) {
        }
        }
    }
}