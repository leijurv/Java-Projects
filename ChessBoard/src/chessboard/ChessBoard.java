/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chessboard;

/**
 *
 * @author leijurv
 */
public class ChessBoard {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long result=0;
        for (int n=1; n<=8; n++){
            for (int m=1; m<=8; m++){
                result=result+(((8-m)+1)*((8-n)+1));
                System.out.println(m+" "+n+" "+result+" "+(((8-m)+1)*((8-n)+1)));
            }
        }
        System.out.println(result);
        // TODO code application logic here
    }
}
