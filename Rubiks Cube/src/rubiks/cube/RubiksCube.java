/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rubiks.cube;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author leif
 */
public class RubiksCube extends Thread {

    /**
     * @param args the command line arguments
     */
    static Map<CubePosition, Node> map;
    static ArrayList<Node> openSet;
    static Node endNode;
    static ViewScreen VS = new ViewScreen();
    static int currentMoves = 0;

    public static void main(String[] args) {
        startScreen();
        CubePosition start = new CubePosition();
        start.scramble(50);
        init(start);
        Thread repaint = new rePaint();
        repaint.start();

        (new RubiksCube()).start();
        (new RubiksCube()).start();
        (new RubiksCube()).start();
        (new RubiksCube()).start();
    }

    public static class rePaint extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RubiksCube.class.getName()).log(Level.SEVERE, null, ex);
                }
                VS.repaint();
            }
        }
    }
    @Override
    public void run() {
        while (!isEmpty()) {
            doLoop();
        }
    }
    public static void startScreen() {
        JFrame frame = new JFrame("Rubiks Cube");
        VS.setFocusable(true);
        (frame).setContentPane(VS);
        frame.setLayout(new FlowLayout());
        frame.setSize(1000, 700);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    public static void init(CubePosition startPos) {
        map = new HashMap<CubePosition, Node>();
        Node start = new Node(startPos);
        start.visited = true;
        start.moves = 0;
        map.put(startPos, start);
        CubePosition end = new CubePosition();
        Node End = new Node(end);
        map.put(end, End);
        endNode = End;
        openSet = new ArrayList<Node>();
        openSet.add(start);
    }
    public static void doLoop() {
        byte smallest = -1;
        short index = 0;
        Node u;
        synchronized (openSet) {
            for (short i = 0; i < openSet.size(); i++) {
                if (openSet.get(i) != null && openSet.get(i).visited) {
                    if (openSet.get(i).moves > smallest) {
                        smallest = openSet.get(i).moves;
                        index = i;
                    }
                }
            }
            u = openSet.get(index);
            openSet.remove(index);
            if (smallest == 1) {
                System.out.println("MEOW!");
            }
        }
        synchronized (u) {
            if (u.position.isSolved()) {
                return;
            }
            synchronized ((Object) currentMoves) {
                currentMoves = u.moves;
            }
            ArrayList<CubePosition> N = u.neighbors();
            if (!N.isEmpty()) {
                ArrayList<Node> n = new ArrayList<Node>();
                for (CubePosition x : N) {
                    synchronized (map) {
                        if (!map.containsKey(x)) {
                            Node y = new Node(x);
                            map.put(x, y);
                            n.add(y);
                        } else {
                            n.add(map.get(x));
                        }
                    }

                }
                for (Node x : n) {
                    synchronized (x) {
                        byte alt = u.moves;
                        alt++;
                        boolean add = false;
                        if (x.visited) {
                            if (x.moves > alt) {
                                x.moves = alt;
                                x.prev = u;
                                add = true;
                            }
                        } else {
                            x.moves = alt;
                            x.prev = u;
                            x.visited = true;
                            add = true;
                        }
                        if (add && !openSet.contains(x)) {
                            openSet.add(x);
                        }
                    }
                }
            }
        }
    }
    public static boolean isEmpty() {
        synchronized (openSet) {
            return openSet.isEmpty();
        }
    }
}
