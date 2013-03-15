/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euler155;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Point {
    int soFar;
    ArrayList<Point> next;
    public double resistance(){
        if (next.isEmpty()){
            return 0;
        }
        double d=0;
        for (Point p : next){
            d+=1/p.resistance();
        }
        return 1/d;
    }
    
}
