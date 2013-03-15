/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankStraight extends Rank{
    private int Rank;
    public RankStraight(int rank){
        Rank=rank;
    }
    public int getRank(){
        return Rank;
    }
    public int getID(){
        return 4;
    }
    public String toString(){
        return "Straight of "+new Card(0,Rank);
    }
}
