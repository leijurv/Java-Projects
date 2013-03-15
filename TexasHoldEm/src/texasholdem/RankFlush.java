/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankFlush extends Rank{
    private int suit;
    public RankFlush(int Suit){
        suit=Suit;
    }
    public int getSuit(){
        return suit;
    }
    public int getID(){
        return 5;
    }
    public String toString(){
    return "Flush of "+new Card(suit,0).Suit();
}
}
