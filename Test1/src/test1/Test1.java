/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;

/**
 *
 * @author leif
 */
public class Test1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int limit = 1500000;
        int[] triangles = new int[limit + 1];
        String t="{";
        int result = 0;
        int mlimit = (int) Math.sqrt(limit / 2);

        for (long m = 2; m < mlimit; m++) {
            for (long n = 1; n < m; n++) {
                if (((n + m) % 2) == 1 && GCD(n, m) == 1) {
                    long a = m * m + n * n;
                    long b = m * m - n * n;
                    long c = 2*m*n;
                    int p = (int) (a + b + c);
                    while (p <= limit) {
                        triangles[p]++;
                        if (triangles[p] == 1) {
                            result++;
                        }
                        if (triangles[p] == 2) {
                            result--;
                        }
                        p+=a+b+c;
                    }
                    //t=t+"{"+a+","+b+","+c+"},";
                }
            }
        }
        System.out.println(result);
    }

    public static long GCD(long a, long b) {
        if (b == 0) {
            return a;
        }
        return GCD(b, a % b);
    }
}
