/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumbers;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author leijurv
 */
public class BigNumbers {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //RSAKeyPair r=new RSAKeyPair();
        //r.generate(10,true,new Random());
    }
    public static void treasurehuntorencode(String[] args){
        boolean m = false;//true if treasure hunt is on, false if treasure hunt is off
        long encs = (args.length == 1) ? Integer.parseInt(args[0]) : (m ? 101 : 10);

        if (!m) {
            System.out.print("Encode or decode? (e/d) >");
        }


        Scanner a = new Scanner(System.in);
        String b = m ? "m" : a.nextLine();
        if (b.equals("e")) {
            System.out.println();
            System.out.print("What do you want to encode? >");
            String c = a.nextLine();
            fullencode(c, encs);
            return;
        }
        if (b.equals("d")) {
            System.out.println();
            System.out.print("What do you want to decode? >");
            String c = a.nextLine();
            fulldecode(c, encs);
            return;
        }
        if (b.equals("m") && m) {

            System.out.println("All messages encoded 101 times");
            System.out.println();
            System.out.print("What's the code? >");
            String c = a.nextLine();
            if (c.equals("v32fc7")) {
                String d = ("h6jb3rvf0ddms1rgw9dsd9jxpxysnzzq5nlt7gjktbm53kn0cihmy4hfqgh8lr1vf7no3fe0enjmbf159m08tru4eaesih");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("wyev10")) {
                String d = ("f89e3a8ycqlwo7fzgxm3umgnvf67kb3fjys2fzxt3ecczug7w4ywjcys7glub84jw3");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("qacvqf")) {
                String d = ("eflb6iegmii7m2hep5hsrrwqj7a5evfln15g90ij4pu0gadppvervsw");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("e6v5w5")) {
                String d = ("ccn567cp");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("4mn37s")) {
                String d = ("78s3sanxtnf9ikhnywxf7");
                fulldecode(d, encs);
                return;
            }





            if (c.equals("7gja5")) {
                String d = ("nollvtuwf04c7eglqr5kky3c6wxynylpbylrvg0nji50bh0j9ua78jnaaudwmaz1");
                fulldecode(d, 20);
                return;
            }
            if (c.equals("k1d4a")) {
                String d = ("3lliq9h1dlawjzcu0r1xqxxhz8vdvdid29");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("q56ee0")) {
                String d = ("7mozabp55mk4kof3snn");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("1jk33q")) {
                String d = ("2zlg2fxeimrmlxqgp21ri9vpcrnoo9");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("asdy98")) {
                String d = ("pbmuguv34ovk7lplv7shei8xq");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("o87t4f")) {
                String d = ("6v9fgc5yelnb68wjm1i1peshlbj58kvkcpabq0z7ofubs");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("t34qt3")) {
                String d = ("5fu4rvzxcq2uy2qyu079ujpgp2s0exiweeach8sis5i84");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("ohgv3o")) {
                String d = ("36cbyw75fkvbvo0vkk72ixi");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("t348qw")) {
                String d = ("fdt746vmmljk1zbx3mxzottahgiyxcay3uwm9lt1xp");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("cnteor")) {
                String d = ("b5eq5sx255yytsunvwssf");
                fulldecode(d, encs);
                return;
            }
            if (c.equals("8o7nwt")) {
                String d = ("78s3sanxtnf9ikhnywxf7");
                fulldecode(d, encs);
                return;
            }
        }
        System.out.println("Sorry, I couldn't understand that.");
    }
    public static void fullencode(String c, long encs) {
        long n = System.currentTimeMillis();
        for (int i = 0; i < encs; i++) {
            c = encode(c);
            System.out.print((i + 1) + "th encryption: " + c);
            long q = System.currentTimeMillis();
            System.out.println("      Time: " + (q - n) + " milliseconds");
            n = q;
        }
    }

    public static String encode(String a) {
        return (new BigNumber(a, 37)).toString(36);
    }

    public static void fulldecode(String c, long encs) {
        System.out.println();
        long n = System.currentTimeMillis();
        for (int i = 0; i < encs; i++) {
            c = decode(c);
            System.out.print((i + 1) + "th decryption: " + c);
            long q = System.currentTimeMillis();
            System.out.println("      Time: " + (q - n) + " milliseconds");
            n = q;
        }
    }

    public static String decode(String a) {
        return (new BigNumber(a, 36)).toString(37);
    }
}