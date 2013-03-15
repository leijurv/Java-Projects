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
    public static void main(String[] args) {
        // TODO code application logic here
        String s="2, 24, 4320, 4680, 26208, 8910720, 17428320, 20427264, 91963648, 197064960, 8583644160, 10200236032, 21857648640, 57575890944, 57629644800, 206166804480, 17116004505600, 1416963251404800, 15338300494970880";
        BigInteger r=new BigInteger("0");
        for (String S : s.split(", ")){
            r=r.add(new BigInteger(S));
        }
        System.out.println(r);
    }
    public static void derp(){
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
        
        
        b.leftover.remove(new Piece(1,5));
        b.taken.put(new Location(0,0),new Piece(1,5));
        
        b.leftover.remove(new Piece(2,3));
        b.taken.put(new Location(0,1),new Piece(2,3));
        
        b.leftover.remove(new Piece(0,0));
        b.taken.put(new Location(0,2),new Piece(0,0));
        
        ArrayList<Board> B=b.solve();
        System.out.println(B.size());
        for (Board BB : B){
            System.out.println(BB);
        }
    }
}
