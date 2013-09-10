/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

/**
 *
 * @author leif
 */
public class Node {
    String name;
    int id;
    double x;
    double y;
    Graph M;
    ArrayList<Edge> connections;
    double tensionX;
    double tensionY;
    public Node(String Name, int Id, double X, double Y, Graph m){
        name=Name;
        id=Id;
        x=X;
        y=Y;
        M=m;
        connections=new ArrayList<Edge>();
        for (Edge e : m.edges){
            System.out.println(e.nodeID1+","+e.nodeID2+":"+id);
            if (e.nodeID1==id || e.nodeID2==id){
                connections.add(e);
            }
        }
        //System.out.println(connections);
    }
    public Node getOther(Edge e){
        return M.nodes.get(e.nodeID1==id?e.nodeID2:e.nodeID1);
    }
    public double getDist(Node n){
        return Math.sqrt((x-n.x)*(x-n.x)+(y-n.y)*(y-n.y));
    }
    public void calculateTension(){
        if (connections.size()==1){
            System.out.println("MEOW");
            Node other=getOther(connections.get(0));
            double distX=other.x-x;
            double distY=other.y-y;
            double dist=connections.get(0).length;
            double curDist=Math.sqrt(distX*distX+distY*distY);
            System.out.println("dist"+dist+","+curDist);
            double W=dist/curDist-1;
            System.out.println("W"+W);
            tensionX=-distX*W;
            tensionY=-distY*W;
            return;
        }
        tensionX=0;
        tensionY=0;
        for (Edge e : connections){
            tensionX+=-e.tension/getDist(getOther(e))*(getOther(e).x-x);
            tensionY+=-e.tension/getDist(getOther(e))*(getOther(e).y-y);
        }
    }
    public void enactTension(){
        x+=tensionX;
        y+=tensionY;
    }
    public String toString(){
        return name+" ("+x+","+y+")";
    }
}
