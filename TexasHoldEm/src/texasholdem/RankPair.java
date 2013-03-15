/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankPair extends Rank{
    private int Rank;
    public RankPair(int rank){
        Rank=rank;
    }
    public int getRank(){
        return Rank;
    }
    public int getID(){
        return 1;
    }
    public String toString(){
        return "Pair of "+new Card(0,Rank).Rank();
}
}
