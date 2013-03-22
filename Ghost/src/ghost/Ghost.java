/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ghost;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author leijurv
 */
public class Ghost {
    static ArrayList<String> words=new ArrayList<String>();
    static char[] alpha={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
public static void load(String path){
     try{
  FileInputStream fstream = new FileInputStream(path);
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
  
  while ((strLine = br.readLine()) != null)   {
      words.add(strLine);
  }
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
}
public static ArrayList<String> startsWith(String start){
    ArrayList<String> result=new ArrayList<String>();
    for (String S : words){
        if (S.startsWith(start) && S.length()>3){
            result.add(S);
        }
    }
    return result;
}
public static int[] solve(String sofar){
    if ((words.contains(sofar) && sofar.length()>3) || startsWith(sofar).isEmpty()){
        return new int[] {sofar.length()%2==0?1:-1,-1};
    }
    if (sofar.length()<5){
        System.out.println(sofar);
    }
    for (int i=0; i<alpha.length; i++){
        int[] q=solve(sofar+alpha[i]);
        if (q[0]==(sofar.length()%2==0?1:-1)){
            return new int[] {q[0],i};
        }
    }
    return new int[] {sofar.length()%2==0?-1:1,-1};
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        load("/Users/leijurv/Dropbox/Java-Projects/Ghost/Ghostwords.txt");
        Scanner scan=new Scanner(System.in);
        System.out.print("What is it so far? >");
        String sofar=scan.nextLine();
        System.out.println("Searching...");
        int[] x=solve(sofar);
        System.out.println(startsWith(sofar));
        System.out.println(x[0]==1?"First player wins":"Second player wins");
        System.out.println(x[1]==-1?"":("Optimal move is "+alpha[x[1]]));
        
    }
}
