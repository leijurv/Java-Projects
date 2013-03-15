/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankStraightFlush extends Rank{
    int rank;
    public int getRank(){
        return rank;
    }
    public RankStraightFlush(int Rank){
        rank=Rank;
    }
    public int getID(){
        return 8;
    }
    public String toString(){
        return "Straight flush of "+(new Card(0,rank)).Rank();
}
}
