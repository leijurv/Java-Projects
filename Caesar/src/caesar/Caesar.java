/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package caesar;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author leijurv
 */
public class Caesar {
static ArrayList<String> words=new ArrayList<String>();
public static void read(){
    try{
  // Open the file that is the first 
  // command line parameter
  FileInputStream fstream = new FileInputStream("/Users/leijurv/Documents/words.txt");
  // Get the object of DataInputStream
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  //Read File Line By Line
  while ((strLine = br.readLine()) != null)   {
  // Print the content on the console
      words.add(strLine);
  }
  //Close the input stream
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        read();
        Scanner scan=new Scanner(System.in);
        System.out.print("What is the encoded message?  >");
        
        decode(scan.nextLine());
        // TODO code application logic here
    }
    public static void decode(String message){
        System.out.println("Original message: "+message);
        int maxwords=0;
        String massage="";
        int s=0;
        for (int i=0; i<26; i++){
            String current=shift(message,i);
            System.out.print("Using shift amount "+i+" gives "+current+".");
            String[] n=current.split(" ");
            int word=0;
            for (int x=0; x<n.length; x++){
                if (words.indexOf(n[x])!=-1){
                    word++;
                }
            }
            System.out.println("  "+word+" of those are English words.");
            if (word>maxwords){
                maxwords=word;
                massage=current;
                s=i;
            }
        }
        System.out.println("I think the message is "+massage+", using shift amount "+s);
    }
    public static char shift(char a, int b){
        char[] c={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        for (int i=0; i<c.length; i++){
            if (c[i]==a){
                return c[(i+b)%c.length];
            }
        }
        return a;
    }
    public static String shift(String a, int b){
        String s="";
        for (int i=0; i<a.length(); i++){
            s=s+shift(a.charAt(i),b);
        }
        return s;
    }
}
