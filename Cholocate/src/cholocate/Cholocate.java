/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cholocate;

/**
 *
 * @author leif
 */
public class Cholocate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[] a=new int[9];
        for (int i=0; i<9; i++){
            a[i]=-1;
        }
        Board b=new Board(a);
        Board c=b.solve();
        System.out.println(c);
        // TODO code application logic here
    }
}
