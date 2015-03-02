/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ring.signatures;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author leijurv
 */
public class RingSignatures {
    public static byte[] encrypt(byte[] key, byte[] input) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }
    public static byte[] decrypt(byte[] key, byte[] input) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }
    public static byte[] hash(byte[] message, int size) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message);
        byte[] d = digest.digest();
        byte[] res = new byte[size / 8];//Extend this hash out to however long it needs to be
        int dlen = d.length;
        for (int i = 0; i < res.length / dlen; i++) {
            System.arraycopy(d, 0, res, i * dlen, dlen);
        }
        return res;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        /*
         System.out.println(System.getProperty("java.home"));
         RSAKeyPair dank = RSAKeyPair.generate(new BigInteger("61"), new BigInteger("53"), new BigInteger("17"));
         System.out.println(dank.encode(new BigInteger("65000000"), 100));
         System.out.println(dank.decode(new BigInteger("65001491"), 100));
         byte[] key = new BigInteger("5021").toByteArray(); // TODO
         byte[] input = new BigInteger("5021").toByteArray(); // TODO
         byte[] output = encrypt(hash(key), input);
         System.out.println(new BigInteger(decrypt(hash(key), encrypt(hash(key), input))));*/
        ArrayList<RSAKeyPair> keyOptions = new ArrayList<RSAKeyPair>();
        int bitlength = 1024;
        Random r = new Random(5021);
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            keyOptions.add(RSAKeyPair.generate(new BigInteger(bitlength / 2 - 3, 8, r), new BigInteger(bitlength / 2, 8, r)));
        }
        while (true) {
            int numKeys = r.nextInt(100) + 1;
            RSAKeyPair[] keys = new RSAKeyPair[numKeys];
            int s = r.nextInt(numKeys);
            byte[] message = new byte[8];
            r.nextBytes(message);
            for (int i = 0; i < keys.length; i++) {
                keys[i] = keyOptions.remove(r.nextInt(keyOptions.size()));
                if (i != s) {
                    keys[i] = keys[i].withoutPriv();//We only need one of the private keys
                }
            }
            System.out.println("Creating and verifying");
            long time = System.currentTimeMillis();
            byte[][] sig = genRing(keys, message, bitlength, r);
            boolean b = verify(sig, keys, message, bitlength);
            System.out.println(b + " " + numKeys + " " + s + " " + (System.currentTimeMillis() - time));
            if (!b) {
                return;
            }
        }
    }
    public static boolean verify(byte[][] sig, RSAKeyPair[] keys, byte[] message, int bitlength) throws Exception {
        byte[] k = hash(message, 256);
        byte[] v = sig[sig.length - 1];
        BigInteger[] x = new BigInteger[sig.length - 1];
        BigInteger[] y = new BigInteger[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = new BigInteger(leadingZero(sig[i]));
            y[i] = keys[i].encode(x[i], bitlength);
        }
        byte[] res = runCKV(k, v, y);
        return new BigInteger(res).equals(new BigInteger(v));
    }
    public static byte[][] genRing(RSAKeyPair[] keys, byte[] message, int b, Random r) throws Exception {
        byte[] k = hash(message, 256);
        int s = -1;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].hasPrivate()) {//Find the one that we have the private key to
                if (s != -1) {
                    throw new IllegalStateException("Too many private keyssss");
                }
                s = i;
            }
        }
        if (s == -1) {
            throw new IllegalStateException("Need at least 1 private key to create a ring signature");
        }
        byte[] v = new byte[b / 8];//Number of bytes = number of bits / 8
        r.nextBytes(v);//Maybe this should be more random?
        BigInteger[] x = new BigInteger[keys.length];
        BigInteger[] y = new BigInteger[keys.length];
        for (int i = 0; i < keys.length; i++) {
            if (i != s) {//Do this for everyone but me, mine is generated later
                x[i] = new BigInteger(b, r);
                y[i] = keys[i].encode(x[i], b);
            }
        }
        byte[] CKV = solveCKV(k, v, y, s);
        //System.out.println(CKV.length);
        //System.out.println(CKV[0]);
        y[s] = new BigInteger(leadingZero(CKV));
        x[s] = keys[s].decode(y[s], b);
        //System.out.println("YS: " + y[s]);
        byte[] check = runCKV(k, v, y);
        int d = new BigInteger(check).compareTo(new BigInteger(v));
        if (d != 0) {
            throw new IllegalStateException("Shrek");
        }
        byte[][] result = new byte[keys.length + 1][];
        for (int i = 0; i < keys.length; i++) {
            byte[] X = x[i].toByteArray();
            if (X.length == 129) {
                X = trimLeading(X);
            }
            result[i] = X;
        }
        result[keys.length] = v;
        return result;
    }
    public static byte[] solveCKV(byte[] k, byte[] v, BigInteger[] y, int s) throws Exception {
        byte[] shouldBe = v;
        int r = y.length;
        shouldBe = decrypt(k, shouldBe);
        for (int i = r - 1; i > s; i--) {
            shouldBe = xor(shouldBe, y[i].toByteArray());
            shouldBe = decrypt(k, shouldBe);
        }
        byte[] of = v;
        for (int i = 0; i < s; i++) {
            of = xor(of, y[i].toByteArray());
            of = encrypt(k, of);
        }
        return xor(shouldBe, of);
    }
    public static byte[] runCKV(byte[] k, byte[] v, BigInteger[] y) throws Exception {
        byte[] temp = v;
        for (int i = 0; i < y.length; i++) {
            temp = xor(temp, y[i].toByteArray());
            temp = encrypt(k, temp);
        }
        return temp;
    }
    public static byte[] xor(byte[] a, byte[] b) {
        int len = a.length;
        if (len != 128) {
            throw new IllegalStateException("Can't xor anything other than 128 bites");
        }
        while (len < b.length) {
            b = trimLeading(b);
            //return xor(a, b);
            //throw new IllegalStateException("Shrek likes waffles" + len + " " + b.length);
        }
        while (len > b.length) {
            b = leadingZero(b);
            //return xor(a, b);
        }
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }
        return result;
    }
    public static byte[] trimLeading(byte[] b) {
        if (b[0] != 0) {
            throw new IllegalStateException("Attempting to trim " + b[0]);
        }
        byte[] res = new byte[b.length - 1];
        for (int i = 0; i < res.length; i++) {
            res[i] = b[i + 1];
        }
        return res;
    }
    public static byte[] leadingZero(byte[] b) {
        byte[] res = new byte[b.length + 1];
        for (int i = 0; i < b.length; i++) {
            res[i + 1] = b[i];
        }
        res[0] = 0;
        return res;
    }
}
