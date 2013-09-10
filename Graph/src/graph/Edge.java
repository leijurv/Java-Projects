/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

/**
 *
 * @author leif
 */
public class Edge {
    int nodeID1;
    int nodeID2;
    Graph M=new Graph();
    double length;
    double tension;
    public void calculateTension(){
        tension=length-getCurLength();
        tension/=10;
    }
    public double getCurLength(){
        Node A=M.nodes.get(nodeID1);
        Node B=M.nodes.get(nodeID2);
        return Math.sqrt((A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y));
    }
    public Edge(int NodeID1, int NodeID2, Graph m, double Length){
        nodeID1=NodeID1;
        nodeID2=NodeID2;
        M=m;
        length=Length;
    }
    public String toString(){
                Node A=M.nodes.get(nodeID1);
        Node B=M.nodes.get(nodeID2);
        double curLength=Math.sqrt((A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y));
        return curLength+","+length;
    }
}
