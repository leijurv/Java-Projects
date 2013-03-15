/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkestra1;
import java.util.*;
/**
 *
 * @author leif
 */
public class Point {
    int x;
    int y;
    int dist=Integer.MAX_VALUE;
    boolean visited;
    Point prev;
    public Point(int X, int Y){
        x=X;
        y=Y;
        visited=false;
        prev=null;
    }
    public boolean equals(Object o){
        if (!(o instanceof Point)){
            return false;
        }
        return x==((Point)o).x && y==((Point)o).y;
    }
    public List<Point> N(){
        List<Point> result=new ArrayList<Point>();
        for (int dx=-1; dx<=1; dx++){
            for (int dy=-1; dy<=1; dy++){
                if ((dx==1&&dy==1) || (dx==-1&&dy==1) ||(dx==-1&&dy==-1) ||(dx==1&&dy==-1) || (dx==-1&&dy==0)){//Last check to prevent left, remove for #83
                    
                }else{
                int X=x+dx;
                int Y=y+dy;
                if (X<0||X>79||Y<0||Y>79){
                    
                }else{
                    result.add(Dijkestra1.Matrix[X][Y]);
                }
            }}
        }
        return result;
    }
    public String toString(){
        return "("+x+","+y+")";
    }
}
