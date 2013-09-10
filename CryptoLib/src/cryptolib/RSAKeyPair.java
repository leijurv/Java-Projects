package cryptolib;

import java.math.BigInteger;

public class RSAKeyPair {

    BigInteger three = new BigInteger("3");
    BigInteger two = new BigInteger("2");
    BigInteger one = BigInteger.ONE;
    BigInteger zero = BigInteger.ZERO;
    public BigInteger modulus = zero;
    public BigInteger pub = zero;
    public BigInteger pri = zero;
    public String toString(){
        return "N:"+modulus+", E:"+pub+", D:"+pri;
    }
    private boolean prime(BigInteger t) {//For SMALL numbers
        //TODO: Add AKS primality search
        if (t.mod(two).equals(zero)) {
            return false;
        }
        BigInteger i = three;
        while (i.multiply(i).compareTo(t) != 1) {
            if (t.mod(i).equals(zero)) {
                return false;
            }
            i = i.add(two);
        }
        return true;
    }

    public void generate(BigInteger p, BigInteger q, BigInteger e, boolean update) {
        if (update) {
            System.out.println("Assuming P and Q are prime...");
            System.out.println("Generating KeyPair with updates. P=" + p.toString() + ",Q=" + q.toString() + ",E=" + e.toString());
            System.out.println("Calculating t... (p-1*q-1)");
        }
        BigInteger t = (p.subtract(one)).multiply(q.subtract(one));
        if (update) {
            System.out.println("Calculated t: " + t.toString());
            System.out.println("Finding closest prime to e...");
        }
        while (t.compareTo(e) == -1 || !prime(e)) {
            e = e.subtract(one);
        }
        if (update) {
            System.out.println("Calculated e: " + e.toString());
            System.out.println("Checking if t is divisible by e...");
        }
        if (t.mod(e).equals(zero)) {
            throw new RuntimeException("T is divisible by E. Try again. (This is pretty rare)");
        }
        if (update) {
            System.out.println("t is not divisible by e");
            System.out.println("Calculating n (p*q)");
        }
        modulus = p.multiply(q);
        pub = e;
        
        if (update) {
            System.out.println("Calculated n: " + modulus.toString());
            System.out.println("Calculating decoding coefficents...");
        }
        /*
        BigInteger k = one;
        while (!(((k.multiply(t)).add(one)).mod(e)).equals(zero)) {
            k = k.add(one);
        }*/
        if (update) {
            //System.out.println("Calculated k: " + k.toString());
            System.out.println("Calculating d...");
        }
        //pri = k.multiply(t).add(one).divide(e);
        pri=e.modPow(zero.subtract(one),t);
        if (update) {
            System.out.println("Calculated d: " + pri.toString());
        }
    }

    public String[] tostring() {
        String[] ans = new String[3];
        ans[0] = modulus.toString();
        if (pub.equals(zero)) {
            ans[1] = "0";
        } else {
            ans[1] = "1" + pub.toString();
        }
        if (pri.equals(zero)) {
            ans[2] = "0";
        } else {
            ans[2] = "1" + pri.toString();
        }
        return ans;
    }
    public BigInteger encode(BigInteger thing) {
        if (modulus.equals(zero) || pub.equals(zero)) {
            return zero;
        }
        return fullencode(thing);
    }

    public BigInteger decode(BigInteger thing) {
        if (modulus.equals(zero) || pri.equals(zero)) {
  
            return zero;
        }
        return fulldecode(thing);
    }

    private BigInteger fulldecode(BigInteger thing) {
        return thing.modPow(pri, modulus);
    }

    private BigInteger fullencode(BigInteger thing) {
        return thing.modPow(pub, modulus);
    }

    public RSAKeyPair() {
    }
}