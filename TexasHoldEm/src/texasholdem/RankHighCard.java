/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;
/**
 *
 * @author leijurv
 */
public class RankHighCard extends Rank{
    private int rank;
    public RankHighCard(int Rank){
        rank=Rank;
    }
    public int getRank(){
        return rank;
    }
    public int getID() {
        return 0;
    }
    public String toString(){
    return "High card of "+(new Card(0,rank)).Rank();
}
}