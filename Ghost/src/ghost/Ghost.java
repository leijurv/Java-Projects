package ghost;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Ghost {

    static ArrayList<String> words = new ArrayList<String>();
    static char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void load(String path) throws Exception {
        DataInputStream in = new DataInputStream(new FileInputStream(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            words.add(strLine);
        }
        in.close();
    }

    public static ArrayList<String> startsWith(String start) {
        ArrayList<String> result = new ArrayList<String>();
        for (String S : words) {
            if (S.startsWith(start) && S.length() > 3) {
                result.add(S);
            }
        }
        return result;
    }
    public static void print(String sofar){
        if (words.contains(sofar) && sofar.length()>3){
            System.out.println(sofar);
            return;
        }
        if (startsWith(sofar).isEmpty()){
            return;
        }
        if (sofar.length()%2==0){
            char next=alpha[solve(sofar,false)[1]];
            String N=sofar+next;
            System.out.println(sofar+":"+next);
            print(N);
        }else{
            for (int i=0; i<alpha.length; i++){
                print(sofar+alpha[i]);
            }
        }
    }
    static HashMap<String,int[]> cache=new HashMap<String,int[]>();
    public static int[] solve(String sofar,boolean print) {
        if (cache.get(sofar)!=null){
            return cache.get(sofar);
        }
        int M = sofar.length() % 2 == 0 ? 1 : -1;
        if ((words.contains(sofar) && sofar.length() > 3) || startsWith(sofar).isEmpty()) {
            cache.put(sofar,new int[]{M, -1});
            return new int[]{M, -1};
        }
        if (sofar.length() < 5 && print) {
            System.out.println(sofar);
        }
        for (int i = 0; i < alpha.length; i++) {
            int[] q = solve(sofar + alpha[i],print);
            if (q[0] == M) {
                cache.put(sofar,new int[]{q[0], i});
                return new int[]{q[0], i};
            }
        }
        cache.put(sofar,new int[]{-M, -1});
        return new int[]{-M, -1};
    }
    public static void prune(ArrayList<String> s){
        for (int i=0; i<s.size(); i++){
            String n=s.get(i);
            if (n.length()>3){
                ArrayList<String> N=startsWith(n);
                N.remove(n);
                s.removeAll(N);
            }
        }
    }
    public static void Run(String sofar){
        
        ArrayList<String> s = startsWith(sofar);
        if (sofar.length()>1){
        System.out.println("Searching for possible words in Ghost...");
        prune(s);
        System.out.println("Done");
        }
        if (s.isEmpty()) {
            System.out.println("No words start with that.");
            return;
        }
        System.out.println("Searching...");
        int[] x = solve(sofar,true);
        System.out.println(s.size() + " words that are possible in Ghost start with " + sofar + ": " + s);
        System.out.println(x[0] == 1 ? "First player wins" : "Second player wins");
        System.out.println(x[1] == -1 ? "Any move results in other player winning" : ("Optimal move is " + alpha[x[1]]));

    }
    public static void Print(String sofar){
        System.out.println("Solving...");
        //solve(sofar);
        System.out.println("Done. ");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        print(sofar);
    }
    public static void DO(){
        words.remove("dnieper");
        Scanner scan=new Scanner(System.in);
        System.out.print("Overall strategy or just one? (1/2) >");
        String s=scan.nextLine();
        boolean b=true;
        if (s.equals("1")){
            b=false;
        }
        System.out.print("What is it so far? >");
        String sofar = scan.nextLine();
if (b){
    Run(sofar);
}else{
    Print(sofar);
}
    }
    public static boolean test(String s){
        for (int i=0; i<s.length(); i++){
            if (s.indexOf(s.substring(i,i+1))!=i){
                return false;
            }
        }
        return true;
    }
           
    public static void main(String[] args) {
        try {
            load("/Users/leijurv/Downloads/mword10/CROSSWD.TXT");
            //load("/Users/leijurv/Dropbox/Java-Projects/Ghost/Ghostwords.txt");
            //Change to "/Users/USERNAME/Downloads/Java-Projects-master/Ghost/Ghostwords.txt" or whereever you downloaded it to.
        } catch (Exception e) {
            System.out.println("There was an error. Maybe you didn't change the path on line 56?");
            return;
        }
        int maxlen=0;
        String s="";
        for (int i=0; i<words.size(); i++){
            if (words.get(i).length()>=maxlen){
                if (test(words.get(i))){
                    System.out.println(words.get(i));
                    s=words.get(i);
                    maxlen=s.length();
                }
            }
        }
        System.out.println(s);
        //DO();
}}
