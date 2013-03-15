/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ob;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Ob {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String r="";
        try{
            FileInputStream fstream = new FileInputStream("/Users/leijurv/Dropbox/LLS/src/LLS/lls.java");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                r=r+strLine;
                
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        r=r.replace("   "," ");
        r=r.replace("\n"," ");
        String alpha="abcdefghijklmnopqrstuvwxyz";
        String Alpha=alpha.toUpperCase();
        for (int i=1; i<r.length()-1; i++){
            if (r.substring(i,i+1).equals(" ")){
                if ((alpha.indexOf(r.substring(i-1,i))==-1 && Alpha.indexOf(r.substring(i-1,i))==-1) || (alpha.indexOf(r.substring(i+1,i+2))==-1 && Alpha.indexOf(r.substring(i+1,i+2))==-1)){
                    r=r.substring(0,i)+r.substring(i+1,r.length());
                    i--;
                }
            }
        }
        System.out.println(r);
        // TODO code application logic here
    }
}
