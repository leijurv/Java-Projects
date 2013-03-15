/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankFourOfAKind extends Rank{
    private int Rank;
    public RankFourOfAKind(int rank){
        Rank=rank;
    }
    public int getRank(){
        return Rank;
    }
    public int getID(){
        return 7;
    }
    public String toString(){
        return "Four of "+new Card(0,Rank).Rank();
    }
}
