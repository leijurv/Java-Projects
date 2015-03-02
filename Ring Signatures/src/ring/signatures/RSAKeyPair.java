package ring.signatures;
import java.math.BigInteger;
public class RSAKeyPair {
    public static final BigInteger defaultPub = new BigInteger("65537");
    public static final BigInteger three = new BigInteger("3");
    public static final BigInteger two = new BigInteger("2");
    public static final BigInteger one = BigInteger.ONE;
    public static final BigInteger zero = BigInteger.ZERO;
    public final BigInteger modulus;
    public final BigInteger pub;
    private final BigInteger pri;
    public static RSAKeyPair generate(BigInteger p, BigInteger q) {
        return generate(p, q, defaultPub);
    }
    public static RSAKeyPair generate(BigInteger p, BigInteger q, BigInteger e) {
        return new RSAKeyPair(p, q, e, false);
    }
    protected RSAKeyPair(BigInteger modulus) {
        pub = defaultPub;
        this.modulus = modulus;
        pri = null;
    }
    protected RSAKeyPair(BigInteger pri, BigInteger modulus) {
        pub = defaultPub;
        this.pri = pri;
        this.modulus = modulus;
    }
    protected RSAKeyPair(BigInteger pri, BigInteger pub, BigInteger modulus) {
        this.pri = pri;
        this.pub = pub;
        this.modulus = modulus;
    }
    protected RSAKeyPair(BigInteger p, BigInteger q, BigInteger e, boolean update) {
        this(p, q, e, update, false);
    }
    protected RSAKeyPair(BigInteger p, BigInteger q, BigInteger e, boolean update, boolean delPriv) {
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
        while (t.compareTo(e) == -1 || !prime(e) || t.mod(e).equals(zero)) {
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
        if (!delPriv) {
            if (update) {
                //System.out.println("Calculated k: " + k.toString());
                System.out.println("Calculating d...");
            }
            //pri = k.multiply(t).add(one).divide(e);
            pri = e.modPow(zero.subtract(one), t);
            if (update) {
                System.out.println("Calculated d: " + pri.toString());
            }
        } else {
            pri = null;
        }
    }
    public String toString() {
        return "N:" + modulus + ", E:" + pub + ", D:" + pri;
    }
    private static boolean prime(long t) {
        if (t < 4) {
            if (t > 1) {
                return true;
            }
            return false;
        }
        if (t % 2 == 0) {
            return false;
        }
        long i = 3;
        long sq = (long) Math.sqrt(t);
        while (i <= sq) {
            if (t % i == 0) {
                return false;
            }
            i += 2;
        }
        return true;
    }
    private static boolean prime(BigInteger t) {//For SMALL numbers
        //TODO: Add AKS primality search
        if (t.compareTo(new BigInteger("" + Long.MAX_VALUE)) == -1) {//If it fits in a long then do so because longs are so much faster
            return prime(t.longValue());
        }
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
    public BigInteger decode(BigInteger thing) {
        return thing.modPow(pri, modulus);
    }
    public BigInteger encode(BigInteger thing) {
        return thing.modPow(pub, modulus);
    }
    public BigInteger encode(BigInteger thing, int numbits) {
        BigInteger p = two.pow(numbits);//this is pretty inefficient i think
        BigInteger[] div = thing.divideAndRemainder(modulus);
        BigInteger quotient = div[0];
        BigInteger remainder = div[1];
        if (quotient.add(one).multiply(modulus).compareTo(p) != 1) {//(Q+1)*N<=2^bits
            return quotient.multiply(modulus).add(encode(remainder));
        }
        return thing;
    }
    public BigInteger decode(BigInteger thing, int numbits) {
        BigInteger p = two.pow(numbits);//this is pretty inefficient i think
        BigInteger[] div = thing.divideAndRemainder(modulus);
        BigInteger quotient = div[0];
        BigInteger remainder = div[1];
        if (quotient.add(one).multiply(modulus).compareTo(p) != 1) {//(Q+1)*N<=2^bits
            return quotient.multiply(modulus).add(decode(remainder));
        }
        throw new IllegalStateException("dank");
    }
    public boolean hasPrivate() {
        return pri != null;
    }
    public RSAKeyPair withoutPriv() {
        return new RSAKeyPair(null, pub, modulus);
    }
}
