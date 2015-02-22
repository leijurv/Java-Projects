/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package palindromes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
/**
 *
 * @author leijurv
 */
public class Palindromes {
    static ArrayList<String> words=new ArrayList<>(236000);
    static HashMap<String,ArrayList<String>> starts=new HashMap<>();
    public static ArrayList<String> createWords(String x){
        if (x.length()==0){
            ArrayList<String> res=new ArrayList<>();
            res.add("");
            return res;
        }
        ArrayList<String> begin=beginningOf(x);
        ArrayList<String> result=new ArrayList<>();
        begin.stream().forEach((String s)->{
            String res=x.substring(s.length(),x.length());
            ArrayList<String> h=createWords(res);
            h.stream().forEach((String S)->{
                result.add(s+" "+S);
            });
        });
        return result;
    }
    public static ArrayList<String> beginningOf(String s){
        if (starts.get(s)!=null){
            return starts.get(s);
        }
        ArrayList<String> res=new ArrayList<>();
        words.stream().filter((S)->(S.length()<=s.length()&&s.startsWith(S)&&!res.contains(S))).forEach((S)->{
            res.add(S);
        });
        res.sort((String o1,String o2)->new Integer(o2.length()).compareTo(o1.length()));
        starts.put(s,res);
        return res;
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception{
        FileWriter out=new FileWriter(new File("/Users/leijurv/Documents/palindromes.txt"),true);
        String path="/Users/leijurv/Downloads/corncob_lowercase.txt";
        String path1="/Users/leijurv/Downloads/wordsen.txt";
        try(BufferedReader br=new BufferedReader(new FileReader(new File(path)))){
            String line;
            words.add("a");
            words.add("i");
            System.out.println("Loading");
            while ((line=br.readLine())!=null){
                line=line.toLowerCase();
                if (!(line.length()==1&&!(line.equals("a")||line.equals("i")))&&line.length()!=0){
                    words.add(line.toLowerCase());
                }
            }
        }
        System.out.println("Loaded");
        while (true){
            String s="";
            Random r=new Random();
            int a=r.nextInt(3)+2;
            for (int i=0; i<a; i++){
                s=s+words.get(r.nextInt(words.size()))+" ";
            }
            String reversed="";
            String p=s.replace(" ","");
            for (int i=p.length()-1; i>=0; i--){
                reversed=reversed+p.charAt(i);
            }
            ArrayList<String> result=createWords(reversed);
            if (!result.isEmpty()){
                System.out.println(s);
                System.out.println(reversed);
                System.out.println(result);
                System.out.println(s+result.get(0));
                out.write(s+result.get(0)+". \n");
                out.flush();
                //return;
            }
            Thread.sleep(10);
        }
    }
}
