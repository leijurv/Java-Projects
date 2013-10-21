/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backgammon;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author leijurv
 */
public class Board {
    boolean[] color=new boolean[24];
    int[] amt=new int[24];
    int[] injail=new int[2];
    int[] inEnd=new int[2];
    boolean move=false;
    public Board(boolean[] Color, int[] Amt){
        color=Color;
        amt=Amt;
    }
    public Board(Board b){
        move=b.move;
        for (int i=0; i<2; i++){
            injail[i]=b.injail[i];
            inEnd[i]=b.inEnd[i];
        }
        for (int i=0; i<24; i++){
            color[i]=b.color[i];
            amt[i]=b.amt[i];
        }
    }
    public void show(Graphics g){
        int startx=50;
        int circlewidth=10;
        int circlespacing=10;
        int starty=100;
        int endy=300;
        for (int i=0; i<injail[0]; i++){
            int y=starty+endy;
            y/=2;
            g.fillOval(i*(circlewidth+circlespacing), y, circlewidth, circlewidth);
        }
        for (int i=0; i<24; i++){
            int x=startx+((i<12?11:0)+(i<12?-1:1)*(i%12))*(circlewidth+circlespacing);
            g.setColor(Color.blue);
                if (!Backgammon.dice.isEmpty() && canMove(i,Backgammon.dice.get(Backgammon.selected),move))
                    g.setColor(Color.red);
                 g.drawString(i+"",x,i<12?starty-20:endy+20);
            if (amt[i]!=0){
                
                
                if (color[i]){
                    g.setColor(Color.white);
                }else{
                    g.setColor(Color.black);
                }
                for (int j=0; j<amt[i]; j++){
                    int y=i<12?starty:endy;
                    y=y+(i<12?1:-1)*j*(circlewidth+circlespacing);
                    g.fillOval(x,y,circlewidth,circlewidth);
                }
            }
        }
        int x=500;
        int y=200;
        if (!Backgammon.dice.isEmpty()){
        g.setColor(Color.blue);
        
        g.drawRect(x-5,(Backgammon.selected+1)*15-30+y,15,15);
        g.setColor(Color.black);
    for (int i=0; i<Backgammon.dice.size(); i++){
            g.drawString(Backgammon.dice.get(i)+"",x,15*i+y);
        }
        }
        
    }
    public boolean canMove(int pos, int dist, boolean player){
        if(color[pos]^player){
            return false;
        }
        if (amt[pos]==0){
            return false;
        }
        int newPos=pos+(player?dist:-dist);
        if (newPos<0 || newPos>23){
            return false;
        }
        if (amt[newPos]==0||amt[newPos]==1){
            return true;
        }
        if (!(color[newPos]^player)){
            return true;
        }
        return false;
    }
}
