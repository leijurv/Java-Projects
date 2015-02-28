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
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author leijurv
 */
public class RingSignatures {
    public static byte[] encrypt(byte[] key, byte[] input) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }
    public static byte[] decrypt(byte[] key, byte[] input) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }
    public static byte[] hash(byte[] message, int size) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message);
        byte[] d = digest.digest();
        byte[] res = new byte[size / 8];
        int dlen = d.length;
        for (int i = 0; i < res.length / dlen; i++) {
            for (int j = 0; j < dlen; j++) {
                res[i * dlen + j] = d[j];
            }
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
        Random r = new Random();
        RSAKeyPair[] keys = new RSAKeyPair[10];
        int bitlength = 1024;
        int s = 2;
        for (int i = 0; i < keys.length; i++) {
            keys[i] = RSAKeyPair.generate(new BigInteger(bitlength / 2 - 4, 10, r), new BigInteger(bitlength / 2, 10, r));
            if (i != s) {
                keys[i].pri = null;
            }
        }
        byte[][] sig = genRing(keys, "kush".getBytes(), 1024, r);
        byte[] v = sig[sig.length - 1];
        BigInteger[] x = new BigInteger[sig.length - 1];
        BigInteger[] y = new BigInteger[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = new BigInteger(leadingZero(sig[i]));
            y[i] = keys[i].encode(x[i], bitlength);
        }
        System.out.println("If this is a 0 it worked: " + new BigInteger(runCKV(hash("kush".getBytes(), 256), v, y)).compareTo(new BigInteger(v)));
    }
    public static byte[][] genRing(RSAKeyPair[] keys, byte[] message, int b, Random r) throws Exception {
        byte[] k = hash(message, 256);
        int s = -1;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].pri != null) {//Find the one that we have the private key to
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
        System.out.println("If this is a 0 it worked: " + new BigInteger(check).compareTo(new BigInteger(v)));
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
        if (len < b.length) {
            b = trimLeading(b);
            return xor(a, b);
            //throw new IllegalStateException("Shrek likes waffles" + len + " " + b.length);
        }
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }
        return result;
    }
    public static byte[] trimLeading(byte[] b) {
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
