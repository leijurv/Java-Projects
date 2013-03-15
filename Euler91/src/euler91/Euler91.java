/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler91;

/**
 *
 * @author leif
 */
public class Euler91 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int SIZE = 50;
        int answer = SIZE * SIZE * 3;
        for (int x = 1; x <= SIZE; x++) {
            for (int y = 1; y <= SIZE; y++) {
                int gcf = GCD(x, y);
                int dx = x / gcf;
                int dy = y / gcf;
                answer += Math.min(y / dx, (SIZE - x) / dy) * 2;
            }
            System.out.println(x);
        }

        System.out.println(answer);
    }

    public static int GCD(int a, int b) {
        if (b == 0) {
            return a;
        }
        return GCD(b, a % b);
    }
}
