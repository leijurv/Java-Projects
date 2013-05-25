/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wordgame;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
/**
 *
 * @author leijurv
 */
public class WordGame {
static ArrayList<char[]> words = new ArrayList<char[]>();
    static char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void load(String path) throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            if (strLine.length()==5 && !strLine.contains("'")){
                words.add(strLine.toCharArray());
            }
        }
        in.close();
    }
    public static ArrayList<char[]> possibles(char[][] a, int[] b){
        ArrayList<char[]> res=new ArrayList<char[]>();
        for (char[] x : words){
            if (fitsAll(x,a,b)){
                res.add(x);
            }
        }
        return res;
    }
    public static boolean fitsAll(char[] a, char[][] b, int[] c){
        for (int i=0; i<b.length; i++){
            if (c[i]!=common(a,b[i])){
                return false;
            }
        }
        return true;
    }
    public static ArrayList<char[]> closest(char[] x){
        int dist=0;
        ArrayList<char[]> best=new ArrayList<char[]>();
        for (char[] n : words){
            int i=common(n,x);
            if (dist==i){
                best.add(n);
            }
            if (dist<i/* && i!=4*/){
                dist=i;
                best=new ArrayList<char[]>();
                best.add(n);
            }
            
        }
        return best;
    }
    public static int common(char[] a, char[] b){
        int[] A=count(a);
        int[] B=count(b);
        int r=0;
        for (int i=0; i<26; i++){
            r+=Math.min(A[i],B[i]);
        }
        return r;
    }
    public static int[] count(char[] a){
        int[] coun=new int[26];
        for (char n : a){
            coun[((int)n)-97]++;
        }
        return coun;
    }
    public static char[][] lets(ArrayList<char[]> xs){
        
        char[][] w=new char[5][xs.size()];
        for (int i=0; i<xs.size(); i++){
            for (int n=0; n<5; n++){
                w[n][i]=xs.get(i)[n];
            }
        }
        return w;
    }
    public static int[][] counts(ArrayList<char[]> xs){
        char[][] Q=lets(xs);
        int[][] x=new int[5][];
        for (int i=0; i<5; i++){
            x[i]=count(Q[i]);
        }
        return x;
    }
    public static int larg(int[] x){
        int lar=0;
        int p=-1;
        for (int i=0; i<x.length; i++){
            if (x[i]>=lar){
                lar=x[i];
                p=i;
            }
        }
        return p;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //load("/Users/leijurv/Downloads/mword10/SINGLE.TXT");
            load("/Users/leijurv/Dropbox/Java-Projects/Ghost/Ghostwords.txt");
            //Change to "/Users/USERNAME/Downloads/Java-Projects-master/Ghost/Ghostwords.txt" or whereever you downloaded it to.
        } catch (Exception e) {
            System.out.println("There was an error. Maybe you didn't change the path on line 56?");
            return;
        }
        char[] secret="warts".toCharArray();
        char[][] guesses={"fairy".toCharArray(),"areas".toCharArray(),"hurry".toCharArray(),"april".toCharArray(),"daisy".toCharArray(),"floor".toCharArray()};
        int[] answers={2,1,1,2,0,4};
        //char[][] guesses={"fairy".toCharArray(),"areas".toCharArray(),"arena".toCharArray(),"sandy".toCharArray(),"saris".toCharArray()};
        //int[] answers={2,3,2,2,3};
        ArrayList<char[]> xs=possibles(guesses,answers);
        for (char[] n : xs){
            System.out.println(n);
        }
        System.out.println(xs.size());
        int[][] count=counts(xs);
        char[] mostCom=new char[5];
        for (int i=0; i<5; i++){
            mostCom[i]=alpha[larg(count[i])];
            System.out.print((i+1)+(i==0?"st":(i==1?"nd":(i==2?"rd":"th"))));
            System.out.print(" letter: ");
            for (int n=0; n<26; n++){
                System.out.print(alpha[n]+":"+count[i][n]+(n!=25?",":""));
            }
            System.out.println();
        }
        for (int i=0; i<5; i++){
            System.out.print("Most common ");
            System.out.print((i+1)+(i==0?"st":(i==1?"nd":(i==2?"rd":"th"))));
            System.out.println(" letter is "+mostCom[i]);
        }
        System.out.println(mostCom);
        ArrayList<char[]> close=closest(mostCom);
        for (char[] n : close){
            System.out.print(n);
            System.out.print(",");
        }
        System.out.println(" is closest to "+new String(mostCom)+".");
        // TODO code application logic here
    }
}
