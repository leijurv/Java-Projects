/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texasholdem;

/**
 *
 * @author leijurv
 */
public class RankFullHouse extends Rank{
    int rankOne;
    int rankTwo;
    public int[] getRanks(){
        return new int[] {rankOne,rankTwo};
    }
    public RankFullHouse(int a, int b){
        rankOne=a;
        rankTwo=b;
    }
    public int getID(){
        return 6;
    }
    public String toString(){
    return "Full house of "+(new Card(0,rankOne)).Rank()+" and "+(new Card(0,rankTwo)).Rank();
}
}

