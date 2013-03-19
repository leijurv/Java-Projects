/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crackr;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leijurv
 */
public class Crackr {

    /**
     * @param args the command line arguments
     */
    static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    static final String numbers = "0123456789";
    static final String others = "`~-_=+[{]}\\|;:'" + '"' + ",<.>/?";
    static final String Alphabet = alphabet.toUpperCase();
    static final String Numbers = "!@#$%^&*()";
    static final String Hard = alphabet + numbers + Alphabet + Numbers + others;
    static final char[] current = Hard.toCharArray();
    static byte[] goal = new byte[5];
    static MessageDigest cript = null;
    static boolean f = false;
    static final int limit = 10000000;
public Crackr(){

}
    static {
        try {
            cript = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
        }
    }

    public static byte[] getHash(String s) {
        try {
            cript.reset();
            cript.update(s.getBytes("utf8"));
            return cript.digest();
        } catch (UnsupportedEncodingException ex) {
            System.out.println("There was some kinda weird error");
        }
        return null;
    }

    public static boolean done(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            if (b[i] != goal[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean Do(int len, String soFar) {
        if (f){
            return f;
        }
        if (len == 0) {
            byte[] b = getHash(soFar);
            if (done(b)) {
                System.out.println("Solved by brute force");
                System.out.println(soFar);
                System.out.println(soFar + ":" + Hex.encodeHexString(b));
                return true;
            }

            return f;
        }
        if (len == 3 && !soFar.equals("")) {
            System.out.println(soFar);
        }
        //System.out.println(soFar);
        for (int i = 0; i < current.length; i++) {
            if (Do(len - 1, soFar + current[i])) {
                return true;
            }
        }
        return f;
    }

    public static class commonChecker extends Thread {

        String path;

        public commonChecker(byte[] b, String pat) {
            path = pat;
            goal1 = new byte[b.length];
            System.arraycopy(b, 0, goal1, 0, b.length);
            try {
                crypt = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException ex) {
            }
        }
        byte[] goal1;
        MessageDigest crypt = null;

        public boolean done(byte[] b) {
            for (int i = 0; i < b.length; i++) {
                if (b[i] != goal1[i]) {
                    return false;
                }
            }
            return true;
        }

        public void run() {
            boolean words = path.endsWith("words.txt");
            FileInputStream fstream = null;
            int amount = 0;
            try {

                fstream = new FileInputStream(path);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null && !f && amount < limit) {
                    try {
                        crypt.reset();
                        crypt.update(strLine.getBytes("utf8"));
                        byte[] b = crypt.digest();
                        amount++;
                        if (done(b)) {
                            f = true;
                            if (words) {
                                System.out.println();
                                System.out.println();
                                System.out.println();
                                System.out.println("Solved by dictionary checker. ");
                                System.out.println(strLine);
                                System.out.println(strLine + ":" + Hex.encodeHexString(b));
                                System.out.println("In the dictionary");

                            } else {
                                System.out.println();
                                System.out.println();
                                System.out.println();
                                System.out.println("Solved by common password checker. ");
                                System.out.println(strLine);
                                System.out.println(strLine + ":" + Hex.encodeHexString(b));
                                System.out.println("On the list of the " + amount + " most common passowrds.");
                            }
                        }
                    } catch (UnsupportedEncodingException ex) {
                    }
                    if (amount % 10000000 == 0) {
                        System.out.println("Processed " + amount + " common passwords.");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Crackr.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!f) {
                if (!words) {
                    System.out.println("Not on list of " + (amount + 1) + " most common passwords.");
                    System.out.println("Common password checker thread ending");
                } else {
                    System.out.println("Not in the dictionary.");
                    System.out.println("Dictionary thread ending.");
                }
            }


        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        //I hope this doesn't violate the AUP. I'm just making a good password checker.

        //PASSWORD SET SECTION
        //This is the only section which contains the actual password
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter password to check >");
        cript.reset();
        cript.update(scan.nextLine().getBytes("utf8"));
        goal = cript.digest();
        cript.reset();
        System.out.println("Starting cracker.");
        long l=System.currentTimeMillis();

        System.out.println();
        System.out.println();
        System.out.println();
        //get();
        System.out.println("Cracking: " + Hex.encodeHexString(goal));
        System.out.println("Using Rainbow Table: false");
        System.out.println("Generating Rainbow Table: false");
        System.out.println("3 Threads");
        System.out.println("Alphabet: " + Hard);
        System.out.println("Starting thread checking common passwords...");
        (new commonChecker(goal, "/Users/leijurv/Downloads/rockyou.txt")).start();
        System.out.println("Starting thread checking dictionary...");
        (new commonChecker(goal, "/Users/leijurv/Downloads/words.txt")).start();

        for (int i = 1; !f; i++) {
            if (i == 7) {
                System.out.println("This program can't help any longer. It has checked all passwords with length<6");
                break;
            }
            if (i == 6) {
                System.out.println("You should probably use a different program. This will take ~6 days, running 24/7");
            }
            if (i == 5) {
                System.out.println("This will take a while. ~1.5 hrs");
            }
            if (i == 4) {
                System.out.println("This will take a little: ~1 min average");
            }
            System.out.println("Brute-force depth: " + i);
            boolean b = Do(i, "");
            if (b) {
                f = true;
            }
        }
        System.out.println((((float)(System.currentTimeMillis()- l))/1000) +" seconds.");
        /* Stats:
         * It takes ~8 seconds for it to generate all 4 length passwords. 
         * (Tested by commenting out the hash part)
         * 
         * It takes 1 min 20 sec to crack ???? the worst case 4 length password.
         * 
         * It takes less then 1 sec to check all 10000 most common passwords.
         * 
         * I might import a larger table, but that 10000 list seems good for now.
         * Note: This isn't salted. It's just taking the hash of the bytes in UTF8.
         * I don't actaully know, but I believe that the alphabet only contains 
         * ASCII characters so all will only be one byte.
         * 
         */
    }
}
