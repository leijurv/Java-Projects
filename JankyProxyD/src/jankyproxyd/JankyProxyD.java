/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankyproxyd;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author leijurv
 */
public class JankyProxyD {
    static Socket socketFromUp;
    static InputStream fromUp;
    static OutputStream toUp;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        final ServerSocket fromU = new ServerSocket(12345);
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Socket s = fromU.accept();
                        System.out.println("Received new connection from up");
                        try {
                            socketFromUp.close();
                        } catch (Exception e) {
                        }
                        socketFromUp = s;
                        fromUp = s.getInputStream();
                        toUp = s.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        final ServerSocket fromOutside = new ServerSocket(12344);
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        final Socket s = fromOutside.accept();
                        System.out.println("Received new connection from out");
                        try {
                            InputStream fromOut = s.getInputStream();
                            final OutputStream toOut = s.getOutputStream();
                            new Thread() {
                                public void run() {
                                    while (true) {
                                        try {
                                            int i = fromUp.read();
                                            if (i < 0) {
                                                System.out.println("fromUp return " + i);
                                                s.close();
                                                break;
                                            }
                                            toOut.write(i);
                                        } catch (Exception io) {
                                            io.printStackTrace();
                                            try {
                                                s.close();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                }
                            }.start();
                            while (true) {
                                int i = fromOut.read();
                                if (i < 0) {
                                    System.out.println("fromOut returned " + i);
                                    s.close();
                                    break;
                                }
                                toUp.write(i);
                            }
                            if (socketFromUp != null) {
                                socketFromUp.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
