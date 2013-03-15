/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra_base;
import java.util.*;
/**
 *
 * @author leif
 */
public class Node {
    Position pos;
    Node prev;
    int dist;
    boolean visited;
    Dijkstra_Base D;
    public Node(Position position, Dijkstra_Base db){
        pos=position;
        D=db;
    }
    public List<Position> neighbors(){
        if (D.maxDist!=-1 && dist>D.maxDist){
            return new ArrayList<Position>();
        }
        List<Position> N=pos.getNeighbors();
        if (prev!=null){
            N.remove(prev.pos);
        }
        return N;
    }
    public String toString(){
        return "Distance: "+dist+", Position: "+pos.toString();
    }
    public boolean equals(Object o){
        if (o instanceof Node){
            Node n=(Node)o;
            return n.pos.equals(pos);
        }
        return false;
    }
}
