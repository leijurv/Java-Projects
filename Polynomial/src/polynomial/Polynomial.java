/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package polynomial;

import fraction.Fraction;
import gaussian_elimination.Gaussian_Elimination;
import gaussian_elimination.Gaussian_Elimination.Matrix;
import java.util.Random;

/**
 *
 * @author leijurv
 */
public class Polynomial {
    static final Polynomial ZERO = new Polynomial(new Fraction[]{new Fraction(0)});
    static final Polynomial ONE = new Polynomial(new Fraction []{new Fraction (1)});
    static final Polynomial X = new Polynomial(new Fraction []{new Fraction (0), new Fraction (1)});
    private final Fraction [] cc;

    private Polynomial(Fraction [] a) {
        cc = a;
    }
    public Polynomial (Fraction value){
        this(new Fraction []{value});
    }
    public Polynomial(Polynomial p){
        cc=new Fraction [p.cc.length];
        System.arraycopy(p.cc, 0, cc, 0, cc.length);
    }
    public boolean equals(Object o) {
        if (!(o instanceof Polynomial)) {
            return false;
        }
        Polynomial c = (Polynomial) o;
        for (int i = 0; i < c.cc.length || i < cc.length; i++) {
            if (cc.length >= i || c.cc.length >= i) {
                if (i < cc.length) {
                    if (!cc[i].equalsZero()) {
                        return false;
                    }
                }
                if (i < c.cc.length) {
                    if (!c.cc[i].equalsZero()) {
                        return false;
                    }
                }
            } else {
                if (!cc[i].equals(c.cc[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public Polynomial subtract(Polynomial b) {
        Fraction [] x = new Fraction [Math.max(b.cc.length, cc.length)];
        System.arraycopy(cc, 0, x, 0, cc.length);
        for (int i = 0; i < b.cc.length; i++) {
            x[i]=x[i].subtract(b.cc[i]);
        }
        return simplify(new Polynomial(x));
    }

    public Polynomial add(Polynomial b) {
        if (b.cc.length < cc.length) {
            return b.add(this);
        }
        Fraction [] x = new Fraction [b.cc.length];
        System.arraycopy(cc, 0, x, 0, cc.length);
        for (int i = 0; i < b.cc.length; i++) {
            x[i]=x[i].add(b.cc[i]);
        }
        return simplify(new Polynomial(x));
    }

    public Polynomial multiply(Polynomial b) {
        if (b.cc.length < cc.length) {
            return b.multiply(this);
        }
        Fraction [] N = new Fraction [b.cc.length + cc.length];
        for (int i = 0; i < cc.length; i++) {
            for (int n = 0; n < b.cc.length; n++) {
                N[i + n]=N[i+n].add(cc[i].multiply(b.cc[n]));
            }
        }
        return simplify(new Polynomial(N));
    }
/*
    public Fraction eval(Fraction i) {
        Fraction result = new Fraction(0);
        for (int n = 0; n < cc.length; n++) {
            result=result.add(i.pow(n).multiply(cc[n]));
        }
        return result;
    }*/

    public Fraction eval(Fraction  i) {
        Fraction result = new Fraction (0);
        Fraction  cur=new Fraction (1);
        for (Fraction n : cc){
            result=result.add(cur.multiply(n)).simplify();
            cur=cur.multiply(i).simplify();
        }
        return result;
    }

    public static Polynomial simplify(Polynomial p) {
        if (p.cc.length == 1) {
            return p;
        }
        if (p.cc[p.cc.length - 1].equalsZero()) {
            Fraction [] x = new Fraction [p.cc.length - 1];
            System.arraycopy(p.cc, 0, x, 0, x.length);
            return simplify(new Polynomial(x));
        }
        return p;
    }

    public Fraction coefficient(int power) {
        return cc.length >= power ? new Fraction(0) : cc[power];
    }

    private String toString1() {
        String result = "";
        for (int i = cc.length - 1; i >= 0; i--) {
            System.out.print(cc[i] + (i == 0 ? "" : ","));
        }
        return "";
    }

    @Override
    public String toString() {
        String result = "";
        boolean first = true;
        for (int i = cc.length - 1; i >= 0; i--) {
            if (!cc[i].equalsZero()) {
                if (cc[i].compareTo(new Fraction(0))==1 && !first) {
                    result = result + "+";
                }
                first = false;

                result = result + ((cc[i] .equalsOne() || cc[i].neg().equalsOne()) && i != 0 ? "" : cc[i]);
                result = result + (cc[i].neg().equalsOne() && i != 0 ? "-" : "");
                result = result + (i != 0 ? "x" : "");
                result = result + (i > 1 || i < -1 ? "^" + i : "");
            }
        }
        return result;
    }
/*
    public String toString2() {
        String result = "";
        for (int i = cc.length - 1; i >= 0; i--) {
            if (cc[i] != 0) {
                if (cc[i] < 0) {
                    if (i == 0) {
                        result = result + cc[i];
                    } else {
                        if (i == 1) {
                            if (cc[i] == -1) {
                                result = result + "-x";
                            } else {
                                result = result + cc[i] + "x";
                            }
                        } else {
                            if (cc[i] == -1) {
                                result = result + "-x^" + i;
                            } else {
                                result = result + cc[i] + "x^" + i;
                            }

                        }
                    }
                } else {
                    if (i == 0) {
                        if (i == cc.length - 1) {
                            result = result + cc[i];
                        } else {
                            result = result + "+" + cc[i];
                        }
                    } else {
                        if (i == cc.length - 1) {
                            if (i == 1) {
                                if (cc[i] == 1) {
                                    result = result + "x";
                                } else {
                                    result = result + cc[i] + "x";
                                }

                            } else {
                                if (cc[i] == 1) {
                                    result = result + "x^" + i;
                                } else {
                                    result = result + cc[i] + "x^" + i;
                                }
                            }
                        } else {
                            if (i == 1) {
                                if (cc[i] == 1) {
                                    result = result + "+x";
                                } else {
                                    result = result + "+" + cc[i] + "x";
                                }
                            } else {
                                if (cc[i] == 1) {
                                    result = result + "+" + "x^" + i;
                                } else {
                                    result = result + "+" + cc[i] + "x^" + i;
                                }
                            }
                        }
                    }
                }

            }
        }
        return result;
    }*/
public Polynomial divide(Polynomial b){
    return longdivide(this,b)[0];
}
public Polynomial mod(Polynomial b){
    return longdivide(this,b)[1];
}
private static Polynomial[] longdivide(Polynomial n, Polynomial d){
    Polynomial q=Polynomial.ZERO;
    Polynomial r=new Polynomial(n);
    while(!r.equals(Polynomial.ZERO) && r.degree()>=d.degree()){
        r=simplify(r);
        d=simplify(d);
        int t=r.cc[r.cc.length-1].divide(d.cc[d.cc.length-1]).toInt();
        q=q.multiply(Polynomial.X).add(new Polynomial(new Fraction(t)));
        r=r.subtract(d.multiply(Polynomial.X.power(r.degree()-d.degree())).multiply(new Polynomial(new Fraction(t))));
    }
    return new Polynomial[] {q,r};
}
public int degree(){
    return simplify(this).cc.length;
}

    public Polynomial power(int i) {
        if (i == 1) {
            return this;
        }
        if (i == 0) {
            return ONE;
        }
        if (i % 2 == 0) {
            Polynomial p = power(i / 2);
            return p.multiply(p);
        }
        return multiply(power(i - 1));
    }
public static Polynomial interpolate(Fraction[] xVal, Fraction[] yVal){
    
        Fraction[][] matr=new Fraction[xVal.length][xVal.length+1];
        for (int i=0; i<xVal.length; i++){
            for (int j=0; j<xVal.length; j++){
                matr[matr.length-i-1][j]=xVal[i].pow(xVal.length-j-1);
            }
            matr[matr.length-i-1][xVal.length]=yVal[i];
        }
        Matrix m=new Matrix(matr);
        Gaussian_Elimination.Gaussian(m);
        Fraction[] coef=new Fraction[xVal.length];
        for (int i=0; i<xVal.length; i++){
            coef[coef.length-i-1]=m.values[i][xVal.length].simplify();
        }
        return new Polynomial(coef);
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random r=new Random();
        int secret=521;
        Fraction[] points=new Fraction[3];
        points[0]=new Fraction(secret);
        points[1]=new Fraction(r.nextInt(500)+10);//Random
        points[2]=new Fraction(r.nextInt(500)+10);//Random
        Polynomial P=new Polynomial(points);
        System.out.println("Secret polynomial is "+P);
        System.out.println("Secret 1 is "+P.eval(new Fraction(1)));
        System.out.println("Secret 2 is "+P.eval(new Fraction(2)));
        System.out.println("Secret 3 is "+P.eval(new Fraction(3)));
        System.out.println("Secret 4 is "+P.eval(new Fraction(4)));
        
        
        System.out.println("Using 3 of 4 secrets");
        Fraction[] xVal=new Fraction[3];
        Fraction[] yVal=new Fraction[xVal.length];
        xVal[0]=new Fraction(1);
        xVal[1]=new Fraction(2);
        xVal[2]=new Fraction(4);
        
        yVal[0]=P.eval(xVal[0]);
        yVal[1]=P.eval(xVal[1]);
        yVal[2]=P.eval(xVal[2]);
        for (int i=0; i<xVal.length; i++){
            System.out.println("Using secret "+xVal[i]+", it is "+yVal[i]);
        }
        Polynomial p=interpolate(xVal,yVal);
        System.out.println("Calculates polynomial is "+p);
    }
}
