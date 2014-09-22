/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khet;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author leijurv
 */
public class Piece {

    public enum Dir {

        UP, DOWN, LEFT, RIGHT
    }

    public enum PieceType {

        PYRAMID, SCARAB, OBELISK, DOUBLEOBELISK, PHAROH
    }

    public enum Direction {

        UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
    }
    int x;
    int y;
    PieceType type;
    Direction direction;
    boolean silver;

    public Piece(Piece p){
        x=p.x;
        y=p.y;
        type=p.type;
        direction=p.direction;
        silver=p.silver;
    }

    public Piece(int X,int Y,PieceType Type,Direction Direction,boolean Silver){
        type=Type;
        x=X;
        y=Y;
        direction=Direction;
        silver=Silver;
    }

    public void draw(Graphics g,int x,int y,int size){
        g.setColor(silver ? Color.LIGHT_GRAY : Color.RED);
        switch (type){
            case PHAROH:
                g.drawLine(x+size/2,y,x,y+size/2);
                g.drawLine(x+size/2,y,x+size,y+size/2);
                g.drawLine(x+size/2,y+size,x,y+size/2);
                g.drawLine(x+size/2,y+size,x+size,y+size/2);
                break;
            case DOUBLEOBELISK:
                int X=x+size/8;
                int Y=y+size/8;
                int Size=3*size/4;
                g.drawLine(X,Y,X+Size,Y);
                g.drawLine(X,Y+Size,X+Size,Y+Size);
                g.drawLine(X+Size,Y,X+Size,Y+Size);
                g.drawLine(X,Y,X,Y+Size);
            case OBELISK:
                g.drawLine(x,y,x+size,y);
                g.drawLine(x,y+size,x+size,y+size);
                g.drawLine(x+size,y,x+size,y+size);
                g.drawLine(x,y,x,y+size);
                g.drawLine(x,y,x+size,y+size);
                g.drawLine(x,y+size,x+size,y);
                break;
            case PYRAMID:
                if (equiv(direction,Dir.DOWN)){
                    g.drawLine(x,y,x+size,y);
                }
                if (equiv(direction,Dir.UP)){
                    g.drawLine(x,y+size,x+size,y+size);
                }
                if (equiv(direction,Dir.LEFT)){
                    g.drawLine(x+size,y,x+size,y+size);
                }
                if (equiv(direction,Dir.RIGHT)){
                    g.drawLine(x,y,x,y+size);
                }
            case SCARAB:
                if (direction==Direction.DOWNLEFT||direction==Direction.UPRIGHT){
                    g.drawLine(x,y,x+size,y+size);
                }else{
                    g.drawLine(x+size,y,x,y+size);
                }
                break;
        }
    }

    public boolean equiv(Direction a,Dir b){
        switch (b){
            case UP:
                if (a==Direction.UPLEFT||a==Direction.UPRIGHT){
                    return true;
                }
                return false;
            case DOWN:
                if (a==Direction.DOWNLEFT||a==Direction.DOWNRIGHT){
                    return true;
                }
                return false;
            case LEFT:
                if (a==Direction.UPLEFT||a==Direction.DOWNLEFT){
                    return true;
                }
                return false;
            case RIGHT:
                if (a==Direction.DOWNRIGHT||a==Direction.UPRIGHT){
                    return true;
                }
                return false;
        }
        return false;
    }

    public Direction opposite(Direction d){
        switch (d){
            case UPLEFT:
                return Direction.DOWNRIGHT;
            case UPRIGHT:
                return Direction.DOWNLEFT;
            case DOWNLEFT:
                return Direction.UPRIGHT;
            case DOWNRIGHT:
                return Direction.UPLEFT;
        }
        return null;
    }

    public static Dir opposite(Dir d){
        switch (d){
            case UP:
                return Dir.DOWN;
            case RIGHT:
                return Dir.LEFT;
            case DOWN:
                return Dir.UP;
            case LEFT:
                return Dir.RIGHT;
        }
        return null;
    }

    public Dir turn(Dir d,Direction dir){
        Dir[] dirs={Dir.UP,Dir.DOWN,Dir.LEFT,Dir.RIGHT};
        for (int i=0; i<dirs.length; i++){
            if (dirs[i]!=d&&equiv(dir,dirs[i])){
                return dirs[i];
            }
        }
        return null;
    }

    public Dir laser(Dir d){
        switch (type){
            case PHAROH:
            case OBELISK:
            case DOUBLEOBELISK:
                return null;
            case SCARAB:
                System.out.println(direction);
                System.out.println(d);
                System.out.println(opposite(direction));
                System.out.println(equiv(opposite(direction),d));
                if (equiv(opposite(direction),d)){
                    return turn(d,opposite(direction));
                }
            case PYRAMID:
                System.out.println("p"+direction);
                System.out.println("p"+d);
                if (equiv(direction,d)){
                    return turn(d,direction);
                }
        }
        return null;
    }

    public boolean rotateLeft(){
        if (direction==null){
            return false;
        }
        switch (direction){
            case UPLEFT:
                direction=Direction.DOWNLEFT;
                return true;
            case DOWNLEFT:
                direction=Direction.DOWNRIGHT;
                return true;
            case DOWNRIGHT:
                direction=Direction.UPRIGHT;
                return true;
            case UPRIGHT:
                direction=Direction.UPLEFT;
                return true;
        }
        return false;
    }

    public boolean rotateRight(){
        if (direction==null){
            return false;
        }
        switch (direction){
            case UPLEFT:
                direction=Direction.UPRIGHT;
                return true;
            case DOWNLEFT:
                direction=Direction.UPLEFT;
                return true;
            case DOWNRIGHT:
                direction=Direction.DOWNLEFT;
                return true;
            case UPRIGHT:
                direction=Direction.DOWNRIGHT;
                return true;
        }
        return false;
    }
    public Piece shot(){
     switch(type){
         case OBELISK:
         case PHAROH:
             //LOL GAME IS OVER
         case PYRAMID:
             return null;
         case DOUBLEOBELISK:
             return new Piece(x,y,PieceType.OBELISK,null,silver);
         
     }
     return this;
     }
}
