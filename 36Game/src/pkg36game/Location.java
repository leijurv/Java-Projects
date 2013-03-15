/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg36game;

/**
 *
 * @author leijurv
 */
public class Location {
    int x;
    int y;
    public Location(int X, int Y){
        x=X;
        y=Y;
    }
    public int hashCode(){
        return x*6+y;
    }
public String toString(){
    return x+","+y;
}
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
        
}
