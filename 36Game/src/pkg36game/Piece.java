/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg36game;

/**
 *
 * @author leijurv
 */
public class Piece {
    int color;
    int height;
    public Piece(int Height,int Color){
        color=Color;
        height=Height;
    }
    public String toString(){
        return color+","+height;
    }
    public boolean equals(Object o){
        if (o instanceof Piece){
            return ((Piece)o).color==color  && ((Piece)o).height==height;
        }
        return false;
    }
}
