/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package morsecode;
import java.util.Date;
import javax.swing.JOptionPane;
import java.util.Scanner;
/**
 *
 * @author leijurv
 */
public class MorseCode {
    static String[] q={".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-."
    ,"---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--..","-----",".----","..---",
    "...--","....-",".....","-....","--...","---..","----.",".-.-.-","--..--","..--..",".----.","-.-.--",
    "-..-.","-.--.","-.--.-",".-...","---...","-.-.-.","-...-",".-.-.","-....-","..--.-",".-..-.","...-..-",".--.-."};
    static String alphabet="abcdefghijklmnopqrstuvwxyz0123456789.,?'!/()&:;=+-_"+'"'+"$@";
    public static void table(){
    String input1 = JOptionPane.showInputDialog(null, "Welcome. What is your call sign?");
    System.out.println("Authenticating...");
        System.out.println("Authenticating...");
        System.out.println("Authenticating...");
    if (input1.equals("W6007") || input1.equals("w6007")){
        System.out.println("Welcome, Jack.");
        
    } else if (input1.equals("W6521") || input1.equals("w6521")){
        
        System.out.println("Welcome, Leif.");
        
    } else if (input1.equals("guest")){
        System.out.println("Welcome, guest.");
    } else {
        System.out.println("Call sign not recognized. Try again.");
    System.exit(0);
    }
    Date currentDate = new Date();
        long a = System.currentTimeMillis();
        System.out.println("Morse Code Translator: -- --- .-. ... -   -.-. --- -.. .   ©2012 LeifMorse Project, all rights reserved.");
        System.out.println("Created by Leif, current version edited by Jack. Project inspired by Jack. Grade I & II license pending.");
        System.out.println(a+" milliseconds, current date is "+currentDate.toString());
        System.out.println("_________________________________________________________________________________________________________");
        System.out.println();
        for (int i=0; i<q.length; i++){
            if (i==11){
                System.out.println();

            }
            if (i==21){
                System.out.println();

            }
            if (i==30){
                System.out.println();

            }
            if (i==38){
                System.out.println();

            }
            if (i==46){
                System.out.println();
            }
            System.out.print(alphabet.charAt(i));
            System.out.print(": "+q[i]+"   ");
        } System.out.println();
}
    
    
    
    public static String decode(String encoded){
        int i=0;
        for (String s : q){
            if (s.equals(encoded)){
                return alphabet.substring(i,i+1);
            }
            i++;
        }
        return encoded;
    }
    public static void end(Date currentDate){
        System.out.println("Build finished. About: Morse Code Translator ©2012 LeifMorse Project, all rights reserved.");
            System.out.println("Sources are as follows: Collaborators include Leif and Jack, websites include below.");
            System.out.println("http://www.uncp.edu/home/acurtis/Courses/ResourcesForCourses/MorseCode.html");
            System.out.println("http://en.wikipedia.org/wiki/Morse_code");
            System.out.println();
            System.out.println("Morse code encoder/decoder finished. Last used: "+currentDate.toString());
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Date currentDate = new Date();
        table();
        String chart;
        String message;
        String fulld;
        String e_d;
        int iterations=1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Loaded chart, element 1 finished.");
        System.out.println();
        System.out.println("Waiting...");
        System.out.println("Waiting...");
        System.out.println();
        System.out.println("Only chart desired? (y/n)");
        chart=scan.nextLine();
        if (chart.equals("y")){
            System.out.println("Copy that! Finishing...");
            end(currentDate);
        } else if (chart.equals("n")){
            System.out.println("Waiting...");
            System.out.println("How many iterations do you want to encode or decode? (Usually 1 unless you are being sneaky)");
            try{
            iterations=Integer.parseInt(scan.nextLine());
            }catch(Exception e){
                System.out.println("Not a number you poopyface");
                System.exit(0);
            }
            if (iterations<1){
                System.out.println("That number is BAD. ");
                System.exit(0);
            }
            System.out.println("Waiting...");
            System.out.println("Main: would you like to encode or decode.");
        e_d=scan.nextLine();
        if (e_d.equals("encode")){
            System.out.println("Loaded \"encode\". Element 2 finished.");
            System.out.println();
            System.out.println("Enter the text you would like to encode.");
            message=scan.nextLine();
            System.out.println();
            System.out.println("Confirming, your message is: "+message);
            System.out.println("Loaded element 3. Waiting...");
            System.out.println();
            System.out.println("_________________________________________________________________________________________________________");
            for (int i=1; i<=iterations; i++){
            message=fullencode(message.toString());
            }
            System.out.println(message);
            System.out.println();
            System.out.println("_________________________________________________________________________________________________________");
            System.out.println();
            end(currentDate);
        } else if (e_d.equals("decode")){
            System.out.println("Loaded \"decode\". Element 2 finished.");
            System.out.println();
            System.out.println("Enter the text you would like to decode.");
            fulld=scan.nextLine();
            System.out.println();
            System.out.println("Confirming, your message is: "+fulld);
            System.out.println("Loaded element 3. Waiting...");
            System.out.println();
            System.out.println("_________________________________________________________________________________________________________");
            for (int i=1; i<=iterations; i++){
            fulld=fulldecode(fulld.toString());
            }
            System.out.println(fulld);
            System.out.println();
            System.out.println("_________________________________________________________________________________________________________");
            System.out.println();
            end(currentDate);
        } else {
            System.out.println("Please specify |Chart| or \"encode\" or \"decode\". Try again next run.");
            end(currentDate);
        }
        } else {
            System.out.println("Unable to read your answer. Exiting program.");
            end(currentDate);
        }
        System.exit(0);
    }
    public static String fullencode(String s){
        String result="";
        String[] words=s.split(" ");
        for (String word : words){
            for (char c : word.toCharArray()){
                result+=(q[alphabet.indexOf(c)]);
                result+=(" ");
            }
            result+=("   ");
        }
        return result;
    }
    public static String fulldecode(String s){
        s=s.replace("…","...");
        String result="";
        String[] words=s.split("   ");
        for(String word : words){
            String w=word.replaceAll("  "," ");
            w=w.replaceAll("  "," ");
            String[] letters=w.split(" ");
            for (String l : letters){
                result+=(decode(l));
            }
            result+=(" ");
        }
        return result;
    }
}


