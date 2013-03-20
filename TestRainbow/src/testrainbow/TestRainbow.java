/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testrainbow;

import cryptolib.SHA1;
import java.io.*;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class TestRainbow {

    static SHA1 s = new SHA1();
    static byte[][] start;
    static byte[][] end;

    public static byte[] get(int len, byte[] hash, int offset) {
        byte[] S = new byte[len];
        for (int j = 0; j < S.length; j++) {
            S[j] = hash[(j + offset + 3) % hash.length];
        }
        return S;
    }

    public static void run(int k, byte[] start, FileOutputStream Ff) throws IOException {
        byte[] S=chain(start,k,k-1);
        byte[] x = new byte[S.length * 2];
        System.arraycopy(start, 0, x, 0, start.length);
        System.arraycopy(S, 0, x, start.length, start.length);
        Ff.write(x);
    }

    public static byte[] chain(byte[] start, int k, int pos) {
        byte[] S = new byte[start.length];
        System.arraycopy(start, 0, S, 0, S.length);
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
        for (int i = 0; i < b.length; i++) {
            System.out.print(b[i] + (i == b.length - 1 ? "" : ","));
        }
        System.out.println();
    }

    public static byte[] pos(byte[] p, int k, int size, boolean print) {
        byte[][] ends = new byte[k][0];
        if (print) {
            System.out.println("Initializing " + k + " endpoints.");
        }
        for (int i = 0; i < k; i++) {
            byte[] S = get(size, p, i);
            for (int n = 1; n + i < k; n++) {
                byte[] N = s.hash(S);
                S = get(S.length, N, i + n);
            }
            ends[i] = S;
        }
        if (print) {
            System.out.println("Initialized endpoints");
        }
        for (int i = 0; i < ends.length; i++) {
            if (print) {
                System.out.println("Searching " + i + " of " + ends.length + " endpoints.");
            }
            for (int j = 0; j < end.length; j++) {
                if (eq(ends[i], end[j])) {
                    byte[] x = chain(start[j], k, i - 1);
                    byte[] t = s.hash(x);
                    if (eq(t, p)) {
                        return x;
                    } else {
                        if (print) {
                            System.out.println("False alarm: ");
                            print(x);
                        }
                    }
                }
            }
        }
        return new byte[]{-1, -1};
    }

    public static void main(String[] args) throws Exception {
        int chainLength = 256;
        String filePath = "/Users/leijurv/Desktop/chains.txt";
        gen(chainLength, filePath);
        RUN(filePath);
    }

    public static void RUN(String filePath) throws Exception {
        Random r = new Random();
        long S = System.currentTimeMillis();
        int k = load(filePath, true);
        long SS = System.currentTimeMillis();
        byte[] x = new byte[]{50, 2, 1};
        byte[] X = pos(s.hash(x), k, end[0].length, false);
        print(X);
        System.out.println("Loaded in " + (SS - S) + " milliseconds, run in " + (System.currentTimeMillis() - SS) + " milliseconds.");
    }

    public static void gen(int K, String chainPath) throws IOException {
        FileOutputStream Ff = new FileOutputStream(chainPath);
        int a = (K / 256 - 128);
        int b = (K % 256 - 128);
        Ff.write(new byte[]{(byte) a, (byte) b});
        for (int i = -128; i <= 128; i++) {
            System.out.println(i);
            for (int j = -127; j <= 128; j++) {
                for (int k = 0; k <= 0; k++) {
                    run(K, new byte[]{(byte) i, (byte) j, (byte) k}, Ff);
                }
            }
        }
        Ff.close();
    }

    public static int load(String filePath, boolean print) throws Exception {
        FileInputStream f = new FileInputStream(filePath);
        byte[] R = new byte[2];
        f.read(R);
        int k = (((int) R[0]) + 128) * 256 + ((int) R[1] + 128);
        if (print) {
            System.out.println("Chain length " + k);
        }
        start = new byte[f.available() / 6][0];
        end = new byte[f.available() / 6][0];
        byte[] raw = new byte[f.available()];

        f.read(raw);
        for (int i = 0; i < raw.length; i += 6) {
            if (i % 10000 == 0) {
                if (print) {
                    System.out.println("Loaded " + (i) + " of " + (raw.length) + " chains.");
                }
            }
            start[i / 6] = new byte[]{raw[i], raw[i + 1], raw[i + 2]};
            end[i / 6] = new byte[]{raw[i + 3], raw[i + 4], raw[i + 5]};
        }
        return k;
    }
}
