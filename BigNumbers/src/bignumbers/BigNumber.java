/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumbers;

//import java.math.BigInteger;

import java.util.Random;

/**
 *
 * @author leijurv
 */
public class BigNumber {

    static boolean verbose = false;
    boolean[] val = new boolean[0];

    public BigNumber(boolean[] a) {
        val = a;
    }
    public BigNumber (int a, Random b){
        val=new boolean[a];
        for (int c=0; c<a; c++){
            val[c]=b.nextBoolean();
        }
    }
    private boolean[] binarystringtoboolarray(String a) {
        boolean[] b = new boolean[a.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (a.substring(i, i + 1).equals("1"));
        }
        return b;
    }
    public int compareTo(BigNumber a){
        return compareTo(val,a.val);
    }
    public int compareTo(boolean[] a, boolean[] b){
        boolean[] c=stripLeadingZeros(a);
        boolean[] d=stripLeadingZeros(b);
        if (greater(a,b)){
            return 1;
        }
        if (less(a,b)){
            return -1;
        }
        return 0;
    }
    public BigNumber(String a) {
        this(a, 10, false);
    }

    public BigNumber(String a, int b) {
        this(a, b, true);
    }

    public BigNumber(String a, int b, boolean c) {
        if (b == 2) {
            val = binarystringtoboolarray(a);
        } else {
            if (b == 10) {
                val = stringtoboolarray(a);
            } else {
                val = fromBase(a, b);
            }
        }
    }

    public BigNumber mod(BigNumber a) {
        return new BigNumber(mod(val, a.val));
    }
    public BigNumber modPow(BigNumber a, BigNumber b){
        return new BigNumber(modPow(val,a.val,b.val));
    }
    public BigNumber pow(int a) {
        BigNumber b = new BigNumber(1);
        for (int c = 0; c < a; c++) {
            b = b.multiply(this);
        }
        return b;
    }

    public BigNumber power(BigNumber a) {
        return new BigNumber(pow(val, a.val));
    }

    public BigNumber divide(BigNumber a) {
        return new BigNumber(div(val, a.val));
    }
    public static BigNumber probablePrime(int a, Random b, int c){
        //System.out.println(c);
        BigNumber d=new BigNumber(a,b);
        while(!passesMillerRabin(d,c)){
            System.out.println(d);
            d=d.add(new BigNumber(1));
        }
        return d;
    }
    public static boolean passesMillerRabin(BigNumber a, int b){
        MillerRabinPrimalityTest r=new MillerRabinPrimalityTest(a,new BigNumber(b));
        return r.check();
    }
public boolean[] modPow(boolean[] a, boolean[] b, boolean[] c) {
        if (verbose) {
            print(a);
            print(b);
            System.out.println();
        }
        if (b.length == 0) {
            boolean[] d = {true};
            return d;
        }
        if (b.length == 1 && b[0] == false) {
            boolean[] d = {true};
            return d;
        }
        if (b[b.length - 1]) {
            return stripLeadingZeros(mod(multi(a, modPow(a, str(b, (new BigNumber(1)).val),c)),c));
        } else {
            boolean[] d = modPow(a, div(b, (new BigNumber(2)).val),c);
            return stripLeadingZeros(mod(multi(d, d),c));
        }

    }
    public boolean[] pow(boolean[] a, boolean[] b) {
        if (verbose) {
            print(a);
            print(b);
            System.out.println();
        }
        if (b.length == 0) {
            boolean[] c = {true};
            return c;
        }
        if (b.length == 1 && b[0] == false) {
            boolean[] c = {true};
            return c;
        }
        if (b[b.length - 1]) {
            return stripLeadingZeros(multi(a, pow(a, str(b, (new BigNumber(1)).val))));
        } else {
            boolean[] c = pow(a, div(b, (new BigNumber(2)).val));
            return stripLeadingZeros(multi(c, c));
        }

    }

    public BigNumber add(BigNumber a) {
        return new BigNumber(ad(val, a.val));
    }

