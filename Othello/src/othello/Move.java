/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othello;

/**
 *
 * @author leijurv
 */
public class Move {
    Board result;
    int xPos;
    int yPos;
    public Move(Board Result, int X, int Y){
        result=Result;
        xPos=X;
        yPos=Y;
    }
    public String toString(){
        return "Move in ("+xPos+","+yPos+"), resulting in"+result.toString();
    }
}
