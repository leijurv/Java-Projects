/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diffiehellman;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author leif
 */
public class DiffieHellman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cracker.P=new BigInteger("23");
        Cracker.G=new BigInteger("5");
        ThreadManager tm=new ThreadManager(1,true);
        //tm.A=new BigInteger("19");
        for (int i=0; i<tm.required.length; i++){
            System.out.println(tm.required[i][0].toString()+" "+tm.required[i][1].toString());
        }
        tm.startAllThreads();
    }
    public static void IO(){
        System.out.println("Diffie-Hellman key exchange algorithm. Implemented by L.J. 6/28/12");
        Random rand=new Random();
        Scanner scan=new Scanner(System.in);
        System.out.print("Calculate p and g, or input p and g? (c/i)  >");
        String k=scan.nextLine();
        BigInteger p=BigInteger.ZERO;
        BigInteger g=BigInteger.ZERO;
        if (k.equals("c")){
            System.out.print("Bitlength >");
            int bitlength=Integer.parseInt(scan.nextLine());
            g=new BigInteger(bitlength,rand);
            System.out.println("G="+g);
            p=rndBigIntPrime(g,100,rand);
            System.out.println("P="+p);
            
        }else{
            if (k.equals("i")){
                System.out.print("What is p? >");
                String Sp=scan.nextLine();
                System.out.print("What is g? >");
                String Sg=scan.nextLine();
                p=new BigInteger(Sp);
                g=new BigInteger(Sg);
            }else{
                System.out.println("Unknown");
                return;
            }
        }
        
        System.out.print("What would you like do? Perform key exchange, or crack key exchange (1/2) >");
        String operation=scan.nextLine();
        if (operation.equals("1")){
            System.out.print("Do you already have the other person's number? (y/n) >");
            String y=scan.nextLine();
            if (y.equals("n")){
                BigInteger a=rndBigInt(p,rand);
                BigInteger A=g.modPow(a, p);
                System.out.println("Send them "+A);
                System.out.println("Your secret number is "+a);
            }else{
                if (y.equals("y")){
                    System.out.print("What is your secret number?  >");
                    String Sa=scan.nextLine();
                    BigInteger a=new BigInteger(Sa);
                    System.out.print("What is the number you got from the other person? >");
                    String SB=scan.nextLine();
                    BigInteger B=new BigInteger(SB);
                    BigInteger s=B.modPow(a,p);
                    System.out.println("Your shared secret is "+s);
                    System.out.println("Hexadecimal: "+s.toString(16));
                }else{
                    System.out.println("Unknown");
                    return;
                }
            }
        }else{
            if (operation.equals("2")){
                System.out.print("What is A? >");
                String SA=scan.nextLine();
                System.out.print("What is B? >");
                String SB=scan.nextLine();
                BigInteger A=new BigInteger(SA);
                BigInteger B=new BigInteger(SB);
                BigInteger s=crack(p,g,A,B);
                System.out.println("The secret is "+s);
                String t=s.toString(16);
                while(t.length()<256){
                    t="0"+t;
                }
                System.out.println("Hexadecimal: "+s.toString(16));
            }
        }
    }
    public static BigInteger rndBigInt(BigInteger max,Random rnd) {
    
    do {
        BigInteger i = new BigInteger(max.bitLength(), rnd);
        if (i.compareTo(max) < 0)
            return i;
    } while (true);
}
public static BigInteger rndBigIntPrime(BigInteger max, int certainty, Random rnd) {
  
    do {
        BigInteger i = new BigInteger(max.bitLength(), certainty, rnd);
        if (i.compareTo(max) < 0)
            return i;
    } while (true);
}
    public static BigInteger crack(BigInteger p,BigInteger g,BigInteger A,BigInteger B){
        BigInteger a=BigInteger.ZERO;
        while(g.modPow(a,p).compareTo(A)!=0){
            a=a.add(BigInteger.ONE);
        }
        return B.modPow(a,p);
    }
}
