/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knightstour;

/**
 *
 * @author leijurv
 */
public class Position {
    int x;
    int y;
    public Position(int X, int Y){
        x=X;
        y=Y;
    }
    public boolean equals(Object o){
        if (!(o instanceof Position)){
            return false;
        }
        Position q=(Position)o;
        return x==q.x && y==q.y;
    }
    public String toString(){
        return "("+x+","+y+")";
    }
}
