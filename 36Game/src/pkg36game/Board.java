/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg36game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author leijurv
 */
public class Board {
static int solutions=0;
    static final ArrayList<Integer> heights;
    ArrayList<Piece> leftover = new ArrayList<Piece>();
    HashMap<Location, Piece> taken = new HashMap<Location, Piece>();
    public Board() {
        for (int height = 0; height < 6; height++) {
            for (int color = 0; color < 6; color++) {
                leftover.add(new Piece(height, color));
            }
        }
    }
    public Board(Board b){
    taken=new HashMap<Location,Piece>(b.taken);
    leftover=new ArrayList<Piece>(b.leftover.size());
    for (Piece p : b.leftover){
        leftover.add(p);
    }
}
    public ArrayList<Location> possibilities() {
        Piece p = leftover.get(0);
        ArrayList<Location> possibilities = new ArrayList<Location>(35);
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 6; y++) {
                boolean fits = true;
                if (heights.get(x*6+y) != p.height) {
                    fits = false;
                } else {
                    if (taken.get(new Location(x, y)) != null) {
                        fits = false;
                    } else {
                        for (int test = 0; test < 6; test++) {
                            Piece T1 = taken.get(new Location(x, test));
                            Piece T2 = taken.get(new Location(test, y));
                            if (T1 != null) {
                                if (T1.color == p.color) {
                                    fits = false;
                                    break;
                                }
                            }
                            if (T2 != null) {
                                if (T2.color == p.color) {
                                    fits = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (fits) {
                    possibilities.add(new Location(x, y));
                }
            }
        }
        return possibilities;
    }
    char[] colors={'R','O','Y','G','B','P'};
    public String toString(){
        String result="";
        for (int x=5; x>=0; x--){
            result=result+":";
            for (int y=0; y<6; y++){
                result=result+(taken.get(new Location(x,y))==null?'-':colors[taken.get(new Location(x,y)).color]);
                result=result+" ";
            }
            result=result+"\n";
        }
        return result;
    }
    
    public ArrayList<Board> solve() {
        if (leftover.isEmpty()){
            ArrayList<Board> r=new ArrayList<Board>(1);
            r.add(this);
            return r;
        }
        if (leftover.size()==35){
            
            System.out.print(this);
            for (Piece p : leftover){
                System.out.print(p+" ");
            }
            System.out.println();
            System.out.println();
             
        }
        ArrayList<Location> pos=possibilities();
        ArrayList<Board> b=new ArrayList<Board>(pos.size());
        ArrayList<Board> result=new ArrayList<Board>();
        Piece X=leftover.get(0);
        for (Location l : pos){
            Board x=new Board(this);
            x.taken.put(l,X);
            x.leftover.remove(0);
            b.add(x);
            ArrayList<Board> B=x.solve();
            if (!B.isEmpty()){
                result.addAll(B);
            }
        }
        return result;
    }
    
    static {
        heights=new ArrayList<Integer>();
        heights.add(1);
        heights.add(2);
        heights.add(0);
        heights.add(5);
        heights.add(4);
        heights.add(3);
        
        heights.add(3);
        heights.add(5);
        heights.add(1);
        heights.add(4);
        heights.add(2);
        heights.add(0);

heights.add(4);
        heights.add(1);//SNEAKY looks like 0
        heights.add(3);
        heights.add(0);//SNEAKY looks like 1
        heights.add(5);
        heights.add(2);
heights.add(5);
        heights.add(4);
        heights.add(2);
        heights.add(3);
        heights.add(0);
        heights.add(1);
heights.add(2);
        heights.add(1);
        heights.add(5);
        heights.add(0);
        heights.add(3);
        heights.add(4);
        heights.add(0);
        heights.add(3);
        heights.add(4);
        heights.add(2);
        heights.add(1);
        heights.add(5);
    }
}
