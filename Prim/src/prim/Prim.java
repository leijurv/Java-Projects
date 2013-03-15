/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prim;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author leijurv
 */
public class Prim {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        long l=System.currentTimeMillis();
        Queue<Edge> S=new PriorityQueue<Edge>();//All edges
        Graph g=Graph.g;//The graph, imported in a different file
        for (Vertex x : g.verticies){
            for (Edge e : x.connected){
                if (!S.contains(e)){
                    S.add(e);
                }
            }
        }
        ArrayList<ArrayList<Integer>> F=new ArrayList<ArrayList<Integer>>();//Forest with each vertex being a seperate tree in the beginning
        for (Vertex x : g.verticies){
            ArrayList<Integer> f=new ArrayList<Integer>(1);
            f.add(x.id);
            F.add(f);
        }
        int totalSum=0;
        int minimalSum=0;
        while(!S.isEmpty()){
            Edge e=S.remove();//Get least weighted edge
            Vertex a=g.verticies.get(e.vertexA);
            Vertex b=g.verticies.get(e.vertexB);//The two verticies it connected
            totalSum+=e.cost;
            int aindex=-1;
            for (int i=0; i<F.size(); i++){
                if (F.get(i).contains(a.id)){
                    aindex=i;
                }
            }
            int bindex=-1;
            for (int i=0; i<F.size(); i++){
                if (F.get(i).contains(b.id)){
                    bindex=i;
                }
            }
            if (aindex!=bindex){//If they are in different trees
                F.get(aindex).addAll(F.get(bindex));
                F.remove(bindex);
                minimalSum+=e.cost;
            }
        }
        System.out.println(totalSum-minimalSum);
        System.out.println(System.currentTimeMillis()-l);
    }
    
    
}
