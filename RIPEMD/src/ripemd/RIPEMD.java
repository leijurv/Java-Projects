/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ripemd;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.DigestMethod;

/**
 *
 * @author leijurv
 */
public class RIPEMD {
    public static int f(int j, int x, int y, int z){
        if (j<=15){
            return (x^y)^z;
        }
        if (j<=31){
            return (x&y)|((~x)&z);
        }
        if (j<=47){
            return (x|(~y))^z;
        }
        if (j<=63){
            return (x&y)|(y&(~z));
        }
        return x^(y|(~z));
            
    }
    public static int k(int j){
        if (j<=15){
            return 0;
        }
        if (j<=31){
            return  1518500249;
        }
        if (j<=47){
            return 1859775393 ;
        }
        if (j<=63){
            return 1200479854+1200479854 ;
        }
        return 1352829926;
    }
    public static int kPrime(int j){
        if (j<=15){
            return 1420426919+1420426919;
        }
        if (j<=31){
            return 1548603684;
        }
        if (j<=47){
            return 1836072691;
        }
        if (j<=63){
            return 2053994217;
        }
        return 0;
    }
    public static int r(int j){
        if (j<=15){
            return j;
        }
        if (j<=31){
            return (new int[] {7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8})[j-16];
        }
        if (j<=47){
            return (new int[] {3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12})[j-32];
        }
        if (j<=63){
            return (new int[] {1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2})[j-48];
        }
        return (new int[] {4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13})[j-64];
    }
    public static int rPrime(int j){
        if (j<=15){
            return (new int[] {5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12})[j];
        }
        if (j<=31){
            return (new int[] {6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2})[j-16];
        }
        if (j<=47){
            return (new int[] {15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13})[j-32];
        }
        if (j<=63){
            return (new int[] {8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14})[j-48];
        }
        return (new int[] {12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11})[j-64];
    }
    public static int s(int j){
        if (j<=15){
            return (new int[] {11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8})[j];
        }
        if (j<=31){
            return (new int[] {7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12})[j-16];
        }
        if (j<=47){
            return (new int[] {11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5})[j-32];
        }
        if (j<=63){
            return (new int[] {11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12})[j-48];
        }
        return (new int[] {9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6})[j-64];
    }
    public static int sPrime(int j){
        if (j<=15){
            return (new int[] {8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6})[j];
        }
        if (j<=31){
            return (new int[] {9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11})[j-16];
        }
        if (j<=47){
            return (new int[] {9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11})[j-32];
        }
        if (j<=63){
            return (new int[] {15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8})[j-48];
        }
        return (new int[] {8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11})[j-64];
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int h0=1732584193;
        int h1=2011616708+2011616708+1;
                int h2=1281191551+1281191551;
                        int h3=271733878;
                        int h4=1642688760+1642688760;
                        int[] words={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                        byte b=-128;
                        
        System.out.println((int)b);
        DigestMethod i;
        
        
        // TODO code application logic here
    }
}
