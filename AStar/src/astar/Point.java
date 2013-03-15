/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

import java.util.*;

/**
 *
 * @author leif
 */
public class Point extends AStarNode {

    int x;
    int y;

    public Point(int X, int Y) {
        x = X;
        y = Y;
    }

    @Override
    public float getCost(AStarNode node) {
        Point p = (Point) node;
        return 1;
    }

    public boolean equals(Object o) {
        return ((x == ((Point) o).x) && (y == ((Point) o).y));
    }

    @Override
    public float getEstimatedCost(AStarNode node) {
        //System.out.println("durp1");
        if (getNeighbors().contains(node)) {
            return getCost(node);
        }
        // System.out.println("im gettin it");
        return (float) Math.sqrt((x - ((Point) node).x) * (x - ((Point) node).x) + (y - ((Point) node).y) * (y - ((Point) node).y));

    }

    @Override
    public List getNeighbors() {
        boolean diagonal = false;
        List<Point> z = new ArrayList<Point>();
        Point i;
        if (diagonal) {
            z.add(new Point(x + 1, y + 1));
            i = z.get(z.size() - 1);

            if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
                z.remove(z.size() - 1);
            }
        }
        if (diagonal) {
            z.add(new Point(x + 1, y - 1));
            i = z.get(z.size() - 1);

            if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
                z.remove(z.size() - 1);
            }
        }
        z.add(new Point(x + 1, y));
        i = z.get(z.size() - 1);

        if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
            z.remove(z.size() - 1);
        }
        if (diagonal) {
            z.add(new Point(x - 1, y + 1));
            i = z.get(z.size() - 1);

            if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
                z.remove(z.size() - 1);
            }
        }
        if (diagonal) {
            z.add(new Point(x - 1, y - 1));
            i = z.get(z.size() - 1);

            if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
                z.remove(z.size() - 1);
            }
        }
        z.add(new Point(x - 1, y));
        i = z.get(z.size() - 1);

        if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
            z.remove(z.size() - 1);
        }
        z.add(new Point(x, y + 1));
        i = z.get(z.size() - 1);

        if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
            z.remove(z.size() - 1);
        }
        z.add(new Point(x, y - 1));


        i = z.get(z.size() - 1);

        if (Graph.current.blocked.contains(i) || i.x < Graph.current.XMin || i.x > Graph.current.XMax || i.y < Graph.current.YMin || i.y > Graph.current.YMax) {
            z.remove(z.size() - 1);
        }

        for (Point n : z) {
            //System.out.println(n);
        }
        return z;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
