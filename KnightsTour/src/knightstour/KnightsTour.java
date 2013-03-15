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
public class KnightsTour {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Position> y=new ArrayList<Position>(1);
        y.add(new Position(0,0));
        y.add(new Position(2,1));
        System.out.println((new Board(y)).solve());
        // TODO code application logic here
    }
}
