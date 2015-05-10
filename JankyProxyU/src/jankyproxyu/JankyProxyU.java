/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankyproxyu;
import java.io.*;
import java.net.Socket;
/**
 *
 * @author leijurv
 */
public class JankyProxyU {
    static String DLoc = "10.1.10.102";
    static int DPort;
    static int MPort;
    static volatile int numEmptyConn = 0;//number of connections to D that are currently empty, and don't have a matching
    static volatile int numSSHConn = 0;//number of ssh connections to localhost
    static PrintWriter logOut;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DPort = Integer.parseInt(args[0]);
        MPort = Integer.parseInt(args[1]);
        String file = System.getProperty("user.home") + "/log" + DPort + "-" + MPort + ".log";
        try {
            logOut = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        } catch (IOException e) {
        }
        new Connector().start();
        new Connector().start();
    }
    public static class Connector extends Thread {
        Socket s;
        Socket ssh;
        public void doIt() {
            boolean dc = true;
            try {
                logOut.println("Trying to connect");
                logOut.flush();
                s = new Socket(DLoc, DPort);
                ssh = null;
                final InputStream fromDown = s.getInputStream();
                OutputStream toDown = s.getOutputStream();
                dc = false;
                numEmptyConn++;
                logOut.println("!NE: " + numEmptyConn + "  NS: " + numSSHConn);
                logOut.flush();
                if (numEmptyConn < 2) {
                    logOut.println("starting");
                    logOut.flush();
                    new Connector().start();
                }
                int firstByte = fromDown.read();//wait for first byte before creating ssh
                numEmptyConn--;
                if (firstByte < 0) {
                    return;
                }
                toDown.write(5);
                if (numEmptyConn < 2) {
                    logOut.println("starting");
                    logOut.flush();
                    new Connector().start();
                }
                logOut.println("Creating ssh");
                logOut.flush();
                ssh = new Socket("localhost", MPort);
                numSSHConn++;
                logOut.println("#NE: " + numEmptyConn + "  NS: " + numSSHConn);
                logOut.flush();
                final OutputStream toLoc = ssh.getOutputStream();
                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                int i = fromDown.read();
                                if (i < 0) {
                                    logOut.println("fromdown returned " + i);
                                    logOut.flush();
                                    s.close();
                                    ssh.close();
                                    logOut.println("successfully closed both");
                                    logOut.flush();
                                    break;
                                }
                                toLoc.write(i);
                            } catch (IOException io) {
                                if (ssh != null) {
                                    try {
                                        logOut.println("Closing ssh because of fromdown exception " + io);
                                        logOut.flush();
                                        ssh.close();
                                    } catch (IOException ex) {
                                    }
                                }
                                if (s != null) {
                                    try {
                                        logOut.println("Closing down because of thing " + io);
                                        logOut.flush();
                                        s.close();
                                    } catch (IOException ex) {
                                    }
                                }
                                break;
                            }
                        }
                    }
                }.start();
                InputStream fromLoc = ssh.getInputStream();
                while (true) {
                    int i = fromLoc.read();
                    if (i < 0) {
                        logOut.println("fromLoc returned " + i);
                        logOut.flush();
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
                    logOut.println("Closing ssh as cleanup");
                    logOut.flush();
                    ssh.close();
                } catch (Exception ex) {
                }
                logOut.println("wait: " + dc);
                logOut.flush();
                if (dc) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
        @Override
        public void run() {
            doIt();
            if (ssh != null) {
                numSSHConn--;
            }
            if (numEmptyConn < 2) {
                new Connector().start();
            }
            logOut.println("@NE: " + numEmptyConn + "  NS: " + numSSHConn);
            logOut.flush();
        }
    }
}
