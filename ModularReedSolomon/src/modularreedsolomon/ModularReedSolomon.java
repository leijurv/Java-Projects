/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modularreedsolomon;

/**
 *
 * @author leif
 */
public class ModularReedSolomon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Polynomial P=new Polynomial(new int[] {4,1},7);
        Polynomial Q=new Polynomial(new int[] {5,1},7);
        
        Polynomial g=P.multiply(Q);
        System.out.println(g);
        Polynomial p=new Polynomial(new int[] {4,5},7);
        
        Polynomial s=g.multiply(p);
        
        System.out.println(s);
        System.out.println(s.eval(1)+","+s.eval(2)+","+s.eval(3)+","+s.eval(4));
    }
}
