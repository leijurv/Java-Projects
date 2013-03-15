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
public abstract class Position {
    public abstract boolean equals(Object obj);
    public abstract List<Position> getNeighbors();
    public abstract int cost(Position neighbor);
    public abstract String toString();
    public abstract int hashCode();
    /*
     * Make SURE that if obj1.equals(obj2) then obj1.hashCode()==obj2.hashCode()! 
     * (It's even better if those those two statements are equal)
     * This is REQUIRED, otherwise HashMap doesn't work! 
     * (Getting from a key will fail if the hashcode that the key was put as is different than the one it is being gotton from)
     * (It's just the way hashmap works)
     */
}
