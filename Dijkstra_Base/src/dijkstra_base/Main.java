/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstra_base;

/**
 *
 * @author leif
 */
public class Main {
    public static void main(String[] args){
        Dijkstra_Base db=new Dijkstra_Base(new Position2D(0,0),new Position2D(10,10),-1,false,false);
        
        while(db.done()){
            db.doIteration();
        }
    }
}
