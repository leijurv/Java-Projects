/*
 * Copyright 2014 Lurf Jurv
 * All rights reserved
 */
package cat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author leijurv
 */
public class CAT {
    byte[] state;
    int i;
    int n;
    MessageDigest md;
    byte[] origState;
    public CAT(byte[] pwd, boolean streaming, MessageDigest M, int N) {
        md = M;
        md.reset();
        state = md.digest(pwd);
        md.reset();
        if (!streaming) {
            origState = new byte[state.length];
            System.arraycopy(state, 0, origState, 0, state.length);
        }
        i = 0;
        n = N;
    }
    public CAT(byte[] pwd, boolean streaming, MessageDigest M) {
        this(pwd, streaming, M, M.getDigestLength() / 2);
    }
    public CAT(byte[] pwd, boolean streaming) {
        this(pwd, streaming, getDefaultMessageDigest());
    }
    public CAT(byte[] pwd) {
        this(pwd, true);
    }
    public CAT(String pwd) {
        this(pwd.getBytes());
    }
    public static MessageDigest getDefaultMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("This platform doesn't support SHA-512");
        }
    }
    public byte encode(byte input) {
        byte result = (byte) (input ^ state[i]);
        state[i] = input;
        i++;
        if (i == n) {
            i = 0;
            state = md.digest(state);
            md.reset();
        }
        return result;
    }
    public byte decode(byte input) {
        byte result = (byte) (input ^ state[i]);
        state[i] = result;
        i++;
        if (i == n) {
            i = 0;
            state = md.digest(state);
            md.reset();
        }
        return result;
    }
    private byte[] encode1(byte[] input) {
        if (origState != null) {
            resetState();
        }
        byte[] result = new byte[input.length];
        int offset = 0;
        while (i != 0) {
            result[offset] = encode(input[offset]);
            offset++;
        }
        for (int x = offset; x < result.length + offset; x += n) {
            int min = Math.min(n, result.length - x);
            for (int II = 0; II < min; II++) {
                result[II + x] = (byte) (input[x + II] ^ state[II]);
                state[II] = input[II + x];
            }
            if (n + x > result.length) {
                i = result.length - x;
            } else {
                state = md.digest(state);
                md.reset();
            }
        }
        return result;
    }
    private byte[] decode1(byte[] input) {
        if (origState != null) {
            resetState();
        }
        byte[] result = new byte[input.length];
        int offset = 0;
        while (i != 0) {
            result[offset] = encode(input[offset]);
            offset++;
        }
        for (int x = offset; x < result.length + offset; x += n) {
            int min = Math.min(n, result.length - x);
            for (int II = 0; II < min; II++) {
                result[II + x] = (byte) (input[x + II] ^ state[II]);
                state[II] = result[II + x];
            }
            if (n + x > result.length) {
                i = result.length - x;
            } else {
                state = md.digest(state);
                md.reset();
            }
        }
        return result;
    }
    private byte[] encode2(byte[] input) {
        if (origState != null) {
            resetState();
        }
        byte[] result = new byte[input.length];
        for (int ind = 0; ind < result.length; ind++) {
            result[ind] = encode(input[ind]);
        }
        return result;
    }
    private byte[] decode2(byte[] input) {
        if (origState != null) {
            resetState();
        }
        byte[] result = new byte[input.length];
        for (int ind = 0; ind < result.length; ind++) {
            result[ind] = decode(input[ind]);
        }
        return result;
    }
    public byte[] encode(byte[] input) {
        if (input.length > n * 4) {
            return encode1(input);
        }
        return encode2(input);
    }
    public byte[] decode(byte[] input) {
        if (input.length > n * 4) {
            return decode1(input);
        }
        return decode2(input);
    }
    private void resetState() {
        System.out.println("resetting state");
        state = new byte[origState.length];
        System.arraycopy(origState, 0, state, 0, state.length);
        i = 0;
    }
}
