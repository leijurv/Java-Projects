/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dropbox;

//import com.dropbox.sync.android.DbxAccountManager;

import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;


/**
 *
 * @author leijurv
 */
public class Dropbox {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        String key="ulypc0tg020vef1";
        String secret="mbw9jc2npwxrfz2";
        DbxAppInfo appInfo = new DbxAppInfo(key, secret);

        DbxRequestConfig config = new DbxRequestConfig(
            "JavaTutorial/1.0", Locale.getDefault().toString());
        /*
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        String authorizeUrl = webAuth.start();
System.out.println("1. Go to: " + authorizeUrl);
System.out.println("2. Click \"Allow\" (you might have to log in first)");
System.out.println("3. Copy the authorization code.");
String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
DbxAuthFinish authFinish = webAuth.finish(code);
String accessToken = authFinish.accessToken;*/
        String accessToken="XMpQv0rtEg4AAAAAAAAEuGkat72JLR2jeshOYf4zdnPQVAjxePdaRJMLy98dh2D9";
DbxClient client = new DbxClient(config, accessToken);
System.out.println(client.getAccessToken());
System.out.println("Linked account: " + client.getAccountInfo().displayName);
ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
try {
    DbxEntry.File downloadedFile = client.getFile("/sphere.scad", null,
        outputStream);
    
    System.out.println("Metadata: " + downloadedFile.toString());
} finally {
    outputStream.close();
}
System.out.println(new String(outputStream.toByteArray()));
    }
    
}
