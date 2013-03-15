/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler135;

/**
 *
 * @author leijurv
 */
public class Euler135 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int n = 1000000;
int[] solutions = new int[n+1];
 
for (int u = 1; u <= n ; u++) {
    for (int v = 1; u * v <= n; v++) {
        if ((u + v) % 4 == 0 &&
            3 * v  > u &&
            ((3 * v - u) % 4) == 0)
                solutions[u * v]++;
    }
}
int i=0;
for (int x : solutions){
    if (x==10){
        i++;
    }
 
    }
System.out.println(i);
}}
