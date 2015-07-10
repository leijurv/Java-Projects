/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        for (int I = 0; I < result.length; I += n) {
            int x = I + offset;
            for (int II = 0; II < n && II + x < result.length; II++) {
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
        for (int I = 0; I < result.length; I += n) {
            int x = I + offset;
            for (int II = 0; II < n && II + x < result.length; II++) {
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
        for (int i = 0; i < result.length; i++) {
            result[i] = encode(input[i]);
        }
        return result;
    }
    private byte[] decode2(byte[] input) {
        if (origState != null) {
            resetState();
        }
        byte[] result = new byte[input.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = decode(input[i]);
        }
        return result;
    }
    public byte[] encode(byte[] input) {
        if (input.length > n * 3000) {
            return encode1(input);
        }
        return encode2(input);
    }
    public byte[] decode(byte[] input) {
        if (input.length > n * 3000) {
            return decode1(input);
        }
        return decode2(input);
    }
    private void resetState() {
        System.out.println("resetting state");
        state = new byte[origState.length];
        for (int I = 0; I < state.length; I++) {
            state[I] = origState[I];
        }
        i = 0;
    }
    public static void print(byte[] b) {
        for (byte B : b) {
            System.out.print(B + ",");
        }
        System.out.println("lol");
    }
    public static void main(String[] args) throws IOException {
        BigInteger serverPrivKey = new BigInteger(256, new Random());
        ECPoint serverPubKey = ECPoint.base.multiply(serverPrivKey);
        ServerSocket ss = new ServerSocket(5021);
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final Socket socket = ss.accept();
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    ECPoint clientPub = new ECPoint(socket.getInputStream());
                                    long bef = System.currentTimeMillis();
                                    long desiredEndTime = bef + 40;
                                    if (!clientPub.verify()) {
                                        System.out.println("Invalid");
                                        socket.close();
                                        return;
                                    }
                                    ECPoint shared = clientPub.multiply(serverPrivKey);
                                    while (System.currentTimeMillis() < desiredEndTime) {//Defeat timing attacks by making the initial handshake take 40ms no matter what
                                        Thread.sleep(1);
                                    }
                                    System.out.println(desiredEndTime - System.currentTimeMillis());
                                    System.out.println("Shared: " + shared);
                                    System.out.println("ClientPub: " + clientPub);
                                    DataInputStream in = new DataInputStream(new CatInputStream(socket.getInputStream(), new CAT(shared.toString())));
                                    DataOutputStream out = new DataOutputStream(new CatOutputStream(socket.getOutputStream(), new CAT(shared.toString())));
                                    while (true) {
                                        String message = in.readUTF();
                                        String response = "Your message was " + message + ", lol. ";
                                        out.writeUTF(response);
                                    }
                                } catch (IOException | InterruptedException ex) {
                                    Logger.getLogger(CAT.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.start();
                        break;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(CAT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        other(serverPubKey);
    }
    public static void other(ECPoint serverPubKey) throws IOException {
        Socket socket = new Socket("localhost", 5021);
        BigInteger myPrivKey = new BigInteger(257, new Random());
        ECPoint myPubKey = ECPoint.base.multiply(myPrivKey);
        System.out.println("MyPub: " + myPubKey);
        ECPoint shared = serverPubKey.multiply(myPrivKey);
        System.out.println("Shared: " + shared);
        myPubKey.write(socket.getOutputStream());
        DataInputStream in = new DataInputStream(new CatInputStream(socket.getInputStream(), new CAT(shared.toString())));
        DataOutputStream out = new DataOutputStream(new CatOutputStream(socket.getOutputStream(), new CAT(shared.toString())));
        out.writeUTF("nhoaeduihtaoeiuthaeodithnaoedthudaoehtnudaonethduitnhaoeduithaoedihtaoeiuthanoeduhtaoedihaoeduhtnoaeduhtnaoeidthnoeadiuhtnoadthidoaehtidhtoeaiuoetdhuaeo");
        System.out.println(in.readUTF());
    }
    public static void main1(String[] args) {
    }
}
