/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankThreeOfAKind extends Rank{
    private int Rank;
    public RankThreeOfAKind(int rank){
        Rank=rank;
    }
    public int getRank(){
        return Rank;
    }
    public int getID(){
        return 3;
    }
    public String toString(){
        return "Three of "+new Card(0,Rank).Rank();
}
}
