/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spades;

/**
 *
 * @author program
 */
public class Player {
    Card[] hand;
    int bid;
    int tricks_taken;
    public Player(){
        hand=new Card[0];
        tricks_taken=0;
    }
    public void Bid(int Bid){
        bid=Bid;
    }
    public void sortCardsBySuit(){
        Card[] hearts=new Card[0];
        Card[] diamonds=new Card[0];
        Card[] clubs=new Card[0];
        Card[] spades=new Card[0];
        for (int i=0; i<hand.length; i++){
            switch(hand[i].suit){
                case 0:
                    hearts=add(hearts,hand[i]);
                break;
                case 1:
                    diamonds=add(diamonds,hand[i]);
                break;
                case 2:
                    clubs=add(clubs,hand[i]);
                break;
                default:
                    spades=add(spades,hand[i]);
                break;
            }
        }
        Card[] sorted=new Card[0];
        Card[] c=hearts;
        for (int i=0; i<c.length; i++){
            sorted=add(sorted,c[i]);
        }
        c=diamonds;
        for (int i=0; i<c.length; i++){
            sorted=add(sorted,c[i]);
        }
        c=clubs;
        for (int i=0; i<c.length; i++){
            sorted=add(sorted,c[i]);
        }
        c=spades;
        for (int i=0; i<c.length; i++){
            sorted=add(sorted,c[i]);
        }
        hand=sorted;
    }
    public Card[] add(Card[] a, Card b){
        Card[] t=new Card[a.length+1];
        for (int i=0; i<a.length;i++){
            t[i]=a[i];
        }
        t[a.length]=b;
        return t;
    }
    public void recieveCard(Card acardOMG){
        Card[] theHand=hand;
        Card[] t=new Card[theHand.length+1];
        for (int i=0; i<theHand.length; i++){
            t[i]=theHand[i];
        }
        t[theHand.length]=acardOMG;
        hand=t;
    }
    public Card[] remove(Card[] a, int index){
        Card[] b=new Card[0];
        for (int i=0; i<index; i++){
            b=add(b,a[i]);
        }
        for (int i=index+1; i<a.length; i++){
            b=add(b,a[i]);
        }
        return b;
    }
    public boolean playCard(int cardIndex){
        Game.currentGame.currentHand=add(Game.currentGame.currentHand,hand[cardIndex]);
        hand=remove(hand,cardIndex);
        if (Game.currentGame.currentHand.length==4){
            int winner=((Game.findPlayerWonCurrentTrick()+(Game.currentGame.nextPlayerToPlay+1)%4)+1)%4-1;
            System.out.println("Player "+Integer.toString((winner+1))+" won this hand.");
            Game.players[winner].tricks_taken++;
            Game.currentGame.nextPlayerToPlay=winner;
            Game.currentGame.currentHand=new Card[0];
            return true;
        }else{
            Game.currentGame.nextPlayerToPlay=(Game.currentGame.nextPlayerToPlay+1)%4;
        }
        return false;
    }
    public boolean isValidPlay(int cardIndex){
        if (Game.currentGame.currentHand.length==0){
            return true;
        }
        if (hand.length<=cardIndex){
            return false;
        }
        Card[] Hand=Game.currentGame.currentHand;
        Card opening=Hand[0];
        int openingSuit=opening.suit;
        if (hand[cardIndex].suit!=openingSuit){
            for (int i=0; i<hand.length; i++){
                if (hand[i].suit==openingSuit){
                    return false;
                }
            }
            return true;
        }
        return true;
    }
        
}
