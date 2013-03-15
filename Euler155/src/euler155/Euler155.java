/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler155;

/**
 *
 * @author leijurv
 */
public class Euler155 {
public static int GCD(int a, int b) {
   if (b==0) return a;
   return GCD(b,a%b);
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long result = 0;
int limit = 120000;
 
//Sieve all radicals
long[] radicals = new long[limit+1];
for (int i = 0; i < radicals.length; i++) {
    radicals[i] = 1;
}
 
for (int i = 2; i < limit; i++) {
    if (radicals[i] == 1) {
        radicals[i] = i;
 
        for (int j = i + i; j < limit; j += i) {
            radicals[j] *= i;
        }
    }
}
 
//Check for the properties
for (int a = 1; a < limit; a++) {
    if (a%100==0)
    System.out.println(a);
    for (int b = a + 1; b < limit-a; b++) {
 
        if (radicals[a] * radicals[b] * radicals[a + b] >= a+b) continue;
        if (GCD(a, b) != 1) continue;
        result += a + b;
    }
}
System.out.println(result);
        // TODO code application logic here
    }
}
