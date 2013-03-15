/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knightstour;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Board {
    static final int size=7;
    ArrayList<Position> taken=new ArrayList<Position>();
    public Board(ArrayList<Position> x){
        for (Position p : x){
            taken.add(new Position(p.x,p.y));
        }
    }
    public ArrayList<Position> get(Position p){
        ArrayList<Position> possibles=new ArrayList<Position>();
        possibles.add(new Position(p.x+2,p.y+1));
        possibles.add(new Position(p.x+2,p.y-1));
        possibles.add(new Position(p.x-2,p.y+1));
        possibles.add(new Position(p.x-2,p.y-1));
        possibles.add(new Position(p.x+1,p.y+2));
        possibles.add(new Position(p.x+1,p.y-2));
        possibles.add(new Position(p.x-1,p.y+2));
        possibles.add(new Position(p.x-1,p.y-2));
        for (int i=0; i<possibles.size(); i++){
            if (possibles.get(i).x>=size || possibles.get(i).y>=size || possibles.get(i).x<0 || possibles.get(i).y<0){
                possibles.remove(i--);
            }
        }
        return possibles;
    }
    public Board solve(){
        if (taken.size()<10){
            System.out.println(this);
        }
        ArrayList<Position> p=get(taken.get(taken.size()-1));
        if (taken.size()==size*size){
            if (taken.get(taken.size()-1).equals(new Position(1,2)) || taken.get(taken.size()-1).equals(new Position(2,1))){
                System.out.println(this);
                System.out.println(1+p.size());
                System.out.println(p.get(p.size()-1));
                return this;
            }
            return null;
        }
        for (int i=0; i<p.size(); i++){
            if (taken.contains(p.get(i))){
                p.remove(i--);
            }
        }
        for (Position P : p){
            Board b=new Board(taken);
            b.taken.add(P);
            Board B=b.solve();
            if (B!=null){
                return B;
            }
        }
        return null;
    }
    public String toString(){
        String result="";
        for (int x=0; x<size; x++){
            for(int y=0; y<size; y++){
                System.out.print(1+taken.indexOf(new Position(x,y)));
                System.out.print(" ");
            }
            System.out.println();
        }
        return result;
    }
}
