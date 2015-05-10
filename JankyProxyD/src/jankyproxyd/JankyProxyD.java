/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankyproxyd;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class JankyProxyD {
    static ServerSocket fromOutside = null;
    static PrintWriter logOut;
    static ArrayList<FromUp> fromUpConnections = new ArrayList<FromUp>();
    static final Object fromUpLock = new Object();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int fromUp = Integer.parseInt(args[0]);
        int fromOut = Integer.parseInt(args[1]);
        String file = System.getProperty("user.home") + "/log" + fromUp + "-" + fromOut + ".log";
        try {
            logOut = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        final ServerSocket fromU = new ServerSocket(fromUp);
        fromOutside = new ServerSocket(fromOut);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket s = fromU.accept();
                        FromUp fromUp = new FromUp(s);
                        logOut.println("Got connection from up, adding to queue..." + fromUp + "    " + s);
                        logOut.flush();
                        synchronized (fromUpLock) {
                            fromUpConnections.add(fromUp);
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket fromOut = fromOutside.accept();
                        logOut.println("Got from out " + fromOut);
                        logOut.flush();
                        FromUp fromUp = getValidFromUp();
                        while (fromUp == null) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                            }
                            logOut.println("Trying again... " + fromOut);
                            fromUp = getValidFromUp();
                        }
                        logOut.println("matching " + fromOut + " with from up: " + fromUp);
                        logOut.flush();
                        fromUp.socketFromOut = fromOut;
                        fromUp.start();
                        logOut.println("Valid fromups: " + fromUpConnections);
                        logOut.flush();
                    } catch (IOException ex) {
                    }
                }
            }
        }.start();
    }
    public static FromUp getValidFromUp() {
        synchronized (fromUpLock) {
            while (!fromUpConnections.isEmpty()) {
                FromUp fromUp = fromUpConnections.remove(0);
                if (!fromUp.isGoing && fromUp.socketFromUp.isConnected() && !fromUp.socketFromUp.isClosed() && !fromUp.socketFromUp.isInputShutdown() && !fromUp.socketFromUp.isOutputShutdown()) {
                    logOut.println("passed tests: " + fromUp);
                    logOut.flush();
                    try {
                        fromUp.socketFromUp.getOutputStream().write(5);
                        logOut.println("written " + fromUp);
                        logOut.flush();
                        int val = fromUp.socketFromUp.getInputStream().read();
                        logOut.println("got resp " + val);
                        logOut.flush();
                        if (val >= 0) {
                            return fromUp;
                        }
                        //if (!fromUp.isGoing && fromUp.socketFromUp.isConnected() && !fromUp.socketFromUp.isClosed() && !fromUp.socketFromUp.isInputShutdown() && !fromUp.socketFromUp.isOutputShutdown()) {
                        //  return fromUp;
                        //}
                    } catch (IOException e) {
                        logOut.println("not picking " + fromUp + " because of " + e);
                        logOut.flush();
                    }
                }
                logOut.println("Skipping " + fromUp);
            }
        }
        return null;
    }
    public static class FromUp extends Thread {
        final Socket socketFromUp;
        final InputStream fromUp;
        final OutputStream toUp;
        volatile boolean isGoing = false;
        public Socket socketFromOut = null;
        public FromUp(Socket socketFromUp) throws IOException {
            this.socketFromUp = socketFromUp;
            fromUp = socketFromUp.getInputStream();
            toUp = socketFromUp.getOutputStream();
        }
        @Override
        public void run() {
            try {
                isGoing = true;
                try {
                    InputStream fromOut = socketFromOut.getInputStream();
                    final OutputStream toOut = socketFromOut.getOutputStream();
                    new Thread() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    int i = fromUp.read();
                                    if (i < 0) {
                                        logOut.println(this + "fromUp return " + i);
                                        logOut.flush();
                                        socketFromOut.close();
                                        break;
                                    }
                                    toOut.write(i);
                                } catch (Exception io) {
                                    try {
                                        socketFromOut.close();
                                    } catch (Exception ex) {
                                    }
                                    try {
                                        socketFromUp.close();
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                            }
                        }
                    }.start();
                    while (true) {
                        int i = fromOut.read();
                        if (i < 0) {
                            logOut.println(this + "fromOut returned " + i);
                            logOut.flush();
                            socketFromOut.close();
                            break;
                        }
                        toUp.write(i);
                    }
                    try {
                        socketFromOut.close();
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
            if (socketFromUp != null) {
                try {
                    socketFromUp.close();
                } catch (Exception e) {
                }
            }
            if (socketFromOut != null) {
                try {
                    socketFromOut.close();
                } catch (Exception e) {
                }
            }
            logOut.println("Thread for socket " + socketFromUp + this + " is finished");
            logOut.flush();
        }
    }
}
