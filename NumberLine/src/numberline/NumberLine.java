/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package numberline;

import fraction.Fraction;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author leijurv
 */
public class NumberLine {
/*
    public static class Fraction {

        int Num;
        int Den;

        public Fraction(int a, int b) {
            Num = a;
            Den = b;
        }

        public Fraction(int a) {
            this(a, 1);
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
        /*
        public Fraction subtract(Fraction f){
        return new Fraction(Num-f.Num,Den-f.Den);
        }

        

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
                return Integer.toString(Num);
            }
            if (Den == -1) {
                return Integer.toString(0 - Num);
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
*/
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int points = 16;
        ArrayList<Fraction> line = new ArrayList<Fraction>();
        line.add(new Fraction(0));
        line.add(new Fraction(1));
        for (int den = 2; den <= points; den++) {
            for (int num = 1; num < den; num++) {
                Fraction c = new Fraction(num, den);
                if (!line.contains(c)) {
                    boolean done = false;
                    for (int i = 0; i < line.size() && !done; i++) {
                        if (c.compareTo(line.get(i)) == -1) {
                            if (i == 0) {
                                line.add(i, c);
                                done = true;
                            } else {
                                if (c.compareTo(line.get(i - 1)) == 1) {
                                    line.add(i, c);
                                    done = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        println("Line contains "+line.size()+" fractions.");
        print("Line: ");
        for (int i=0; i<line.size(); i++){
            print(line.get(i)+"");
            if (i!=line.size()-1){
                print(",");
            }
        }
        println("");
        println("Finding "+points+" points");
        println("Precentages every 5,000 points evaluated");
        if (Cull){
            println("Culling "+(cull-1)+"/"+cull+" of points from "+cullMin+" to "+cullMax);
        }
        ArrayList<ArrayList<Integer>> x=new ArrayList<ArrayList<Integer>> ();
        x = possibilities(points, line);
        FileWriter f=new FileWriter("/Users/leijurv/Desktop/numberline.txt");
        println("Found "+x.size()+" different possible points of length "+points);
        for (int i=0; i<x.size(); i++){
            int N=i;
            if (N % 100 == 0) {
                println(((float)((int)(((double)N / (double)x.size())*1000D)))/10F+"%");
            }
            ArrayList<Integer> n=x.get(i);
            String r="";
            for (int y=0; y<n.size(); y++){
                r=r+line.get(n.get(y)).add(line.get(n.get(y)+1)).multiply(new Fraction(1,2));
                if (y!=n.size()-1){
                    r=r+",";
                }
            }
            f.write(r);
            f.write("\n");
        }
f.close();

    }
    static final int seed=10;
    static final Random R=new Random(seed);
    static final int cull=3;
    static final int cullMin=8;
    static final int cullMax=10;
    static final boolean Cull=false;
    //1/2: 6.5 min
    //0:1628
    //1:1284
    
    //1/3: 2.5-3 min
    //0:288
    //1:112
    //2:432
    //3:1028
    //4:1400
    //5:526
    //6:480
    //7:243
    //8:288
    //9:832
    //10:288
    public static ArrayList<ArrayList<Integer>> possibilities(int length, ArrayList<Fraction> line) {
        if (length == 1) {
            ArrayList<ArrayList<Integer>> Result = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < line.size() - 1; i++) {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(i);
                Result.add(temp);
            }
            return Result;
        }
        /*
        if (length == 2) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.add(0);
            temp.add(line.size() - 2);
            ArrayList<ArrayList<Integer>> x = new ArrayList<ArrayList<Integer>>();
            x.add(temp);
            return x;
        }*/
        ArrayList<ArrayList<Integer>> next = possibilities(length - 1, line);
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        int N = 0;
        boolean[] taken = new boolean[length];
        for (ArrayList<Integer> n : next) {
            N++;
            if (N % 5000 == 0) {
                println(((float)((int)(((double)N / (double)next.size())*1000D)))/10F+"%");
            }
            boolean works = true;
            for (int i = 0; i < length; i++) {
                taken[i] = false;
            }
            for (int i = 0; i < n.size() && works; i++) {
                Fraction c = line.get(n.get(i)).add(line.get(n.get(i) + 1)).multiply(new Fraction(1, 2));
                int p = -1;
                boolean done = false;
                for (int x = 0; x < length && !done; x++) {
                    if ((new Fraction(x, length)).compareTo(c) == -1) {
                        if ((new Fraction(x + 1, length)).compareTo(c) == 1) {
                            p = x;
                            done = true;
                        }
                    }
                }
                if (taken[p]) {
                    works = false;
                }
                taken[p] = true;
            }
            if (works) {
                for (int i = 0; i < line.size(); i++) {
                    Fraction c = line.get(i);
                    int p = -1;
                    boolean done = false;
                    for (int x = 0; x < length && !done; x++) {
                        if ((new Fraction(x, length)).compareTo(c) == -1) {
                            if ((new Fraction(x + 1, length)).compareTo(c) == 1) {
                                p = x;
                                done = true;
                            }
                        }
                    }
                    if (p != -1 && !taken[p]) {
                        ArrayList<Integer> r = new ArrayList<Integer>(n);
                        r.add(i);
                        if (!Cull || length < cullMin || length > cullMax || R.nextInt(cull) ==0) {
                            result.add(r);
                        }

                    }
                }

            }
        }
        println("Done "+length + " points. "+result.size()+" until next point.");
        return result;
    }
    public static void println(String s){
        System.out.println(s);
    }
    public static void print(String s){
        System.out.print(s);
    }
}