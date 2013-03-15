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
public class Dijkstra_Base {
    Map<Position,Node> map=new HashMap<Position,Node>();
    ArrayList<Node> openSet=new ArrayList<Node>();
    int maxDist;//Maximum distance searchable (Nodes with dist>maxDist return empty set for neighbors), -1 if infinite
    Node endNode;
    boolean depth_first;
    boolean e;//Exit on solution found
    Node startNode;
    boolean solution_found=false;
    public Dijkstra_Base(Position start, Position goal, int max_dist, boolean Depth_first, boolean Exit_on_path_found){
        //startNode=new Node(start,this);
        Node startNode=new Node(start,this);
        startNode.visited=true;
        startNode.dist=0;
        openSet.add(startNode);
        map.put(start, startNode);
        endNode=new Node(goal,this);
        depth_first=Depth_first;
        openSet.add(startNode);
        e=Exit_on_path_found;
        maxDist=max_dist;
        map.put(goal, endNode);
    }
    public boolean done(){
        synchronized(openSet){
            synchronized((Object)solution_found){
                boolean b=(!solution_found && !openSet.isEmpty());
                return b;
            }
        }
    }
    
    public void doIteration(){
        int smallest = Integer.MAX_VALUE;
        int index = 0;
        Node u;
        synchronized (openSet) {
            for (int i = 0; i < openSet.size(); i++) {
                
                if (openSet.get(i) != null && openSet.get(i).visited) {
                    if ((openSet.get(i).dist > smallest && depth_first) || (openSet.get(i).dist < smallest && !depth_first)) {
                        smallest = openSet.get(i).dist;
                        index = i;
                    }
                }
                if (openSet.get(i)==null){
                    openSet.remove(i);
                }
            }
            u = openSet.get(index);
            openSet.remove(index);
        }
        synchronized (u) {
            //System.out.println(u.pos);
            System.out.print(u.pos);
            System.out.print(";");
            System.out.println(u.dist);
            if (u.equals(endNode) && e) {
                solution_found=true;
                return;
            }
            List<Position> N = u.neighbors();
            if (!N.isEmpty()) {
                ArrayList<Node> n = new ArrayList<Node>();
                for (Position x : N) {
                    synchronized (map) {
                        if (!map.containsKey(x)) {
                            Node y = new Node(x,this);
                            map.put(x, y);
                            n.add(y);
                        } else {
                            n.add(map.get(x));
                        }
                    }

                }
                for (Node x : n) {
                    synchronized (x) {
                        int alt = u.dist+u.pos.cost(x.pos);
                        boolean add = false;
                        if (x.visited) {
                            if (x.dist > alt) {
                                x.dist = alt;
                                x.prev = u;
                                add = true;
                            }
                        } else {
                            x.dist = alt;
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
}