    public BigNumber(long a) {
        val = inttoboolarray(a);
    }

    public BigNumber subtract(BigNumber a) {
        return new BigNumber(str(val, a.val));
    }

    public BigNumber multiply(BigNumber a) {
        return new BigNumber(multi(val, a.val));
    }

    private boolean[] ad(boolean[] a, boolean[] b) {
        boolean c = false;
        if (a.length > b.length) {
            c = true;
        }
        boolean[] d = c ? a : addbegintil(a, b.length);
        boolean[] e = c ? addbegintil(b, a.length) : b;
        if (verbose) {
            print(d);
            print(e);
        }
        boolean f = false;
        boolean[] g = new boolean[d.length];
        for (int h = d.length - 1; h != -1; h--) {
            if (verbose) {
                System.out.print(f);
                System.out.print(d[h]);
                System.out.print(e[h]);
            }
            boolean i = (d[h] && e[h]);
            boolean j = ((d[h] || e[h]) && !(d[h] && e[h]));
            boolean k = ((f || j) && !(f && j));
            f = (i || (f && j));
            if (verbose) {
                System.out.println(k);
            }
            g[h] = k;
        }
        boolean[] h = f ? addbegin(g, f) : g;
        if (verbose) {
            print(h);
        }
        h = stripLeadingZeros(h);
        return h;
    }

    private boolean[] multi(boolean[] a, boolean[] b) {
        boolean[][] c = new boolean[b.length][a.length];
        for (int d = 0; d < b.length; d++) {
            if (b[d]) {
                c[d] = a;
            } else {
                boolean[] e = {};
                e = addbegintil(e, a.length);
                c[d] = e;
            }
            if (verbose) {
                print(c[d]);
            }
        }
        for (int d = 0; d < b.length; d++) {
            for (int e = 0; e < b.length - d - 1; e++) {
                c[d] = addend(c[d], false);
            }
            if (verbose) {
                print(c[d]);
            }
        }
        boolean[] d = {};
        for (int e = 0; e < c.length; e++) {
            d = ad(d, c[e]);

        }
        if (d.length == 0) {
            boolean[] e = {false};
            return e;
        }
        d = stripLeadingZeros(d);
        return d;
    }

    private boolean greater(boolean[] a, boolean[] b) {
        boolean[] c = a;
        boolean[] d = b;
        d = stripLeadingZeros(d);
        c = stripLeadingZeros(c);
        if (c.length > d.length) {
            return true;
        }
        if (d.length > c.length) {
            return false;
        }
        for (int e = 0; e < c.length; e++) {
            if (c[e] && !d[e]) {
                return true;
            }
            if (d[e] && !c[e]) {
                return false;
            }
        }
        return false;
    }

    private boolean equal(boolean[] a, boolean[] b) {
        boolean[] c = a;
        boolean[] d = b;
        while (c.length != 1 && !c[0]) {
            c = delbegin(c);
        }
        while (d.length != 1 && !d[0]) {
            d = delbegin(d);
        }
        if (c.length > d.length) {
            return false;
        }
        if (d.length > c.length) {
            return false;
        }
        for (int e = 0; e < c.length; e++) {
            if (c[e] != d[e]) {
                return false;
            }
        }
        return true;
    }

    private boolean less(boolean[] a, boolean[] b) {
        return greater(b, a);
    }

    private boolean[] mod(boolean[] a, boolean[] b) {
        if (verbose) {
            print(a);
            print(b);
        }
        boolean[] c = {};
        for (int d = 0; d < a.length; d++) {
            c = addend(c, false);
            c[c.length - 1] = a[d];
            if (verbose) {
                print(c);
            }
            if (greater(c, b) || equal(c, b)) {
                if (verbose) {
                    print(c);
                }
                c = str(c, b);
                if (verbose) {
                    print(c);
                }

            }
        }
        return stripLeadingZeros(c);
    }

