/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package knightstour;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Position {
    int x;
    int y;
    ArrayList<Neuron> connected=new ArrayList<Neuron>();
    public Position(int X, int Y){
        x=X;
        y=Y;
    }
    public boolean equals(Object o){
        if ( o instanceof Position){
            Position p=(Position)o;
            if (p.x==x&&p.y==y){
                return true;
            }
        }
        return false;
    }
    public String toString(){
        return "("+x+","+y+")";
    }
}
