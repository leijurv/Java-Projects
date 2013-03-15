/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bernoulli;

/**
 *
 * @author leijurv
 */
public class Bernoulli {
public static class Fraction {

        long Num;
        long Den;

        public Fraction(long a, long b) {
            Num = a;
            Den = b;
        }

        public Fraction(long a) {
            this(a, 1);
        }
public Fraction inverse(){
    return new Fraction(Den,Num);
}
        public boolean equals(Fraction f) {
            Fraction F = f.simplify();
            Fraction x = simplify();
            return x.Num == F.Num && F.Den == x.Den;
        }

        public boolean equals(Object o) {
            if (o instanceof Fraction) {
                return equals((Fraction) o);
            }
            return false;
        }

        public int compareTo(Fraction f) {
            if (simplify().equals(f.simplify())) {
                return 0;
            }
            if (Num * f.Den < Den * f.Num) {
                return -1;
            }
            return 1;
        }


        public Fraction add(Fraction f) {
            if (f.equalsZero()) {
                return new Fraction(Num, Den);
            }
            return new Fraction(Num * f.Den + Den * f.Num, Den * f.Den);
        }
        public Fraction neg(){
            return new Fraction(-Num,Den);
        }
        /*
        public Fraction subtract(Fraction f){
        return new Fraction(Num-f.Num,Den-f.Den);
        }*/

        

        public Fraction multiply(Fraction f) {
            return new Fraction(Num * f.Num, Den * f.Den);
        }

        public boolean equalsOne() {
            return Num == Den;
        }

        public boolean equalsZero() {
            return Num == 0 || Den == 0;
        }

        public Fraction simplify() {
            if (Num == 0) {
                return new Fraction(0, 1);
            }
            if (Num < 0 && Den < 0) {
                return new Fraction(-Num, -Den);
            }
            if (Num < 0) {
                Fraction f = (new Fraction(-Num, Den)).simplify();
                return new Fraction(-f.Num, f.Den);
            }
            if (Den < 0) {
                Fraction f = (new Fraction(Num, -Den)).simplify();
                return new Fraction(f.Num, -f.Den);
            }


            if (Num == 1 || Den == 1) {
                return new Fraction(Num, Den);
            }

            for (int i = 2; i <= Num && i <= Den; i++) {
                if (Num % i == 0 && Den % i == 0) {
                    return (new Fraction(Num / i, Den / i)).simplify();
                }
            }
            return new Fraction(Num, Den);
        }

        public String toString() {
            Fraction f = simplify();
            Num = f.Num;
            Den = f.Den;
            if (Den == 1) {
                return Long.toString(Num);
            }
            if (Den == -1) {
                return Long.toString(0 - Num);
            }
            if (equalsOne()) {
                return "1";
            }
            if (Num == 0) {
                return "0";
            }
            return Num + "/" + Den;
        }
    }
public static Fraction Bernoulli(int n){
    Fraction[] A=new Fraction[n+2];
    for (int m=0; m<=n; m++){
        A[m]=new Fraction(1,m+1);
        for (int j=m; j>=1; j--){
            A[j-1]=(new Fraction(j)).multiply(A[j-1].add(A[j].neg())).simplify();
        }
    }
    return A[0];
}
//zeta * (-1)^(n+1) * (2*(2n)!) / (B(2*n)* 4^(2*n))
public static Fraction durp(int n){
    Fraction f=(new Fraction(factorial(n*2),(long)Math.pow(2,2*n-1)));
 
    Fraction x=(Bernoulli(n*2));
    Fraction y=x.inverse();
    
    Fraction u=new Fraction((long)Math.pow(-1,n+1));
    return f.multiply(y).multiply(u).simplify();
}
public static int factorial(int n){
    if (n==0){
        return 1;
    }
    return factorial(n-1)*n;
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for (int i=1; i<10; i++){
            System.out.println(durp(i));
        }
        // TODO code application logic here
    }
}