    private boolean[] div(boolean[] a, boolean[] b) {
        if (verbose) {
            print(a);
            print(b);
        }
        boolean[] c = new boolean[a.length];
        for (int d = 0; d < c.length; d++) {
            c[d] = false;
        }
        boolean[] d = {};//R
        for (int e = 0; e < a.length; e++) {
            d = addend(d, false);
            d[d.length - 1] = a[e];
            if (verbose) {
                print(d);
            }
            if (greater(d, b) || equal(d, b)) {
                if (verbose) {
                    print(d);
                }
                d = str(d, b);
                if (verbose) {
                    print(d);
                }
                c[e] = true;
            }
        }
        return stripLeadingZeros(c);
    }

    private boolean[] str(boolean[] a, boolean[] b) {
        boolean c = false;
        if (a.length > b.length) {
            c = true;
        }
        boolean[] d = c ? a : addbegintil(a, b.length);
        boolean[] e = c ? addbegintil(b, a.length) : b;
        if (verbose) {
            print(d);
            print(e);
        }
        boolean f = false;
        boolean[] g = new boolean[d.length];
        for (int h = d.length - 1; h != -1; h--) {
            if (verbose) {
                System.out.print(f);
                System.out.print(d[h]);
                System.out.print(e[h]);
            }
            boolean i = (!d[h] && e[h]);
            boolean j = ((d[h] || e[h]) && !(d[h] && e[h]));
            boolean k = ((f || j) && !(f && j));
            f = (i || (f && !j));
            if (verbose) {
                System.out.println(k);
            }
            g[h] = k;
        }
        boolean[] h = f ? addbegin(g, f) : g;
        if (verbose) {
            print(h);
        }
        h = stripLeadingZeros(h);
        return h;
    }

    private boolean[] addbegintil(boolean[] a, int b) {
        boolean[] c = a;
        while (c.length < b) {
            c = addbegin(c, false);
        }
        return c;
    }

    private boolean[] addbegin(boolean[] a, boolean b) {
        boolean[] c = new boolean[a.length + 1];
        System.arraycopy(a, 0, c, 1, a.length);
        c[0] = b;
        return c;
    }

    private boolean[] addend(boolean[] a, boolean b) {
        boolean[] c = new boolean[a.length + 1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length] = b;
        return c;
    }

    private boolean[] delend(boolean[] a) {
        boolean[] b = new boolean[a.length - 1];
        System.arraycopy(a, 0, b, 0, b.length);
        return b;
    }
    
    private static boolean[] delbegin(boolean[] a) {
        if (a.length == 2) {
            boolean[] b = {a[1]};
            return b;
        }
        if (a.length == 1) {
            boolean[] b = {};
            return b;
        }
        if (a.length == 0) {
            return a;
        }
        boolean[] b = new boolean[a.length - 1];
        for (int c = 1; c < a.length; c++) {
            b[c - 1] = a[c];
        }
        return b;
    }

    private static boolean[] stripLeadingZeros(boolean[] a) {
        boolean[] b = a;
        if (a.length == 0) {
            boolean[] c = {false};
            return c;
        }
        while (!b[0] && b.length != 1) {
            b = delbegin(b);
        }
        return b;
    }

    private static String[] addend(String[] a, String b) {
        String[] c = new String[a.length + 1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length] = b;
        return c;
    }

    private static boolean[] inttoboolarray(long a) {
        int b = 0;
        while (Math.pow(2, b) < a || Math.pow(2, b) == a) {
            b++;
        }
        if (verbose) {
            System.out.println(b);
        }
        boolean[] c = new boolean[b];
        long d = a;
        for (int f = c.length - 1; f != -1; f--) {
            int e = c.length - f - 1;
            int g = (int) Math.pow(2, f);
            c[e] = (d >= g);
            if (verbose) {
                System.out.println(f);
                System.out.println(c[e]);
            }
            d -= c[e] ? g : 0;
            if (verbose) {
                System.out.println(d);
                System.out.println();
            }
        }
        return stripLeadingZeros(c);
    }

