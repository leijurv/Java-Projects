/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prim;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Vertex {
    int id;
    ArrayList<Edge> connected=new ArrayList<Edge>();
    public Vertex(int Id, ArrayList<Edge> con){
        id=Id;
        connected=con;
    }
    public boolean equal(Object o){
        if (o instanceof Vertex){
            Vertex v=(Vertex)o;
            return id==v.id;
        }
        return false;
    }
    public String toString(){
        return ""+id;
    }
}
