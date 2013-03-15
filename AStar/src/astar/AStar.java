/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author leif
 */
public class AStar extends JComponent implements MouseMotionListener, MouseListener, ActionListener {

    /**
     * @param args the command line arguments
     */
    static int S = 10;

    public static void eval() {

        Graph.current.solve();

        M.repaint();
    }

    public static void main(String[] args) {
        AStarNode a = new Point(0, 0);
        AStarNode b = new Point(6, 5);
        ArrayList<AStarNode> c = new ArrayList<AStarNode>();
        Graph g = new Graph(c, a, b, -20, 20, -20, 20);//Bounds: -20,20 and -20,20
        Graph.current = g;
        setup();
        eval();

    }

    public static JButton button(String text, AStar th) {
        JButton b1 = new JButton(text);
        b1.setVerticalTextPosition(AbstractButton.CENTER);
        b1.setHorizontalTextPosition(AbstractButton.LEADING);
        b1.addActionListener(th);
        b1.setActionCommand(text);
        return b1;
    }
    boolean setEnd = false;
    boolean setStart = false;

    public AStar() {

        add(button("Clear", this));
        add(button("Set Start", this));
        add(button("Set End", this));
        add(button("Help", this));
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (help) {
            int i = 10;
            g.drawString("Click or click and drag to add a barrier (blue).", 10, i);
            i += 15;
            g.drawString("Click Set Start/End then click somewhere on the graph", 10, i);
            i += 15;
            g.drawString("to set the start/end of the black.", 0, i);

            i += 15;
            g.drawString("", 0, i);
        }
        for (int row = Graph.current.YMin; row <= Graph.current.YMax; row++) {
            for (int col = Graph.current.XMin; col <= Graph.current.XMax; col++) {
                int C = S * (row - Graph.current.YMin) + 80;
                int R = S * (col - Graph.current.XMin) + 80;
                g.drawRect(R, C, S, S);
            }
        }

        //Systeout.println(q);

        for (AStarNode b : Graph.current.path) {
            Point l = (Point) b;
            int R = S * (l.x - Graph.current.XMin) + 80;
            int C = S * (l.y - Graph.current.YMin) + 80;
            g.fillRect(R, C, S, S);
        }
        g.setColor(Color.BLUE);
        for (AStarNode l : Graph.current.blocked) {

            int R = S * (((Point) l).x - Graph.current.XMin) + 80;
            int C = S * (((Point) l).y - Graph.current.YMin) + 80;
            g.fillRect(R, C, S, S);
        }



    }
    static AStar M = new AStar();

    public static void setup() {

        JFrame frame = new JFrame("Interactive A* search in Java");

        M.setFocusable(true);
        (frame).setContentPane(M);
        frame.setLayout(new FlowLayout());
        frame.setSize(1000, 700);
        //frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    boolean add = true;
    boolean k = false;

    @Override
    public void mouseClicked(MouseEvent arg0) {
        //System.out.println("meow")
        int x = arg0.getX();
        int y = arg0.getY();
        x -= 80;
        y -= 80;
        x /= S;
        y /= S;
        x += Graph.current.XMin;
        y += Graph.current.YMin;
        //System.out.println("meow"+new Point(x,y));

        Point c = new Point(x, y);
        if (!setStart && !setEnd) {


            if (Graph.current.blocked.contains(c)) {
                Graph.current.blocked.remove(c);
            } else {
                Graph.current.blocked.add(new Point(x, y));
            }

        }
        //System.out.println(c);
        if (setStart) {
            Graph.current.startNode = c;

        }
        if (setEnd) {
            Graph.current.goalNode = c;
        }
        setStart = false;
        setEnd = false;
        eval();
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        k = false;
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Clear".equals(e.getActionCommand())) {
            Graph.current.blocked.clear();
        }
        if ("Set Start".equals(e.getActionCommand())) {
            setEnd = false;
            setStart = true;
        }
        if ("Set End".equals(e.getActionCommand())) {
            setEnd = true;
            setStart = false;
        }
        if ("Help".equals(e.getActionCommand())) {
            help = !help;

        }
        eval();
    }
    boolean help = false;

    @Override
    public void mouseDragged(MouseEvent arg0) {
        int x = arg0.getX();
        int y = arg0.getY();
        x -= 80;
        y -= 80;
        x /= S;
        y /= S;
        x += Graph.current.XMin;
        y += Graph.current.YMin;
        //System.out.println("meow"+new Point(x,y));

        Point c = new Point(x, y);
        if (!k) {

            k = true;
            add = !Graph.current.blocked.contains(c);
            //System.out.println(add);
        }
        //System.out.println(k);
        if (k && add && !Graph.current.blocked.contains(c)) {

            Graph.current.blocked.add(c);
            eval();
        } else {
            if (k && !add && Graph.current.blocked.contains(c)) {
                Graph.current.blocked.remove(c);
                eval();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}