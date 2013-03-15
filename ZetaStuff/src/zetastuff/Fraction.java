/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zetastuff;

/**
 *
 * @author leif
 */
public class Fraction {
    long a;
    long b;
    public Fraction (long A, long B){
        a=A;
        b=B;/*
        if (A<0){
            if (B<0){
                long[] ab=simplify(-A,-B);
        b=ab[1];
        a=ab[0];
        return;
            }
            long[] ab=simplify(-A,B);
        b=ab[1];
        a=-ab[0];
        }
        if (B<0){
            long[] ab=simplify(A,-B);
        b-=ab[1];
        a=ab[0];
        return;
        }*/
        long[] ab=simplify(A,B);
        b=ab[1];
        a=ab[0];
        
    }
    public long[] simplify(long A, long B){
        if (A!=0 && B%A==0){
            return new long[]{1,B/A};
        }
        for (int i=2; i<A && i<B; i++){
            if (A%i==0 && B%i==0){
                return simplify(A/i,B/i);
            }
        }
        return new long[]{A,B};
    }
    public Fraction multiply(int x){
        return new Fraction(a*x,b);
    }
    public Fraction multiply(Fraction f){
        return new Fraction(a*f.a,b*f.b);
    }
    public Fraction subtract(Fraction f){
        return new Fraction(a*f.b-b*f.a,b*f.b);
    }
    public String toString(){
        return a+"/"+b;
    }
}
