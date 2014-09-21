/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khet;

import java.awt.Color;
import java.awt.Graphics;
import khet.Piece.Dir;
import khet.Piece.Direction;
import khet.Piece.PieceType;

/**
 *
 * @author leijurv
 */
public class Board {

    Piece[][] board=new Piece[10][8];

    public Board(Board b){
        for (int x=0; x<10; x++){
            for (int y=0; y<8; y++){
                if (b.board[x][y]!=null){
                    board[x][y]=new Piece(b.board[x][y]);
                }
            }
        }
    }

    public Board(){
        set(0,3,PieceType.PYRAMID,Direction.DOWNRIGHT,false);
        set(0,4,PieceType.PYRAMID,Direction.UPRIGHT,false);
        set(2,0,PieceType.PYRAMID,Direction.UPLEFT,true);
        set(2,3,PieceType.PYRAMID,Direction.UPLEFT,true);
        set(2,4,PieceType.PYRAMID,Direction.DOWNLEFT,true);
        set(2,6,PieceType.PYRAMID,Direction.DOWNLEFT,false);
        set(3,0,PieceType.DOUBLEOBELISK,null,true);
        set(3,5,PieceType.PYRAMID,Direction.UPLEFT,true);
        set(4,0,PieceType.PHAROH,null,true);
        set(4,3,PieceType.SCARAB,Direction.UPLEFT,true);
        set(4,4,PieceType.SCARAB,Direction.DOWNLEFT,false);
        set(4,7,PieceType.DOUBLEOBELISK,null,false);
        set(5,0,PieceType.DOUBLEOBELISK,null,true);
        set(5,3,PieceType.SCARAB,Direction.DOWNLEFT,true);
        set(5,4,PieceType.SCARAB,Direction.DOWNRIGHT,false);
        set(5,7,PieceType.PHAROH,null,false);
        set(6,2,PieceType.PYRAMID,Direction.DOWNRIGHT,false);
        set(6,7,PieceType.DOUBLEOBELISK,null,false);
        set(7,1,PieceType.PYRAMID,Direction.UPRIGHT,true);
        set(7,3,PieceType.PYRAMID,Direction.UPRIGHT,false);
        set(7,4,PieceType.PYRAMID,Direction.DOWNRIGHT,false);
        set(7,7,PieceType.PYRAMID,Direction.DOWNRIGHT,false);
        set(9,3,PieceType.PYRAMID,Direction.DOWNLEFT,true);
        set(9,4,PieceType.PYRAMID,Direction.UPLEFT,true);
    }

    public final void set(int x,int y,PieceType pt,Direction d,boolean silver){
        board[x][y]=new Piece(x,y,pt,d,silver);
    }

    public void draw(Graphics g,int dx,int dy){
        int size=64;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(dx,dy,size*11,size*9);
        g.setColor(Color.BLACK);
        g.drawLine(10*size+dx+size/2,dy+size/2,10*size+dx+size/2,8*size+dy+size/2);
        for (int X=0; X<10; X++){
            g.setColor(Color.BLACK);
            g.drawLine(X*size+dx+size/2,dy+size/2,X*size+dx+size/2,8*size+dy+size/2);
            if (X<9){
                g.drawLine(dx+size/2,dy+X*size+size/2,10*size+dx+size/2,X*size+dy+size/2);
            }
            for (int Y=0; Y<8; Y++){
                int x=X*size+dx+size/2;
                int y=(7-Y)*size+dy+size/2;
                if (board[X][Y]!=null){
                    board[X][Y].draw(g,x+size/8,y+size/8,3*size/4);
                }else{
                    //g.setColor(Color.BLACK);
                    //g.fillRect(x+size/8,y+size/8,3*size/4,3*size/4);
                }
                //g.drawString(X+","+Y,x+size/2,y+size/2);
            }
        }
    }

    public Piece laser(Graphics g,int dx,int dy,int x,int y,Dir d){
        if (x<0||x>9||y<0||y>7){
            return new Piece(x,y,null,null,false);
        }
        int size=64;
        if (board[x][y]==null){
            int[] nxt=next(x,y,d);
            g.setColor(Color.BLUE);
            g.drawLine(x*size+dx+size,(7-y)*size+dy+size,nxt[0]*size+dx+size,(7-nxt[1])*size+dy+size);
            return laser(g,dx,dy,nxt[0],nxt[1],d);
        }
        Piece p=board[x][y];
        Dir ne=(p.laser(Piece.opposite(d)));
        if (ne==null){
            return p;
        }
        int[] nxt=next(x,y,ne);
        g.drawLine(x*size+dx+size,(7-y)*size+dy+size,nxt[0]*size+dx+size,(7-nxt[1])*size+dy+size);
        return laser(g,dx,dy,nxt[0],nxt[1],ne);
    }

    public int[] next(int x,int y,Dir d){
        switch (d){
            case UP:
                return new int[]{x,y+1};
            case DOWN:
                return new int[]{x,y-1};
            case LEFT:
                return new int[]{x-1,y};
            case RIGHT:
                return new int[]{x+1,y};
        }
        return null;
    }
}
