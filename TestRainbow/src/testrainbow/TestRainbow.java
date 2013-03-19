/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testrainbow;

import cryptolib.SHA1;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;

/**
 *
 * @author leijurv
 */
public class TestRainbow {

    public static byte[] get(int len, byte[] hash, int offset) {
        byte[] S = new byte[len];
        for (int j = 0; j < S.length; j++) {
            S[j] = hash[(j + offset+3) % hash.length];
        }
        return S;
    }

    public static void run(int k, byte[] start, FileWriter F, FileOutputStream Ff) throws IOException {
        SHA1 s = new SHA1();
        byte[] S = new byte[start.length];
        for (int i = 0; i < S.length; i++) {
            S[i] = start[i];
        }
        for (int i = 0; i < k; i++) {
            byte[] n = s.hash(S);
            S = get(S.length, n, i);

            F.write(S[0] + "," + S[1] + "," + S[2] + "\n");

        }
        F.write("\n");
        //f.write(start[0] + "," + start[1] +","+start[2]+ " " + S[0] + "," + S[1] +","+S[2]+ "\n");
        byte[] x = new byte[S.length * 2];
        for (int i = 0; i < start.length; i++) {
            x[i] = start[i];
        }
        for (int i = 0; i < start.length; i++) {
            x[i + start.length] = S[i];
        }
        Ff.write(x);
    }

    public static byte[] chain(byte[] start, int k, int pos) {
        SHA1 s = new SHA1();
        byte[] S = new byte[start.length];
        for (int i = 0; i < S.length; i++) {
            S[i] = start[i];
        }
        for (int i = 0; i <= pos; i++) {
            byte[] N = s.hash(S);
            S = get(S.length, N, i);
        }
        return S;
    }

    public static boolean eq(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static void print(byte[] b) {
        for (byte B : b) {
            System.out.print(B + ",");
        }
        System.out.println();
    }

    public static byte[] pos(byte[][] end, byte[] p, byte[][] start,int k, int size) {
        SHA1 sha1 = new SHA1();
        byte[][] ends = new byte[k][0];
        for (int i = 0; i < k; i++) {
            byte[] S = get(size, p, i);
            for (int n = 1; n + i < k; n++) {
                byte[] N = sha1.hash(S);
                S = get(S.length, N, i + n);
            }
            ends[i] = S;
        }
        for (int i = 0; i < ends.length; i++) {
            System.out.println("Searching "+i+" of "+ends.length+" endpoints.");
            for (int j = 0; j < end.length; j++) {
                if (eq(ends[i], end[j])) {
                    byte[] x=chain(start[j],k,i-1);
                    byte[] t=sha1.hash(x);
                    if (eq(t,p)){
                        return x;
                    }else{
                        System.out.println("False alarm: ");
                        print(x);
                    }
                }
            }
        }
        return new byte[]{-1,-1};
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        int k = 256;


        String filePath = "/Users/leijurv/Desktop/chains.txt";

        //gen(k, filePath);



        byte[] orig = {50,2,1};
        byte[] hash = (new SHA1()).hash(orig);
        long s=System.currentTimeMillis();
        print(find(k, filePath, hash));
        System.out.println("Done in "+(System.currentTimeMillis()-s)+" milliseconds.");
    }

    public static void gen(int K, String chainPath) throws IOException {
        FileOutputStream Ff = new FileOutputStream(chainPath);
        FileWriter F = new FileWriter("/Users/leijurv/Desktop/pos.txt");
        for (int i = -128; i <= 128; i++) {
            System.out.println(i);
            for (int j = -127; j <= 128; j++) {
                for (int k = 0; k <= 0; k++) {
                    run(K, new byte[]{(byte) i, (byte) j, (byte) k}, F, Ff);

                }
            }
        }
        Ff.close();
        F.close();
    }

    public static byte[] find(int k, String filePath, byte[] hash) throws Exception {
        FileInputStream f = new FileInputStream(filePath);
        byte[][] start = new byte[f.available() / 6][3];
        byte[][] end = new byte[f.available() / 6][3];
        byte[] raw = new byte[f.available()];
        f.read(raw);
        for (int i = 0; i < raw.length - 6; i += 6) {
            if (i%100000==0){
                System.out.println("Loaded "+(i/100000)+" of "+(raw.length/100000)+" chains.");
            }
            start[i / 6] = new byte[]{raw[i], raw[i + 1], raw[i + 2]};
            end[i / 6] = new byte[]{raw[i + 3], raw[i + 4], raw[i + 5]};
        }
        return pos( end, hash,start,k, end[0].length);
    }
}
