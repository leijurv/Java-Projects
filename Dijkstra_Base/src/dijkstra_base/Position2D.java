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
public class Position2D extends Position{
    int x;
    int y;
    static final boolean diagonals=false;
    public Position2D(int X, int Y){
        x=X;
        y=Y;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position2D){
            Position2D pos=(Position2D)obj;
            return pos.x==x && pos.y==y;
            
        }
        return false;
    }

    @Override
    public List<Position> getNeighbors() {
        ArrayList<Position> res=new ArrayList<Position>();
        
            res.add(new Position2D(x-1,y));
       
        res.add(new Position2D(x,y+1));
        
        res.add(new Position2D(x+1,y));
        
        res.add(new Position2D(x,y-1));
        if (diagonals){
            res.add(new Position2D(x+1,y+1));
            res.add(new Position2D(x+1,y-1));
            res.add(new Position2D(x-1,y+1));
            res.add(new Position2D(x-1,y-1));
        }
        return res;
    }

    @Override
    public int cost(Position neighbor) {
        return 1;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
static final int q=(int)Math.sqrt(Integer.MAX_VALUE);
    @Override
    public int hashCode() {
        return q*x+y;
    }
}
