/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankyproxyu;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 *
 * @author leijurv
 */
public class JankyProxyU {
    static String DLoc = "10.1.10.102";
    static int DPort = 12345;
    static Socket s;
    static Socket ssh;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            try {
                s = new Socket(DLoc, DPort);
                ssh = null;
                InputStream fromDown = s.getInputStream();
                OutputStream toDown = s.getOutputStream();
                new Thread() {
                    public void run() {
                        OutputStream toLoc = null;
                        while (true) {
                            try {
                                int i = fromDown.read();
                                if (i < 0) {
                                    System.out.println("fromdown returned " + i);
                                    s.close();
                                    s = null;
                                    break;
                                }
                                if (ssh == null) {
                                    System.out.println("Creating ssh");
                                    ssh = new Socket("localhost", 22);
                                    toLoc = ssh.getOutputStream();
                                }
                                if (toLoc == null) {
                                    toLoc = ssh.getOutputStream();
                                }
                                toLoc.write(i);
                            } catch (IOException io) {
                                if (ssh != null) {
                                    try {
                                        System.out.println("Closing ssh");
                                        ssh.close();
                                    } catch (IOException ex) {
                                    }
                                }
                                if (s != null) {
                                    try {
                                        s.close();
                                    } catch (IOException ex) {
                                    }
                                }
                                break;
                            }
                        }
                    }
                }.start();
                while (ssh == null && s != null) {
                    Thread.sleep(100);
                }
                if (s == null) {
                    if (ssh != null) {
                        System.out.println("Closing ssh");
                        ssh.close();
                    }
                }
                InputStream fromLoc = ssh.getInputStream();
                while (true) {
                    int i = fromLoc.read();
                    if (i < 0) {
                        System.out.println("fromLoc returned " + i);
                        s.close();
                        break;
                    }
                    toDown.write(i);
                }
                ssh.close();
                s.close();
            } catch (Exception e) {
                try {
                    s.close();
                } catch (Exception ex) {
                }
                try {
                    System.out.println("Closing ssh");
                    ssh.close();
                } catch (Exception ex) {
                }
                Thread.sleep(5000);
            }
        }
    }
}