    private static int boolarraytoint(boolean[] a) {
        int b = 0;
        for (int c = 0; c < a.length; c++) {
            b = b + (a[c] ? (int) Math.pow(2, a.length - c - 1) : 0);
        }
        return b;
    }

    private static boolean[] stringtoboolarray(String a) {
        if (a.length() < 5) {
            BigNumber b = new BigNumber(inttoboolarray(Long.parseLong(a)));
            return b.val;
        }
        boolean[] b = {true, false, true, false};
        boolean[] c = {};
        BigNumber d = new BigNumber(b);
        BigNumber e = new BigNumber(c);
        for (int f = 0; f < a.length(); f++) {
            boolean[] g = stringtoboolarray(a.substring(f, f + 1));
            e = e.add(d.pow(a.length() - f - 1).multiply(new BigNumber(g)));
        }
        return stripLeadingZeros(e.val);
    }

    public void stripZeros() {
        val = stripLeadingZeros(val);
    }

    public void print() {
        System.out.println(this);
    }
    static String[] base = new String[37];

    static {
        String b = "0123456789abcdefghijklmnopqrstuvwxyz ";
        for (int i = 0; i < b.length(); i++) {
            base[i] = b.substring(i, i + 1);
        }
    }

    public String toString(int a) {
        val = stripLeadingZeros(val);
        if (val.length < 5) {
            int b = 0;
            for (int c = 0; c < val.length; c++) {
                b += val[val.length - 1 - c] ? ((int) (Math.pow(2, c))) : 0;
            }
            return Integer.toString(b, a);
        }
        BigNumber b = new BigNumber(val);
        b.stripZeros();
        String[] c = new String[0];
        int d = 0;
        BigNumber e = new BigNumber(0);
        boolean[] f = inttoboolarray(a);
        while (b.val.length != 1 || b.val[0]) {
            BigNumber g = new BigNumber(f);
            e = b.mod(g);
            b = b.divide(g);
            b.stripZeros();
            c = addend(c, base[boolarraytoint(e.val)]);
        }
        String g = "";
        for (int h = 0; h < c.length; h++) {
            if (c[h] != null) {
                g = c[h] + g;
            }
        }
        return g;
    }

    public String toString() {
        return toString(10);
    }

    public String toStringBase10() {//Not used. Use toString(10)
        val = stripLeadingZeros(val);
        if (val.length < 5) {
            int a = 0;
            for (int b = 0; b < val.length; b++) {
                a += val[val.length - 1 - b] ? ((int) (Math.pow(2, b))) : 0;
            }
            return Integer.toString(a);
        }
        BigNumber a = new BigNumber(val);
        a.stripZeros();
        String[] c = new String[0];
        int d = 0;
        BigNumber e = new BigNumber(0);
        while (a.val.length != 1 || a.val[0]) {
            BigNumber f = new BigNumber("1010", 2);
            e = a.mod(f);
            a = a.divide(f);
            a.stripZeros();
            c = addend(c, e.toString());
        }
        String f = "";
        for (int g = 0; g < c.length; g++) {
            if (c[g] != null) {
                f = c[g] + f;
            }
        }
        return f;
    }

    public static boolean[] fromBase(String a, int b) {
        if (a.length() == 1) {
            for (int c=0; c<base.length; c++){
                if (base[c].equals(a)){
                    return inttoboolarray(c);
                }
            }
        }
        boolean[] c = (new BigNumber(b)).val;
        boolean[] d = {};
        BigNumber e = new BigNumber(c);
        BigNumber f = new BigNumber(d);
        for (int g = 0; g < a.length(); g++) {
            boolean[] h = fromBase(a.substring(g, g + 1), b);
            f = f.add(e.pow(a.length() - g - 1).multiply(new BigNumber(h)));
        }
        return stripLeadingZeros(f.val);
    }

    public static void print(boolean[] a) {
        a = stripLeadingZeros(a);
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i]);
        }
        System.out.println();
    }
}
