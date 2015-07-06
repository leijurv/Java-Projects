/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 *
 * @author leijurv
 */
public class ECPoint {

    public static final ECPoint base = new ECPoint(new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798".toLowerCase(), 16), new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8".toLowerCase(), 16));
    public static final BigInteger modulus = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F".toLowerCase(), 16);
    private static final BigInteger three = new BigInteger("3");
    private static final BigInteger two = new BigInteger("2");
    private static final BigInteger seven = new BigInteger("7");
    private final BigInteger x;
    private final BigInteger y;

    public ECPoint(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;

    }

    public ECPoint(InputStream input) throws IOException {
        byte[] pointX = new byte[32];
        byte[] pointY = new byte[32];
        input.read(pointX);
        input.read(pointY);
        byte[] normalX = new byte[33];
        byte[] normalY = new byte[33];
        System.arraycopy(pointX, 0, normalX, 1, 32);
        System.arraycopy(pointY, 0, normalY, 1, 32);//Add a zero byte to the beginning to prevent biginteger from interpreting a leading 1 byte as two's complement negative
        this.x = new BigInteger(normalX);
        this.y = new BigInteger(normalY);
    }

    public ECPoint add(ECPoint q) {
        BigInteger s;
        if (q.x.equals(x)) {
            if (q.y.add(y).equals(modulus)) {
                throw new IllegalStateException("kush");
            }
            s = three.multiply(x.pow(2)).subtract(modulus).multiply(y.multiply(two).modPow(new BigInteger("-1"), modulus)).mod(modulus);
        } else {
            s = y.subtract(q.y).add(modulus).multiply(x.subtract(q.x).modPow(new BigInteger("-1"), modulus)).mod(modulus);
        }
        BigInteger xR = s.pow(2).subtract(x).subtract(q.x).mod(modulus);
        BigInteger yR = modulus.subtract(y.add(s.multiply(xR.subtract(x))).mod(modulus));
        return new ECPoint(xR, yR);
    }

    public ECPoint multiply(BigInteger r) {
        if (r.equals(BigInteger.ONE)) {
            return this;
        }
        if (r.mod(two).equals(BigInteger.ZERO)) {
            ECPoint d = add(this);
            return d.multiply(r.divide(two));
        }
        return add(multiply(r.subtract(BigInteger.ONE)));
    }

    public boolean verify() {
        return verify(x, y);
    }

    public static boolean verify(BigInteger x, BigInteger y) {
        return x.modPow(three, modulus).add(seven).mod(modulus).equals(y.modPow(two, modulus)) && x.equals(x.mod(modulus)) && y.equals(y.mod(modulus));
    }

    @Override
    public String toString() {
        return x.toString(16) + "," + y.toString(16);
    }

    public static byte[] toNormal(byte[] input) {//Trim leading or add leading to make length 32.
        if (input.length == 32) {
            return input;
        }
        byte[] result = new byte[32];
        if (input.length > 32) {
            int off = input.length - 32;
            System.arraycopy(input, off, result, 0, 32);
            return result;
        }
        int off = 32 - input.length;
        System.arraycopy(input, 0, result, off, input.length);
        return result;
    }

    public void write(OutputStream out) throws IOException {
        out.write(toNormal(x.toByteArray()));
        out.write(toNormal(y.toByteArray()));
    }
}
