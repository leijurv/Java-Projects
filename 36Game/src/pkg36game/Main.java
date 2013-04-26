/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg36game;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args){
        Board b=new Board();
        for (int x=5; x>=0; x--){
            for (int y=0; y<6; y++){
                System.out.print(Board.heights.get(x*6+y)+" ");
            }
            System.out.println();
        }
        b.leftover.remove(new Piece(1,2));
        b.taken.put(new Location(2,1),new Piece(1,2));
        
        b.leftover.remove(new Piece(0,1));
        b.taken.put(new Location(2,3),new Piece(0,1));
        System.out.println(b);
        ArrayList<Board> B=b.solve();
        System.out.println(B.size());
        for (Board BB : B){
            System.out.println(BB);
        }
    }
}
