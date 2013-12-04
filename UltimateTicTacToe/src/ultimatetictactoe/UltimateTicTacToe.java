/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe;

/**
 *
 * @author leijurv
 */
public class UltimateTicTacToe {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Board b=new Board();
        b.mb[1].d=new int[]{1,0,0,0,0,0,0,0,0};
        System.out.println(b);
        b.move=false;
        System.out.println(b.solve(0));
    }
}
