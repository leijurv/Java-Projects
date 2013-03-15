/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zetastuff;

/**
 *
 * @author leif
 */
public class ZetaStuff {

    /**
     * @param args the command line arguments
     */
    public static Fraction getBernoulli(int i){
        Fraction[] a=new Fraction[i+1];
        for (int m=0; m<=i; m++){
            a[m]=new Fraction(1,m+1);
            for (int j=m; j>=1; j--){
                a[j-1]=a[j-1].subtract(a[j]).multiply(j);
            }
        }
        return a[0];
    }
    public static long factorial(int n){
        if (n==0){
            return 1;
        }
        return n*factorial(n-1);
    }
    public static Fraction derp(int n){
        Fraction f=new Fraction(((int)Math.pow(-1,1+n))*((int)Math.pow(2,2*n)),2*factorial(2*n));
        return f.multiply(getBernoulli(2*n));
    }
    public static void main(String[] args) {
        System.out.println(getBernoulli(4).a);
        System.out.println(getBernoulli(4).b);
        System.out.println(derp(6));
        ///1/(zeta(2n)*((-1)^(n+1))*B(2n)*(2^(2*n)))=pi^2n*(2*((2*n)!)
    }
}
