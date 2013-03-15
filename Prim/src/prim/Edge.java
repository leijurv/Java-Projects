/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prim;

/**
 *
 * @author leijurv
 */
public class Edge implements Comparable{
    int cost;
    int vertexA;
    int vertexB;
    public Edge(int Cost, int VertexA, int VertexB){
        cost=Cost;
        vertexA=VertexA;
        vertexB=VertexB;
    }
    public boolean equals(Object o){
        if (o instanceof Edge){
            Edge e=(Edge)o;
            return (e.vertexA==vertexA && e.vertexB==vertexB) || (e.vertexA==vertexB && e.vertexB==vertexA);
        }
        return false;
    }
    public int getOther(int i){
        if (vertexB==i){
            return vertexA;
        }
        return vertexB;
    }

    @Override
    public int compareTo(Object t) {
        if (t instanceof Edge){
            int C=((Edge)t).cost;
            if (cost<C){
                return -1;
            }
            return 1;
        }
        return 0;
    }
    public String toString(){
        return vertexA+":"+cost+":"+vertexB;
    }
}
